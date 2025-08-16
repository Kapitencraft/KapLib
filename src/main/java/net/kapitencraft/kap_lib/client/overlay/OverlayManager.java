package net.kapitencraft.kap_lib.client.overlay;

import com.mojang.blaze3d.platform.Window;
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
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * controls the location, and renders all registered overlays
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
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
        return MapStream.of(map).mapValues(Overlay::getProperties).mapKeys(Holder::getKey).mapKeys(ResourceKey::location).toMap();
    }

    /**
     * a holder for the File all information is saved in
     */
    private static File PERSISTENT_FILE;

    private static @NotNull File getOrCreateFile() {
        if (PERSISTENT_FILE == null) {
            PERSISTENT_FILE = new File(KapLibMod.ROOT, "overlay_config.json");
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

    private final Map<Holder<OverlayProperties>, Function<OverlayProperties, Overlay>> constructors = new HashMap<>();
    public final Map<Holder<OverlayProperties>, Overlay> map = new HashMap<>();

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

    void createRenderer(Holder<OverlayProperties> provider, Function<OverlayProperties, Overlay> constructor) {
        if (this.constructors.containsKey(provider))
            throw new IllegalStateException("detected double registered Overlay with ID '" + provider.getKey().location() + "'");
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
    public static void overlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(KapLibMod.res("overlay"), LibClient.overlays::render);
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
    private void render(GuiGraphics graphics, DeltaTracker tracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();
        LocalPlayer entity = minecraft.player;
        if (entity != null && !ClientHelper.hideGui()) {
            visible.forEach(renderHolder -> {
                graphics.pose().pushPose();
                OverlayProperties holder = renderHolder.getProperties();
                Vec2 renderLocation = renderHolder.getLoc(screenWidth, screenHeight);
                graphics.pose().translate(renderLocation.x, renderLocation.y, 0);
                graphics.pose().scale(holder.getXScale(), holder.getYScale(), 0);
                renderHolder.render(minecraft.gui, graphics, screenWidth, screenHeight, entity);
                graphics.pose().popPose();
            });
        }
    }

    /**
     * construct all Overlays into the active render queue
     */
    private void construct() {
        this.constructors.forEach((location, constructor) -> {
            OverlayProperties holder = MiscHelper.nonNullOr(this.loadedPositions.get(location.getKey().location()), location.value().createCopy());
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
            Holder<OverlayProperties> location = CollectionHelper.getKeyForValue(this.map, dedicatedHolder);
            dedicatedHolder.getProperties().copy(location.value());
            return;
        }
        throw new IllegalStateException("attempted to reset non-existing Holder");
    }

    /**
     * reset all Overlays
     */
    public static void resetAll() {
        OverlayManager controller = LibClient.overlays;
        Collection<Holder<OverlayProperties>> locations = controller.constructors.keySet();
        locations.forEach(location -> {
            Overlay holder = controller.map.get(location);
            holder.getProperties().copy(location.value());
            controller.visible.add(holder);
        });
        controller.invisible.clear();
    }
}