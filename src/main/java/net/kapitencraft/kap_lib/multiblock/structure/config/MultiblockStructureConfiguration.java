package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MultiblockStructureConfiguration {
    private final Map<String, BlockGroup> groups;

    private final List<BlockInstance> blockLookup;

    private MultiblockStructureConfiguration(Map<String, BlockGroup> groups, List<BlockInstance> blockLookup) {
        this.groups = groups;
        this.blockLookup = blockLookup;
    }

    private static class BlockGroup {

        public boolean matches(Block block) {
            return true;
        }
    }

    public abstract static class BlockInstance {

        public static BlockInstance forState(BlockState state) {
            return new StateBlockInstance(state);
        }

        public static BlockInstance forTag(TagKey<Block> key) {
            return new TagBlockInstance(key);
        }

        public static BlockInstance forGroup(String name) {
            return new GroupBlockInstance(name);
        }

        public abstract @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups);

        protected enum Type {
            STATE(StateBlockInstance.CODEC),
            TAG(TagBlockInstance.CODEC),
            GROUP(GroupBlockInstance.CODEC);

            private final MapCodec<? extends BlockInstance> codec;

            Type(MapCodec<? extends BlockInstance> codec) {
                this.codec = codec;
            }

            public MapCodec<? extends BlockInstance> getCodec() {
                return codec;
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
            public @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups) {
                if (tags.isEmpty()) {
                    if (groups.isEmpty())
                        return this;
                    return BlockInstance.forGroup(groups.getFirst());
                }
                return BlockInstance.forTag(tags.getFirst());
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
            public @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups) {
                int i = tags.indexOf(tagKey);
                if (i == -1) {
                    if (tags.isEmpty()) {
                        if (groups.isEmpty())
                            return BlockInstance.forState(state);
                        return BlockInstance.forGroup(groups.getFirst());
                    }
                    return BlockInstance.forTag(tags.getFirst());
                }
                if (i >= tags.size() - 1) {
                    if (groups.isEmpty())
                        return BlockInstance.forState(state);
                    return BlockInstance.forGroup(groups.getFirst());
                }
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
            public @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups) {
                int i = groups.indexOf(this.groupName);
                if (i == -1 || i >= groups.size() - 1)
                    return BlockInstance.forState(state);
                return BlockInstance.forGroup(groups.get(i + 1));
            }
        }
    }
}
