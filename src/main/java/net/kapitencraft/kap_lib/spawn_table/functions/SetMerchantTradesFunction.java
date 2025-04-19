package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

public class SetMerchantTradesFunction extends SpawnEntityConditionalFunction {
    private final MerchantOffers offers;

    protected SetMerchantTradesFunction(LootItemCondition[] pPredicates, MerchantOffers offers) {
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

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetMerchantTradesFunction> {

        @Override
        public void serialize(JsonObject pJson, SetMerchantTradesFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            JsonArray array = pFunction.offers.stream().map(Serializer::writeOffer)
                    .collect(CollectorHelper.toJsonArray());
            pJson.add("offers", array);
        }

        private static JsonObject writeOffer(MerchantOffer offer) {
            JsonObject object = new JsonObject();
            JsonHelper.addItemStack(object, "costA", offer.getBaseCostA());
            if (offer.getCostB() != ItemStack.EMPTY) JsonHelper.addItemStack(object, "costB", offer.getCostB());
            JsonHelper.addItemStack(object, "result", offer.getResult());
            if (offer.getUses() != 0) object.addProperty("uses", offer.getUses());
            object.addProperty("maxUses", offer.getMaxUses());
            if (!offer.shouldRewardExp()) object.addProperty("rewardXp", false);
            if (offer.getSpecialPriceDiff() != 0) object.addProperty("specialPriceDiff", offer.getSpecialPriceDiff());
            if (offer.getDemand() != 0) object.addProperty("demand", offer.getDemand());
            if (offer.getPriceMultiplier() != 1) object.addProperty("priceMultiplier", offer.getPriceMultiplier());
            if (offer.getXp() != 1) object.addProperty("xp", offer.getXp());
            return object;
        }

        @Override
        public SetMerchantTradesFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            MerchantOffers offers = JsonHelper.castToObjects(GsonHelper.getAsJsonArray(pObject, "offers")).map(Serializer::readOffer).collect(Collectors.toCollection(MerchantOffers::new));
            return new SetMerchantTradesFunction(pConditions, offers);
        }

        private static MerchantOffer readOffer(JsonObject object) {
            ItemStack costA = JsonHelper.getAsItemStack(GsonHelper.getAsJsonObject(object, "costA"));
            ItemStack costB = object.has("costB") ? JsonHelper.getAsItemStack(GsonHelper.getAsJsonObject(object, "costB")) : ItemStack.EMPTY;
            ItemStack result = JsonHelper.getAsItemStack(GsonHelper.getAsJsonObject(object, "result"));
            int uses = GsonHelper.getAsInt(object, "uses", 0);
            int maxUses = GsonHelper.getAsInt(object, "maxUses");
            boolean rewardXp = GsonHelper.getAsBoolean(object, "rewardXp", true);
            int specialPriceDiff = GsonHelper.getAsInt(object, "specialPriceDiff", 0);
            int demand = GsonHelper.getAsInt(object, "demand", 0);
            float priceMultiplier = GsonHelper.getAsInt(object, "priceMultiplier", 1);
            int xp = GsonHelper.getAsInt(object, "xp", 1);
            MerchantOffer offer = new MerchantOffer(costA, costB, result, uses, maxUses, xp, priceMultiplier, demand);
            offer.rewardExp = rewardXp;
            offer.setSpecialPriceDiff(specialPriceDiff);
            return offer;
        }
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
            return new SetMerchantTradesFunction(getConditions(), offers);
        }
    }
}
