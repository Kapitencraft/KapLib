package net.kapitencraft.kap_lib.requirements.type;

import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.resources.ResourceLocation;

public class BonusRequirementType implements RequirementType<BonusManager.BonusElement> {

    @Override
    public BonusManager.BonusElement getById(ResourceLocation location) {
        if (location.getPath().startsWith("set/")) return BonusManager.instance.getSet(location.withPath(s -> s.substring(4)));
        return BonusManager.instance.getItemBonus(location);
    }

    @Override
    public ResourceLocation getId(BonusManager.BonusElement value) {
        return value.getId();
    }

    @Override
    public String getName() {
        return "bonuses";
    }
}
