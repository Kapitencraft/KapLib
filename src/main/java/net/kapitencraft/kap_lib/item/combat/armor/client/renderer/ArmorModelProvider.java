package net.kapitencraft.kap_lib.item.combat.armor.client.renderer;

import net.kapitencraft.kap_lib.item.combat.armor.client.model.ArmorModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * the armor model provider
 */
public interface ArmorModelProvider {

    /**
     * provides a custom armor model.
     * cache the model if possible as it's needed each <i>rendering</i> tick
     * @param living the entity for the model
     * @param stack the stack for the model
     * @param slot the slot for the model
     * @return the model
     */
    ArmorModel getModel(LivingEntity living, ItemStack stack, EquipmentSlot slot);
}
