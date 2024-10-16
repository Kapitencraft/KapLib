package net.kapitencraft.kap_lib.client.overlay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.overlay.box.InteractiveBox;
import net.kapitencraft.kap_lib.client.overlay.holder.MultiLineOverlay;
import net.kapitencraft.kap_lib.client.overlay.holder.Overlay;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.event.ModEventFactory;
import net.kapitencraft.kap_lib.event.custom.client.RegisterOverlaysEvent;
import net.kapitencraft.kap_lib.helpers.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
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
                    Codec.unboundedMap(IOHelper.UUID_CODEC, OverlayProperties.CODEC).fieldOf("storage").forGetter(OverlayManager::getLocations)
            ).apply(renderControllerInstance, OverlayManager::fromCodec)
    );

    private static OverlayManager fromCodec(Map<UUID, OverlayProperties> map) {
        OverlayManager controller = new OverlayManager();
        controller.loadedPositions.putAll(map);
        return controller;
    }

    /**
     * @return the positions of each overlay mapped to their UUID
     */
    private Map<UUID, OverlayProperties> getLocations() {
        return MapStream.of(map).mapValues(Overlay::getProperties).mapKeys(OverlayLocation::getUUID).toMap();
    }

    /**
     * a holder for the File all information is saved in
     */
    private static File PERSISTENT_FILE;

    private static @NotNull File getOrCreateFile() {
        if (PERSISTENT_FILE == null) {
            PERSISTENT_FILE = new File(KapLibMod.MAIN, "gui-locations.json");
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

    private final Map<OverlayLocation, Function<OverlayProperties, Overlay>> constructors = new HashMap<>();
    public final Map<OverlayLocation, Overlay> map = new HashMap<>();
    private final Map<UUID, OverlayProperties> loadedPositions = new HashMap<>();

    private OverlayManager() {
        this.register();
    }

    /**
     * fire the {@link RegisterOverlaysEvent} for registering custom events
     */
    private void register() {
        this.createRenderer(OverlayLocations.STATS, positionHolder -> new MultiLineOverlay(
                Component.translatable("overlay.stats"),
                positionHolder,
                -10,
                List.of(
                        player -> Component.literal("Protection: " + getDamageProtection(player) + "%").withStyle(ChatFormatting.DARK_BLUE),
                        player -> Component.literal("Effective HP: " + MathHelper.defRound(player.getHealth() * 100 / (100 - getDamageProtection(player)))).withStyle(ChatFormatting.DARK_AQUA),
                        player -> Component.literal("Current Speed: " + cancelGravityMovement(player) + " b/s").withStyle(ChatFormatting.YELLOW)
                )
        ));
        ModEventFactory.fireModEvent(new RegisterOverlaysEvent(this::createRenderer));
        construct();
    }

    private static double getDamageProtection(LivingEntity living) {
        return MathHelper.defRound(100 - MathHelper.calculateDamage(100, living.getAttributeValue(Attributes.ARMOR), living.getAttributeValue(Attributes.ARMOR_TOUGHNESS)));
    }

    private static double cancelGravityMovement(LivingEntity living) {
        Vec3 delta = living.getDeltaMovement();
        if (living.onGround())
            delta = delta.add(0, living.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()), 0);
        return MathHelper.defRound(delta.length() * 20);
    }

    private void createRenderer(OverlayLocation provider, Function<OverlayProperties, Overlay> constructor) {
        if (this.constructors.containsKey(provider))
            throw new IllegalStateException("detected double registered Overlay with UUID '" + provider.getUUID() + "'");
        this.constructors.put(provider, constructor);
    }

    /**
     * save the Overlay locations to the corresponding file
     */
    public static void save() {
        IOHelper.saveFile(getOrCreateFile(), CODEC, LibClient.controller);
    }

    /**
     * register the renderer
     */
    @SubscribeEvent
    public static void overlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("main", LibClient.controller::render);
    }

    /**
     * create all overlay-relocation widgets and push them to the handling screen
     */
    public void fillRenderBoxes(Consumer<InteractiveBox> acceptor, LocalPlayer player, Font font, float width, float height) {
        this.map.values().stream().map(renderHolder -> renderHolder.newBox(width, height, player, font)).forEach(acceptor);
    }

    /**
     * render all overlays
     */
    private void render(ForgeGui forgeGui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        LocalPlayer entity = Minecraft.getInstance().player;
        if (entity != null && !ClientHelper.hideGui()) {
            map.forEach((uuid, renderHolder) -> {
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
            OverlayProperties holder = MiscHelper.nonNullOr(this.loadedPositions.get(location.getUUID()), location.getDefault().createCopy());
            this.map.put(location, constructor.apply(holder));
        });
    }

    /**
     * reset given Overlay to it's default location
     * <br> provided from inside {@link OverlayLocation}
     */
    @SuppressWarnings("all")
    public void reset(Overlay dedicatedHolder) {
        if (this.map.containsValue(dedicatedHolder)) {
            OverlayLocation location = CollectionHelper.getKeyForValue(this.map, dedicatedHolder);
            dedicatedHolder.getProperties().copy(location.getDefault());
            return;
        }
        throw new IllegalStateException("attempted to reset non-existing Holder");
    }

    /**
     * reset all Overlays
     */
    public static void resetAll() {
        OverlayManager controller = LibClient.controller;
        Collection<OverlayLocation> locations = controller.constructors.keySet();
        locations.forEach(location -> {
            Overlay holder = controller.map.get(location);
            holder.getProperties().copy(location.getDefault());
        });
    }
}