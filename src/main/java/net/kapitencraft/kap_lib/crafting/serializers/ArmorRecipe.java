package net.kapitencraft.kap_lib.crafting.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.crafting.ExtraRecipeTypes;
import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.registry.ExtraRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ArmorRecipe extends CustomRecipe {
    private final Ingredient material;
    private final List<ShapedRecipe> all;
    private final String group;

    public ArmorRecipe(ResourceLocation location, CraftingBookCategory p_249010_, Ingredient material, Map<ArmorType, ItemStack> all, String group) {
        super(location, p_249010_);
        this.material = material;
        this.group = group;
        this.all = MapStream.of(all).mapToSimple(this::create).toList();
    }

    private ShapedRecipe create(ArmorType type, ItemStack stack) {
        NonNullList<Ingredient> cost = type.makeIngredients(this.material);
        return new ShapedRecipe(getId(), getGroup(), category(), 3, cost.size() / 3, cost, stack);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        return this.all.stream().anyMatch(recipe -> recipe.matches(container, level));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer pContainer, @NotNull RegistryAccess pRegistryAccess) {
        for (ShapedRecipe recipe : all) {
            if (recipe.matches(pContainer, null)) {
                return recipe.assemble(pContainer, pRegistryAccess);
            }
        }
        return ItemStack.EMPTY;
    }

    public List<ShapedRecipe> getAll() {
        return all;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ExtraRecipeTypes.ARMOR_RECIPE.get();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i == 3 && j == 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ExtraRecipeSerializers.ARMOR.get();
    }

    public enum ArmorType implements StringRepresentable {
        HELMET("helmet",
                of(List.of(true, true, true, true, false, true), true)),
        CHESTPLATE("chestplate",
                of(List.of(true, false, true, true, true, true, true, true, true), false)),
        LEGGINGS("leggings",
                of(List.of(true, true, true, true, false, true, true, false, true), false)),
        BOOTS("boots",
                of(List.of(true, false, true, true, false, true), true));

        public static ArmorType fromEquipmentSlot(EquipmentSlot slot) {
            return switch (slot) {
                case FEET -> BOOTS;
                case LEGS -> LEGGINGS;
                case CHEST -> CHESTPLATE;
                case HEAD -> HELMET;
                default -> throw new IllegalArgumentException("equipment slot '" + slot.getName() + "' can not be converted to armor type");
            };
        }

        public static final EnumCodec<ArmorType> CODEC = StringRepresentable.fromEnum(ArmorType::values);

        public static ArmorType get(String name) {
            return CODEC.byName(name, HELMET);
        }

        private final String name;
        private final boolean small;
        private final boolean[][] data;

        private static boolean[][] of(List<Boolean> list, boolean small) {
            int height = small ? 2 : 3;
            boolean[][] map = new boolean[3][height];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < height; j++) {
                    map[i][j] = list.get(i + j * 3);
                }
            }
            return map;
        }

        ArmorType(String name, boolean[][] data) {
            this.name = name;
            this.small = data[0].length == 2;
            this.data = data;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        public NonNullList<Ingredient> makeIngredients(Ingredient main) {
            NonNullList<Ingredient> list = NonNullList.withSize(small ? 6 : 9, Ingredient.EMPTY);
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < (small ? 2 : 3); y++) {
                    if (data[x][y]) {
                        list.set(x + y*3, main);
                    }
                }
            }
            return list;
        }
    }

    public static class Serializer implements RecipeSerializer<ArmorRecipe> {

        @Override
        public @NotNull ArmorRecipe fromJson(@NotNull ResourceLocation location, @NotNull JsonObject object) {
            String group = GsonHelper.getAsString(object, "group", "");
            Ingredient material = Ingredient.fromJson(object.get("material"));
            JsonArray unused = GsonHelper.getAsJsonArray(object, "unused", new JsonArray());
            List<ArmorType> unusedSlots = unused.asList().stream().map(JsonElement::getAsString).map(ArmorType::get).toList();
            MapStream<ArmorType, ResourceLocation> map;
            List<ArmorType> toUse = Arrays.stream(ArmorType.values()).filter(armorType -> !unusedSlots.contains(armorType)).toList();
            String results = GsonHelper.getAsString(object, "results", null);
            if (results != null) {
                Stream<ResourceLocation> stream = toUse.stream()
                        .map(ArmorType::getSerializedName)
                        .map(s -> TextHelper.mergeRegister(results, s))
                        .map(ResourceLocation::new);
                map = MapStream.create(toUse, stream.toList());
            } else {
                JsonArray array = GsonHelper.getAsJsonArray(object, "results", new JsonArray());
                List<ResourceLocation> locations = array.asList().stream().map(JsonElement::getAsString).map(ResourceLocation::new).toList();
                map = MapStream.create(toUse, locations);
            }
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(object, "category", null));
            return new ArmorRecipe(location, category, material, map.mapValues(ForgeRegistries.ITEMS::getValue).mapValues(ItemStack::new).toMap(), group);
        }

        @Override
        public @Nullable ArmorRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            Ingredient material = Ingredient.fromNetwork(buf);
            List<String> items = CollectionHelper.create(4, buf::readUtf);
            List<ItemStack> results = items.stream().filter(s -> !Objects.equals(s, ""))
                    .map(ResourceLocation::new)
                    .map(BuiltInRegistries.ITEM::get)
                    .map(ItemStack::new).toList();
            List<ArmorType> types = Arrays.stream(ArmorType.values()).toList();
            Map<ArmorType, ItemStack> resultMap = MapStream.create(types, results).toMap();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            return new ArmorRecipe(resourceLocation, category, material, resultMap, group);
        }

        @SuppressWarnings("DataFlowIssue")
        @Override
        public void toNetwork(FriendlyByteBuf buf, ArmorRecipe recipe) {
            buf.writeUtf(recipe.group);
            recipe.material.toNetwork(buf);
            recipe.all.stream()
                    .map(shapedRecipe -> shapedRecipe.getResultItem(null))
                    .map(ItemStack::getItem)
                    .map(BuiltInRegistries.ITEM::getKey)
                    .map(ResourceLocation::toString)
                    .forEach(buf::writeUtf);
            MiscHelper.repeat(4 - recipe.all.size(), integer -> buf.writeUtf(""));
            buf.writeEnum(recipe.category());
        }
    }
}
