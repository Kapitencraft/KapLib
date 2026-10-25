package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiblockStructureConfiguration {
    public static final Codec<MultiblockStructureConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(Codec.STRING, BlockGroup.CODEC).fieldOf("groups").forGetter(c -> c.groups),
            Codec.unboundedMap(Direction.Axis.CODEC, Quantifier.CODEC.listOf()).fieldOf("quantifiers").forGetter(c -> c.quantifiers),
            SpacialData.codec(BlockInstance.CODEC, BlockInstance.EmptyBlockInstance.INSTANCE).fieldOf("data").forGetter(c -> c.spacialData)
    ).apply(i, MultiblockStructureConfiguration::new));

    private final Map<String, BlockGroup> groups;
    private final Map<Direction.Axis, List<Quantifier>> quantifiers;
    private final SpacialData<BlockInstance> spacialData;

    public MultiblockStructureConfiguration(Map<String, BlockGroup> groups, Map<Direction.Axis, List<Quantifier>> quantifiers, SpacialData<BlockInstance> spacialData) {
        this.groups = groups;
        this.quantifiers = quantifiers;
        this.spacialData = spacialData;
    }

    public BlockInstance getInstanceAt(int x, int y, int z) {
        return spacialData.get(x, y, z);
    }

    public BlockInstance getInstanceAt(Vec3i pos) {
        return this.getInstanceAt(pos.getX(), pos.getY(), pos.getZ());
    }

    public List<Quantifier> getQuantifiers(Direction.Axis axis) {
        return quantifiers.getOrDefault(axis, List.of());
    }

    public Vec3i getSize() {
        return spacialData.getSize();
    }

    public Multimap<BlockState, Vec3i> buildInfo() {
        Multimap<BlockState, Vec3i> multimap = HashMultimap.create();
        Vec3i size = spacialData.getSize();
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockInstance blockInstance = this.spacialData.get(x, y, z);
                    List<BlockState> validStates = blockInstance.getValidStates();
                    Vec3i offset = new Vec3i(x, y, z);
                    for (BlockState validState : validStates) {
                        multimap.put(validState, offset);
                    }
                }
            }
        }
        return multimap;
    }

    public static class BlockGroup {
        public static final Codec<BlockGroup> CODEC = Codec.unit(BlockGroup::new);

        public boolean matches(Block block) {
            return true;
        }
    }

    public abstract static class BlockInstance {
        public static BlockInstance getEmpty() {
            return EmptyBlockInstance.INSTANCE; //must be method due to possible class loading error
        }

        public static final Codec<BlockInstance> CODEC = Type.CODEC.dispatch(BlockInstance::getType, Type::getCodec);

        public static BlockInstance forState(BlockState state) {
            return new StateBlockInstance(state);
        }

        public static BlockInstance forTag(TagKey<Block> key) {
            return new TagBlockInstance(key);
        }

        public static BlockInstance forGroup(String name) {
            return new GroupBlockInstance(name);
        }

        public abstract @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups, Player player);

        public boolean isState() {
            return false;
        }

        public abstract @Nullable TagKey<Block> getTag();

        public abstract @Nullable String getGroupId();

        public Tag toNbt() {
            return null;
        }

        public abstract List<BlockState> getValidStates();

        protected enum Type implements StringRepresentable {
            STATE(StateBlockInstance.CODEC),
            TAG(TagBlockInstance.CODEC),
            GROUP(GroupBlockInstance.CODEC),
            EMPTY(EmptyBlockInstance.CODEC);

            public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

            private final MapCodec<? extends BlockInstance> codec;

            Type(MapCodec<? extends BlockInstance> codec) {
                this.codec = codec;
            }

            public MapCodec<? extends BlockInstance> getCodec() {
                return codec;
            }

            @Override
            public String getSerializedName() {
                return this.name().toLowerCase();
            }
        }

        protected abstract Type getType();

        public abstract boolean isValid(BlockState state);

        private static class StateBlockInstance extends BlockInstance {
            private static final MapCodec<StateBlockInstance> CODEC = BlockState.CODEC.xmap(StateBlockInstance::new, i -> i.state).fieldOf("state");

            private final BlockState state;

            private StateBlockInstance(BlockState state) {
                this.state = state;
            }

            @Override
            protected Type getType() {
                return Type.STATE;
            }

            @Override
            public boolean isValid(BlockState state) {
                return this.state == state;
            }

            @Override
            public List<BlockState> getValidStates() {
                return List.of(this.state);
            }

            @Override
            public @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups, Player player) {
                if (tags.isEmpty()) {
                    if (groups.isEmpty())
                        return this;
                    BlockInstance.displayGroupMessage(player, groups.getFirst());
                    return BlockInstance.forGroup(groups.getFirst());
                }
                BlockInstance.displayTagMessage(player, tags.getFirst());
                return BlockInstance.forTag(tags.getFirst());
            }

            @Override
            public boolean isState() {
                return true;
            }

            @Override
            public @Nullable String getGroupId() {
                return null;
            }

            @Override
            public @Nullable TagKey<Block> getTag() {
                return null;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof StateBlockInstance stateBlockInstance && this.state == stateBlockInstance.state;
            }
        }

        private static class TagBlockInstance extends BlockInstance {
            private static final MapCodec<TagBlockInstance> CODEC = TagKey.codec(Registries.BLOCK).xmap(TagBlockInstance::new, i -> i.tagKey).fieldOf("tag");

            private final TagKey<Block> tagKey;

            private TagBlockInstance(TagKey<Block> tagKey) {
                this.tagKey = tagKey;
            }

            @Override
            protected Type getType() {
                return Type.TAG;
            }

            @Override
            public boolean isValid(BlockState state) {
                return state.is(this.tagKey);
            }

            @Override
            public List<BlockState> getValidStates() {
                List<BlockState> states = new ArrayList<>();
                for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(this.tagKey)) {
                    states.add(holder.value().defaultBlockState());
                }
                return states;
            }

            @Override
            public @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups, Player player) {
                int i = tags.indexOf(tagKey);
                if (i == -1) {
                    if (tags.isEmpty()) {
                        if (groups.isEmpty()) {
                            BlockInstance.displayStateMessage(player);
                            return BlockInstance.forState(state);
                        }
                        BlockInstance.displayGroupMessage(player, groups.getFirst());
                        return BlockInstance.forGroup(groups.getFirst());
                    }
                    BlockInstance.displayTagMessage(player, tags.getFirst());
                    return BlockInstance.forTag(tags.getFirst());
                }
                if (i >= tags.size() - 1) {
                    if (groups.isEmpty()) {
                        BlockInstance.displayStateMessage(player);
                        return BlockInstance.forState(state);
                    }
                    BlockInstance.displayGroupMessage(player, groups.getFirst());
                    return BlockInstance.forGroup(groups.getFirst());
                }
                BlockInstance.displayTagMessage(player, tags.get(i + 1));
                return BlockInstance.forTag(tags.get(i + 1));
            }

            @Override
            public @Nullable String getGroupId() {
                return null;
            }

            @Override
            public @Nullable TagKey<Block> getTag() {
                return this.tagKey;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof TagBlockInstance that && that.tagKey.equals(this.tagKey);
            }
        }

        private static class GroupBlockInstance extends BlockInstance {
            private static final MapCodec<GroupBlockInstance> CODEC = Codec.STRING.xmap(GroupBlockInstance::new, i -> i.groupName).fieldOf("name");

            private final String groupName;

            private GroupBlockInstance(String groupName) {
                this.groupName = groupName;
            }

            @Override
            protected Type getType() {
                return Type.GROUP;
            }

            @Override
            public boolean isValid(BlockState state) {
                return false;
            }

            @Override
            public @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups, Player player) {
                int i = groups.indexOf(this.groupName);
                if (i == -1 || i >= groups.size() - 1) {
                    displayStateMessage(player);
                    return BlockInstance.forState(state);
                }
                displayGroupMessage(player, groups.get(i + 1));
                return BlockInstance.forGroup(groups.get(i + 1));
            }

            @Override
            public List<BlockState> getValidStates() {
                return List.of();
            }

            @Override
            public @Nullable String getGroupId() {
                return this.groupName;
            }

            @Override
            public @Nullable TagKey<Block> getTag() {
                return null;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof GroupBlockInstance that && that.groupName.equals(this.groupName);
            }
        }

        private static class EmptyBlockInstance extends BlockInstance {
            private static final EmptyBlockInstance INSTANCE = new EmptyBlockInstance();
            public static final MapCodec<? extends BlockInstance> CODEC = MapCodec.unit(INSTANCE);

            @Override
            public @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups, Player player) {
                return INSTANCE;
            }

            @Override
            protected Type getType() {
                return Type.EMPTY;
            }

            @Override
            public boolean isValid(BlockState state) {
                return true;
            }

            @Override
            public List<BlockState> getValidStates() {
                return List.of();
            }

            @Override
            public @Nullable String getGroupId() {
                return null;
            }

            @Override
            public @Nullable TagKey<Block> getTag() {
                return null;
            }
        }

        private static void displayStateMessage(Player player) {
            player.displayClientMessage(Component.translatable("mb.structure.configurator.select_state"), true);
        }

        private static void displayTagMessage(Player player, TagKey<Block> key) {
            player.displayClientMessage(Component.translatable("mb.structure.configurator.select_tag", key.location().toString()), true);
        }

        private static void displayGroupMessage(Player player, String s) {
            player.displayClientMessage(Component.translatable("mb.structure.configurator.select_group", s), true);
        }
    }
}
