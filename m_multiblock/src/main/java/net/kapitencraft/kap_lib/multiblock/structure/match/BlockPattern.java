package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Function;

public class BlockPattern {

    private final int quantifierCount;
    final Node root;

    BlockPattern(int quantifierCount, Node root) {
        this.quantifierCount = quantifierCount;
        this.root = root;
    }

    public static BlockPattern build(List<Quantifier> quantifiers, Function<Integer, MultiblockStructureConfiguration.BlockInstance> blockAccessor, int size) {

        int quantifierOrdinal = 0;
        Node root = null;
        for (int i = 0; i < size; i++) {

            //we'll be doing recursive quantifiers later
            Quantifier quantifier = findQuantifier(i, quantifiers);
            if (quantifier != null) {
                Node qNode = null;
                while (i <= quantifier.toPosition()) {
                    LiteralNode literalNode = new LiteralNode(blockAccessor.apply(i));
                    if (qNode != null) {
                        qNode.next = literalNode;
                    }
                    qNode = literalNode;
                    i++;
                }
                QuantifiedNode n = new QuantifiedNode(qNode, quantifier, quantifierOrdinal++);
                if (root != null) {
                    root.next = n;
                }
                root = n;
            } else {
                LiteralNode literalNode = new LiteralNode(blockAccessor.apply(i));
                if (root != null) {
                    root.next = literalNode;
                }
                root = literalNode;
            }
        }
        return new BlockPattern(quantifierOrdinal, root);
    }

    private static Quantifier findQuantifier(int offset, List<Quantifier> quantifiers) {
        for (Quantifier quantifier : quantifiers) {
            if (quantifier.fromPosition() == offset && quantifier.toPosition() == offset) {
                return quantifier;
            }
        }
        return null;
    }

    public BlockMatcher matcher(Level level) {

        return new BlockMatcher(quantifierCount, this, level);
    }

    public record BlockAccessor(Direction direction, BlockPos origin, Level level) {

        public BlockState getBlock(int idx) {
            return level.getBlockState(origin.relative(direction, idx));
        }
    }

    //region nodes

    /**
     * base pattern node. stores the next node for the next element
     */
    abstract static class Node {
        protected Node next;

        /**
         * @param accessor block accessor, wrapper around the direction to convert a 3d space into usable 1d space for the state machine
         * @param idx      index in the folded 1d space
         * @return whether this node found a match
         */
        abstract boolean matches(BlockMatcher matcher, BlockAccessor accessor, int idx);
    }

    /**
     * simple literal node
     */
    private static class LiteralNode extends Node {
        private final MultiblockStructureConfiguration.BlockInstance blockInstance;

        protected LiteralNode(MultiblockStructureConfiguration.BlockInstance blockInstance) {
            this.blockInstance = blockInstance;
        }

        @Override
        public boolean matches(BlockMatcher matcher, BlockAccessor accessor, int idx) {
            return blockInstance.isValid(accessor.getBlock(idx)) && (next == null || next.matches(matcher, accessor, idx + 1));
        }
    }

    /**
     * quantified node
     */
    private static class QuantifiedNode extends Node {
        private final Node element;
        private final Quantifier instance;
        private final int ordinal;

        protected QuantifiedNode(Node element, Quantifier instance, int ordinal) {
            this.element = element;
            this.instance = instance;
            this.ordinal = ordinal;
        }

        @Override
        protected boolean matches(BlockMatcher matcher, BlockAccessor accessor, int idx) {
            int c = 0;
            List<Integer> calculatedIndexes = matcher.quantifierList[ordinal];
            while (c <= instance.maxCount() || instance.maxCount() == -1) {
                if (c >= instance.minCount() && (next == null || next.matches(matcher, accessor, idx))) {
                    calculatedIndexes.add(c);
                }
                if (!this.element.matches(matcher, accessor, idx)) {
                    return !calculatedIndexes.isEmpty();
                }
                c++;
                idx++;
            }
            return false;
        }
    }
    //endregion
}
