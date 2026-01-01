package net.kapitencraft.kap_lib.bonus.requirement;

import net.kapitencraft.kap_lib.core.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.bonus.AbstractBonusElement;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.kapitencraft.kap_lib.requirement.type.RequirementType;
import org.jetbrains.annotations.NotNull;

public class BonusRequirementType implements RequirementType<AbstractBonusElement> {
    public static final RequirementType<AbstractBonusElement> INSTANCE = new BonusRequirementType();

    private static final  DataPackSerializer<AbstractBonusElement> SERIALIZER = new DataPackSerializer<>(
            BonusManager.updateInstance().getElementCodec(), //ensure instance is loaded
            BonusManager.instance.streamCodec
    );

    @Override
    public @NotNull DataPackSerializer<AbstractBonusElement> serializer() {
        return SERIALIZER;
    }

    @Override
    public String getName() {
        return "bonuses";
    }
}
