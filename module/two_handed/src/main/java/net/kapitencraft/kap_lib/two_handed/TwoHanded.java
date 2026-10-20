package net.kapitencraft.kap_lib.two_handed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record TwoHanded(boolean showInTooltip) implements TooltipProvider {
    public static final Codec<TwoHanded> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(TwoHanded::showInTooltip)
    ).apply(i, TwoHanded::new));
    public static final StreamCodec<ByteBuf, TwoHanded> STREAM_CODEC = ByteBufCodecs.BOOL.map(TwoHanded::new, TwoHanded::showInTooltip);

    private static final Component TOOLTIP = Component.translatable("item.two_handed").withStyle(ChatFormatting.BLUE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (showInTooltip) {
            tooltipAdder.accept(TOOLTIP);
        }
    }
}
