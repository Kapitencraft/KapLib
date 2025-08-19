package net.kapitencraft.kap_lib.client.shaders;

import com.mojang.blaze3d.shaders.Uniform;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.event.ModEventFactory;
import net.kapitencraft.kap_lib.event.custom.client.RegisterUniformsEvent;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * manages all the custom uniforms registered via the {@link RegisterUniformsEvent}
 */
public class UniformsProvider {

    private static final HashMap<String, Supplier<float[]>> vecSuppliers = new HashMap<>();
    private static final HashMap<String, Supplier<Integer>> intSuppliers = new HashMap<>();

    static {
        RegisterUniformsEvent event = new RegisterUniformsEvent(vecSuppliers, intSuppliers);
        ModEventFactory.fireModEvent(event);
    }

    public static void applyVectors(ShaderInstance inst) {
        MapStream.of(vecSuppliers).mapKeys(inst::getUniform)
                .filterKeys(Objects::nonNull).mapValues(Supplier::get)
                .forEach(Uniform::set);
    }

    public static void applySingletons(ShaderInstance inst) {
        MapStream.of(intSuppliers).mapKeys(inst::getUniform)
                .filterKeys(Objects::nonNull).mapValues(Supplier::get)
                .forEach(Uniform::set);
    }
}