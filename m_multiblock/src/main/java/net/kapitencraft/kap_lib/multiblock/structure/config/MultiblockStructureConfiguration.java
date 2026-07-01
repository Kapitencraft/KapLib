package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MultiblockStructureConfiguration {
    public static final Codec<MultiblockStructureConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(Codec.STRING, BlockGroup.CODEC).fieldOf("groups").forGetter(c -> c.groups),
            SpacialData.codec(BlockInstance.CODEC, BlockInstance.EmptyBlockInstance.INSTANCE).fieldOf("data").forGetter(c -> c.spacialData)
    ).apply(i, MultiblockStructureConfiguration::new));

    private final Map<String, BlockGroup> groups;
    private final SpacialData<BlockInstance> spacialData;

    public MultiblockStructureConfiguration(Map<String, BlockGroup> groups, SpacialData<BlockInstance> spacialData) {
        this.groups = groups;
        this.spacialData = spacialData;
    }

    public static class BlockGroup {
        private static final Codec<BlockGroup> CODEC = Codec.unit(BlockGroup::new);

        public boolean matches(Block block) {
            return true;
        }
    }

    public abstract static class BlockInstance {
        public static BlockInstance getEmpty() {
            return EmptyBlockInstance.INSTANCE; //must be method due to possible class loading error
        }

        private static final Codec<BlockInstance> CODEC = Type.CODEC.dispatch(BlockInstance::getType, Type::getCodec);

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
                return false;
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
