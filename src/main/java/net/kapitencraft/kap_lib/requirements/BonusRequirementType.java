package net.kapitencraft.kap_lib.requirements;

import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class BonusRequirementType extends RequirementType<BonusManager.BonusElement> {
    public static final BonusRequirementType INSTANCE = new BonusRequirementType();

    public BonusRequirementType() {
        super("bonuses", null);
    }

    //TODO fix
    @Override
    public BonusManager.BonusElement getById(ResourceLocation location) {
        if (location.getPath().startsWith("set/")) return BonusManager.instance.getSet(location.withPath(s -> s.substring(4)));
        return super.getById(location);
    }

    @Override
    public ResourceLocation getId(BonusManager.BonusElement value) {
        return value.getId();
    }
}
