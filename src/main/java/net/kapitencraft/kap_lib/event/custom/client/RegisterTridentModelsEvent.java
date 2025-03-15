package net.kapitencraft.kap_lib.event.custom.client;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Function;

/**
 * use to register custom models for custom tridents
 */
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

    public void register(RegistryObject<Item> item, Function<ModelPart, Model> modelConstructor, ModelLayerLocation location) {
        models.put(item.get(), modelConstructor.apply(modelSet.bakeLayer(location)));
    }

    public void registerVanillaModel(RegistryObject<Item> item) {
        register(item, TridentModel::new, ModelLayers.TRIDENT);
    }
}
