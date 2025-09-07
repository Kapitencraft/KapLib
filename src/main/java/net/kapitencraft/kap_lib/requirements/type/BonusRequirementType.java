package net.kapitencraft.kap_lib.requirements.type;

import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.AbstractBonusElement;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.resources.ResourceLocation;
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
