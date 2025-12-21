package net.kapitencraft.kap_lib.requirement.type;

import net.kapitencraft.kap_lib.core.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.bonus.AbstractBonusElement;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import org.jetbrains.annotations.NotNull;

public class BonusRequirementType implements RequirementType<AbstractBonusElement> {
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
