package net.kapitencraft.kap_lib.recipe.serializers;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.collection.MapStream;
import net.kapitencraft.kap_lib.core.collection.StreamEntry;
import net.kapitencraft.kap_lib.core.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.recipe.registry.ExtraRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class ArmorRecipe extends CustomRecipe {
    private final Ingredient material;
    private final Map<ArmorType, CraftingRecipe> all;
    private final Map<ArmorType, ItemStack> entries;
    private final String group;

    public ArmorRecipe(Ingredient material, Map<ArmorType, ItemStack> all, String group) {
        super(CraftingBookCategory.EQUIPMENT);
        this.material = material;
        this.group = group;
        this.entries = all;
        this.all = MapStream.of(all).biMap((armorType, stack) -> new StreamEntry<>(armorType, this.create(armorType, stack))).toMap();
    }

    private CraftingRecipe create(ArmorType type, ItemStack stack) {
        ShapedRecipePattern pattern = type.makePattern(this.material);
        return new ShapedRecipe(getGroup(), category(), pattern, stack);
    }

    @Override
    public boolean matches(@NotNull CraftingInput container, @NotNull Level level) {
        return this.all.values().stream().anyMatch(recipe -> recipe.matches(container, level));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput pContainer, @NotNull HolderLookup.Provider pRegistryAccess) {
        for (CraftingRecipe recipe : all.values()) {
            if (recipe.matches(pContainer, null)) {
                return recipe.assemble(pContainer, pRegistryAccess);
            }
        }
        return ItemStack.EMPTY;
    }

    public Map<ArmorType, CraftingRecipe> getAll() {
        return all;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i == 3 && j == 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ExtraRecipeSerializers.ARMOR.value();
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

        public static ArmorType fromEquipmentSlot(ArmorItem.Type type) {
            return valueOf(type.getName().toUpperCase());
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

        public ShapedRecipePattern makePattern(Ingredient main) {
            NonNullList<Ingredient> list = NonNullList.withSize(small ? 6 : 9, Ingredient.EMPTY);
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < (small ? 2 : 3); y++) {
                    if (data[x][y]) {
                        list.set(x + y*3, main);
                    }
                }
            }
            return new ShapedRecipePattern(3, small ? 2 : 3, list, Optional.empty());
        }
    }

    public static class Serializer implements RecipeSerializer<ArmorRecipe> {
        private static final MapCodec<ArmorRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Ingredient.CODEC.fieldOf("material").forGetter(r -> r.material),
                Codec.either(Codec.STRING, ResourceLocation.CODEC.listOf()).flatXmap(Serializer::decodeResults, Serializer::encodeResults).fieldOf("results").forGetter(r -> r.entries),
                Codec.STRING.optionalFieldOf("group", "").forGetter(ArmorRecipe::getGroup)
        ).apply(i, ArmorRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, ArmorRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        private static DataResult<? extends Either<String, List<ResourceLocation>>> encodeResults(Map<ArmorType, ItemStack> map) {
            String merged = null;
            List<ResourceLocation> locations = new ArrayList<>();
            for (Map.Entry<ArmorType, ItemStack> entry : map.entrySet()) {
                ItemStack stack = entry.getValue();
                if (!BuiltInRegistries.ITEM.containsValue(stack.getItem())) return DataResult.error(() -> "unable to find item '" + stack.getItem() + "' in the registry");
                ResourceLocation location = BuiltInRegistries.ITEM.getKey(stack.getItem());
                String val = location.toString(); //ensure namespace is preserved
                String typeName = entry.getKey().getSerializedName();
                if (!val.endsWith("_" + typeName)) merged = null;
                else {
                    String element = val.substring(0, val.length() - typeName.length() - 1);
                    if (merged == null) {
                        merged = element;
                    } else if (!merged.equals(element)) merged = "";
                }
                locations.add(location);
            }
            if (merged != null && !merged.isEmpty()) return DataResult.success(Either.left(merged));
            return DataResult.success(Either.right(locations));
        }

        private static DataResult<Map<ArmorType, ItemStack>> decodeResults(Either<String, List<ResourceLocation>> stringListEither) {
            ArmorType[] values = ArmorType.values();
            List<ResourceLocation> locations = stringListEither.map(string -> Arrays.stream(values)
                    .map(ArmorType::getSerializedName)
                    .map(s -> string + "_" + s
                    ).map(ResourceLocation::parse).toList(), Function.identity()
            );
            Map<ArmorType, ItemStack> stackMap = new HashMap<>();
            for (int i1 = 0; i1 < locations.size(); i1++) {
                ResourceLocation name = locations.get(i1);
                if (!BuiltInRegistries.ITEM.containsKey(name))
                    return DataResult.error(() -> "unknown item '" + name + "'");
                stackMap.put(values[i1], new ItemStack(BuiltInRegistries.ITEM.get(name)));
            }
            return DataResult.success(ImmutableMap.copyOf(stackMap));
        }

        public static @NotNull ArmorRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            Ingredient material = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            List<String> items = CollectionHelper.create(4, buf::readUtf);
            List<ItemStack> results = items.stream().filter(s -> !Objects.equals(s, ""))
                    .map(ResourceLocation::parse)
                    .map(BuiltInRegistries.ITEM::get)
                    .map(ItemStack::new).toList();
            List<ArmorType> types = Arrays.stream(ArmorType.values()).toList();
            Map<ArmorType, ItemStack> resultMap = MapStream.create(types, results).toMap();
            return new ArmorRecipe(material, resultMap, group);
        }

        @SuppressWarnings("DataFlowIssue")
        public static void toNetwork(RegistryFriendlyByteBuf buf, ArmorRecipe recipe) {
            buf.writeUtf(recipe.group);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.material);
            recipe.all.values().stream()
                    .map(shapedRecipe -> shapedRecipe.getResultItem(null))
                    .map(ItemStack::getItem)
                    .map(BuiltInRegistries.ITEM::getKey)
                    .map(ResourceLocation::toString)
                    .forEach(buf::writeUtf);
            for (int i = 0; i < 4 - recipe.all.size(); i++) {
                buf.writeUtf("");
            }
        }

        @Override
        public MapCodec<ArmorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ArmorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
