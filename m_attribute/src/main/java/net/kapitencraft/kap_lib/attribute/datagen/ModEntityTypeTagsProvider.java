package net.kapitencraft.kap_lib.attribute.datagen;

import net.kapitencraft.kap_lib.attribute.AMEntityTypeTags;
import net.kapitencraft.kap_lib.attribute.AttributeModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, AttributeModule.MODULE_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(AMEntityTypeTags.IMMUNE_TO_LIFE_STEAL).addTags(
                EntityTypeTags.UNDEAD
        ).add(
                EntityType.IRON_GOLEM,
                EntityType.SNOW_GOLEM
        );
    }
}
