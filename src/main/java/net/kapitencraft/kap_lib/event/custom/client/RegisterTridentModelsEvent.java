package net.kapitencraft.kap_lib.event.custom.client;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * use to register custom models for custom tridents
 */
@ApiStatus.Experimental
public class RegisterTridentModelsEvent extends Event implements IModBusEvent {
    private final Map<Item, Model> models;
    private final EntityModelSet modelSet;

    public RegisterTridentModelsEvent(Map<Item, Model> models, EntityModelSet modelSet) {
        this.models = models;
        this.modelSet = modelSet;
    }

    public EntityModelSet getModelSet() {
        return modelSet;
    }

    public void register(Supplier<Item> item, Function<ModelPart, Model> modelConstructor, ModelLayerLocation location) {
        models.put(item.get(), modelConstructor.apply(modelSet.bakeLayer(location)));
    }

    public void registerVanillaModel(Supplier<Item> item) {
        register(item, TridentModel::new, ModelLayers.TRIDENT);
    }
}
