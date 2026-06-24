package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.IdMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MultiblockStructureConfiguration {
    private static final PalettedContainer.Strategy STRATEGY = new PalettedContainer.Strategy() {
        @Override
        public <A> PalettedContainer.Configuration<A> getConfiguration(IdMap<A> registry, int size) {
            return null;
        }
    };

    private final Map<String, BlockGroup> groups;

    private final List<BlockInstance> blockLookup;
    private final PalettedContainer<BlockInstance> content;

    private MultiblockStructureConfiguration(Map<String, BlockGroup> groups, List<BlockInstance> blockLookup) {
        this.groups = groups;
        this.blockLookup = blockLookup;
        this.blockLookup.add(BlockInstance.EmptyBlockInstance.INSTANCE);
        this.content = new PalettedContainer<>(new LookupMap(), BlockInstance.EmptyBlockInstance.INSTANCE, );
    }

    private final class LookupMap implements IdMap<BlockInstance> {

        @Override
        public int getId(BlockInstance value) {
            return blockLookup.indexOf(value);
        }

        @Override
        public @Nullable BlockInstance byId(int id) {
            return blockLookup.get(id);
        }

        @Override
        public int size() {
            return blockLookup.size();
        }

        @Override
        public @NotNull Iterator<BlockInstance> iterator() {
            return blockLookup.listIterator();
        }
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

        public abstract @NotNull BlockInstance cycle(BlockState state, List<TagKey<Block>> tags, List<String> groups, Player player);

        public boolean isState() {
            return false;
        }

        protected enum Type {
            STATE(StateBlockInstance.CODEC),
            TAG(TagBlockInstance.CODEC),
            GROUP(GroupBlockInstance.CODEC),
            EMPTY(EmptyBlockInstance.CODEC);

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
