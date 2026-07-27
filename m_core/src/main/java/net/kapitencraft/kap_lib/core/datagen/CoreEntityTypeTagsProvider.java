package net.kapitencraft.kap_lib.core.datagen;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.tags.ExtraTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CoreEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public CoreEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, LibConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ExtraTags.EntityTypes.ENDER_MOBS).add(
                EntityType.ENDERMAN,
                EntityType.ENDERMITE,
                EntityType.ENDER_DRAGON,
                EntityType.SHULKER
        );
    }
}
