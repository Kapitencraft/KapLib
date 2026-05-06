package net.kapitencraft.kap_lib.core.event.custom;

import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * used to register custom {@link FontSet} to the manager.
 * see documentation of subclasses for further information
 */
public abstract class FontSetsEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, FontSet> fontSets;

    protected FontSetsEvent(Map<ResourceLocation, FontSet> fontSets) {
        this.fontSets = fontSets;
    }

    public void register(ResourceLocation location, FontSet set) {
        if (this.fontSets.put(location, set) != null) {
            throw new IllegalStateException("duplicate font set with id " + location);
        }
    }

    /**
     * used to create and initially register font sets to the font manager.
     * <br> exposes the TextureManager via {@link #getManager()} for fonts using textures
     * <br>this event is fired on the mod eventbus, only on the logical client
     */
    public static class Register extends FontSetsEvent {
        private final TextureManager manager;

        @ApiStatus.Internal
        public Register(Map<ResourceLocation, FontSet> fontSets, TextureManager manager) {
            super(fontSets);
            this.manager = manager;
        }

        public TextureManager getManager() {
            return manager;
        }
    }

    /**
     * event fired whenever the Font Manager reloads.
     * <br> does not provide the TextureManager; implementers should have an instance of their FontSet stored
     * <br> this event is fired on the mod eventbus, only on the logical client
     */
    public static class Update extends FontSetsEvent {

        @ApiStatus.Internal
        public Update(Map<ResourceLocation, FontSet> fontSets) {
            super(fontSets);
        }
    }
}
