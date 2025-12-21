package net.kapitencraft.kap_lib.mana.event.handler;

import net.kapitencraft.kap_lib.mana.ManaAttachmentTypes;
import net.kapitencraft.kap_lib.mana.ManaAttributes;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@EventBusSubscriber
public class ManaEvents {

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ManaAttributes.MANA_COST);
        event.add(EntityType.PLAYER, ManaAttributes.MANA_REGEN);
        event.add(EntityType.PLAYER, ManaAttributes.MAX_MANA);
    }

    @SubscribeEvent
    public static void leaveLevelEvent(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            //save mana to reset back to when re-joining
            player.getPersistentData().putDouble("Mana", player.getData(ManaAttachmentTypes.MANA));
        }
    }

    @SubscribeEvent
    public static void joinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            CompoundTag tag = player.getPersistentData();
            double mana; //upload lost mana
            if (tag.contains("Mana", Tag.TAG_DOUBLE)) {
                mana = tag.getDouble("Mana");
            } else mana = 100;
            player.setData(ManaAttachmentTypes.MANA, mana);
        }
    }
}
