package net.kapitencraft.kap_lib.client.overlay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.overlay.box.ResizeBox;
import net.kapitencraft.kap_lib.client.overlay.holder.Overlay;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.event.ModEventFactory;
import net.kapitencraft.kap_lib.event.custom.client.RegisterConfigurableOverlaysEvent;
import net.kapitencraft.kap_lib.helpers.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * controls the location, and renders all registered overlays
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OverlayManager {
    /**
     * the Codec for saving
     */
    private static final Codec<OverlayManager> CODEC = RecordCodecBuilder.create(
            renderControllerInstance -> renderControllerInstance.group(
                    Codec.unboundedMap(ResourceLocation.CODEC, OverlayProperties.CODEC).fieldOf("storage").forGetter(OverlayManager::getLocations)
            ).apply(renderControllerInstance, OverlayManager::fromCodec)
    );

    private static OverlayManager fromCodec(Map<ResourceLocation, OverlayProperties> map) {
        OverlayManager controller = new OverlayManager();
        controller.loadedPositions.putAll(map);
        return controller;
    }

    public static void setVisible(Overlay overlay, boolean b) {
        OverlayManager manager = LibClient.overlays;
        if (b) {
            manager.visible.add(overlay);
            manager.invisible.remove(overlay);
        } else {
            manager.invisible.add(overlay);
            manager.visible.remove(overlay);
        }
    }

    /**
     * @return the positions of each overlay mapped to their UUID
     */
    private Map<ResourceLocation, OverlayProperties> getLocations() {
        return MapStream.of(map).mapValues(Overlay::getProperties).mapKeys(RegistryObject::getId).toMap();
    }

    /**
     * a holder for the File all information is saved in
     */
    private static File PERSISTENT_FILE;

    private static @NotNull File getOrCreateFile() {
        if (PERSISTENT_FILE == null) {
            PERSISTENT_FILE = new File(KapLibMod.MAIN, "overlay_config.json");
        }
        return PERSISTENT_FILE;
    }

    /**
     * load the overlay controller from it's dedicated file
     * @return the loaded controller
     */
    public static OverlayManager load() {
        return IOHelper.loadFile(getOrCreateFile(), CODEC, OverlayManager::new);
    }

    private final Map<RegistryObject<OverlayProperties>, Function<OverlayProperties, Overlay>> constructors = new HashMap<>();
    public final Map<RegistryObject<OverlayProperties>, Overlay> map = new HashMap<>();


    private final ConfigureOverlaysScreen screen = new ConfigureOverlaysScreen();
    private final List<Overlay> visible = new ArrayList<>(), invisible = new ArrayList<>();

    private final Map<ResourceLocation, OverlayProperties> loadedPositions = new HashMap<>();

    private OverlayManager() {
        this.register();
    }

    /**
     * fires the {@link RegisterConfigurableOverlaysEvent} for registering custom overlays
     */
    private void register() {
        OverlaysRegister.register(this);
        ModEventFactory.fireModEvent(new RegisterConfigurableOverlaysEvent(this::createRenderer));
        construct();
    }

    void createRenderer(RegistryObject<OverlayProperties> provider, Function<OverlayProperties, Overlay> constructor) {
        if (this.constructors.containsKey(provider))
            throw new IllegalStateException("detected double registered Overlay with ID '" + provider.getId() + "'");
        this.constructors.put(provider, constructor);
    }

    /**
     * save the Overlay locations to the corresponding file
     */
    public static void save() {
        IOHelper.saveFile(getOrCreateFile(), CODEC, LibClient.overlays);
    }

    /**
     * register the renderer
     */
    @SubscribeEvent
    public static void overlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("main", LibClient.overlays::render);
    }

    /**
     * create all overlay-relocation widgets and push them to the handling screen
     */
    public void fillRenderBoxes(Consumer<ResizeBox> acceptor, BiConsumer<Overlay, ResizeBox> map, LocalPlayer player, Font font, float width, float height) {
        this.visible.forEach(overlay -> {
            ResizeBox box = overlay.newBox(width, height, player, font);
            acceptor.accept(box);
            map.accept(overlay, box);
        });
    }

    /**
     * render all overlays
     */
    private void render(ForgeGui forgeGui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        LocalPlayer entity = Minecraft.getInstance().player;
        if (entity != null && !ClientHelper.hideGui()) {
            visible.forEach(renderHolder -> {
                graphics.pose().pushPose();
                OverlayProperties holder = renderHolder.getProperties();
                Vec2 renderLocation = renderHolder.getLoc(screenWidth, screenHeight);
                graphics.pose().translate(renderLocation.x, renderLocation.y, 0);
                graphics.pose().scale(holder.getXScale(), holder.getYScale(), 0);
                renderHolder.render(graphics, screenWidth, screenHeight, entity);
                graphics.pose().popPose();
            });
        }
    }

    /**
     * construct all Overlays into the active render queue
     */
    private void construct() {
        this.constructors.forEach((location, constructor) -> {
            OverlayProperties holder = MiscHelper.nonNullOr(this.loadedPositions.get(location.getId()), location.get().createCopy());
            Overlay overlay = constructor.apply(holder);
            if (holder.isVisible())
                visible.add(overlay);
            else
                invisible.add(overlay);
            this.map.put(location, overlay);
        });
    }

    /**
     * reset given Overlay to it's default location
     * <br> provided from inside {@link OverlayLocation}
     */
    @SuppressWarnings("all")
    public void reset(Overlay dedicatedHolder) {
        if (this.map.containsValue(dedicatedHolder)) {
            if (invisible.contains(dedicatedHolder)) {
                invisible.remove(dedicatedHolder);
                visible.add(dedicatedHolder);
            }
            RegistryObject<OverlayProperties> location = CollectionHelper.getKeyForValue(this.map, dedicatedHolder);
            dedicatedHolder.getProperties().copy(location.get());
            return;
        }
        throw new IllegalStateException("attempted to reset non-existing Holder");
    }

    /**
     * reset all Overlays
     */
    public static void resetAll() {
        OverlayManager controller = LibClient.overlays;
        Collection<RegistryObject<OverlayProperties>> locations = controller.constructors.keySet();
        locations.forEach(location -> {
            Overlay holder = controller.map.get(location);
            holder.getProperties().copy(location.get());
            controller.visible.add(holder);
        });
        controller.invisible.clear();
    }
}