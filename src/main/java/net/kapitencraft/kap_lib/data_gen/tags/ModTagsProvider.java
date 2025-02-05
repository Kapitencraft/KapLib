package net.kapitencraft.kap_lib.data_gen.tags;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModTagsProvider {
    public static class Block extends BlockTagsProvider {

        public Block(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, KapLibMod.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {
            this.tag(ExtraTags.Blocks.VANILLA_GOLEM_HEADS).add(Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN);
        }
    }

    public static class EntityTypes extends EntityTypeTagsProvider {

        public EntityTypes(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider) {
            super(pOutput, pProvider);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {
            this.tag(ExtraTags.EntityTypes.ENDER_MOBS).add(EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE);
        }
    }

    public static class DamageType extends DamageTypeTagsProvider {

        public DamageType(PackOutput p_270719_, CompletableFuture<HolderLookup.Provider> p_270256_, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_270719_, p_270256_, KapLibMod.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            this.tag(ExtraTags.DamageTypes.MAGIC).add(DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, DamageTypes.DRAGON_BREATH, DamageTypes.SONIC_BOOM);
        }
    }
}
