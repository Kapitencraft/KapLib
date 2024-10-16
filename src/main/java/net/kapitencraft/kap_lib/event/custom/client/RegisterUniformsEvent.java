package net.kapitencraft.kap_lib.event.custom.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * used for registering custom Uniforms for shaders to use <br>
 * only clientside, can not be canceled
 */
@OnlyIn(Dist.CLIENT)
public class RegisterUniformsEvent extends Event implements IModBusEvent {
    private final HashMap<String, Supplier<float[]>> vecSuppliers;
    private final HashMap<String, Supplier<Integer>> intSuppliers;

    public RegisterUniformsEvent(HashMap<String, Supplier<float[]>> vecSuppliers, HashMap<String, Supplier<Integer>> intSuppliers) {
        this.vecSuppliers = vecSuppliers;
        this.intSuppliers = intSuppliers;
    }

    public void addVecUniform(String name, Supplier<float[]> floatCreator) {
        vecSuppliers.put(name, floatCreator);
    }

    public void addIntUniform(String name, Supplier<Integer> intCreator) {
        intSuppliers.put(name, intCreator);
    }
}
