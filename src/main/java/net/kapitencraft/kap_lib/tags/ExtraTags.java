package net.kapitencraft.kap_lib.tags;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ExtraTags {

    public interface DamageTypes {
        TagKey<DamageType> MAGIC = forgeKey("magic");
        TagKey<DamageType> PARTICLE_WEAPON = forgeKey("particle_weapon");

        private static TagKey<DamageType> forgeKey(String subName) {
            return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("forge", subName));
        }
    }

    public interface Items {
        /**
         * add this tag to any bow or crossbow to make its arrows hit enderman
         */
        TagKey<Item> HITS_ENDERMAN = modKey("hits_enderman");

        private static TagKey<Item> forgeKey(String path) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("forge", path));
        }
        private static TagKey<Item> modKey(String path) {
            return TagKey.create(Registries.ITEM, KapLibMod.res(path));
        }
    }

    public interface EntityTypes {
        TagKey<EntityType<?>> ENDER_MOBS = forgeKey("ender_mobs");

        private static TagKey<EntityType<?>> forgeKey(String path) {
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", path));
        }
    }

    public interface Blocks {
        TagKey<Block> VANILLA_GOLEM_HEADS = vanillaKey("golem_heads");

        static TagKey<Block> vanillaKey(String path) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(path));
        }
    }
}
