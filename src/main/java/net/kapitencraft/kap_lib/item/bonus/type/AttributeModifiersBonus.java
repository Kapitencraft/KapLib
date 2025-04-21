package net.kapitencraft.kap_lib.item.bonus.type;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AttributeModifiersBonus implements Bonus<AttributeModifiersBonus> {

    @Override
    public DataPackSerializer<AttributeModifiersBonus> getSerializer() {
        return null;
    }

    @Override
    public void additionalToNetwork(FriendlyByteBuf buf) {

    }

    //TODO implement
    @Override
    public @Nullable Multimap<Attribute, AttributeModifier> getModifiers(LivingEntity living, EquipmentSlot slot) {
    }

    @Override
    public void addDisplay(List<Component> currentTooltip) {

    }
}
