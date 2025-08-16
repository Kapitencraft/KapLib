package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SetMerchantTradesFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetMerchantTradesFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MerchantOffers.CODEC.fieldOf("offers").forGetter(f -> f.offers)
    ).and(commonFields(i).t1()).apply(i, SetMerchantTradesFunction::new));

    private final MerchantOffers offers;

    protected SetMerchantTradesFunction(MerchantOffers offers, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.offers = offers;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof AbstractVillager abstractVillager) {
            MerchantOffers offers = abstractVillager.getOffers();
            offers.clear();
            offers.addAll(this.offers);
        }
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_MERCHANT_TRADES.get();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final MerchantOffers offers = new MerchantOffers();

        public Builder addOffer(MerchantOffer offer) {
            offers.add(offer);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetMerchantTradesFunction(offers, getConditions());
        }
    }
}
