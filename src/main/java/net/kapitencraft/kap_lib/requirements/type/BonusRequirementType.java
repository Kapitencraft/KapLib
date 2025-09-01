package net.kapitencraft.kap_lib.requirements.type;

import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.AbstractBonusElement;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.resources.ResourceLocation;

public class BonusRequirementType implements RequirementType<AbstractBonusElement> {
    private static final  DataPackSerializer<AbstractBonusElement> SERIALIZER = new DataPackSerializer<>(
            BonusManager.instance.getElementCodec(),

    );

    @Override
    public DataPackSerializer<AbstractBonusElement> serializer() {
        return null;
    }

    @Override
    public String getName() {
        return "bonuses";
    }
}
