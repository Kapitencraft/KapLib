package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.SectionQuantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
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

    public static BlockPattern build(List<SectionQuantifier> sectionQuantifiers, Function<Integer, MultiblockStructureConfiguration.BlockInstance> blockAccessor, int size) {

        int quantifierOrdinal = 0;
        Node root = null;
        Node current = null;
        for (int i = 0; i < size; i++) {

            //we'll be doing recursive quantifiers later
            SectionQuantifier sectionQuantifier = SectionQuantifier.findQuantifier(i, sectionQuantifiers);
            Node n;
            if (sectionQuantifier != null) {
                Node qRoot = null;
                Node qCurrent = null;
                while (i <= sectionQuantifier.toPosition()) {
                    LiteralNode literalNode = new LiteralNode(blockAccessor.apply(i));
                    if (qCurrent != null) {
                        qCurrent.next = literalNode;
                    }
                    if (qRoot == null) {
                        qRoot = literalNode;
                    }
                    qCurrent = literalNode;
                    i++;
                }
                n = new QuantifiedNode(qRoot, sectionQuantifier, quantifierOrdinal++);
                i--; //move back for the for loop
            } else {
                n = new LiteralNode(blockAccessor.apply(i));
            }
            if (current != null) {
                current.next = n;
            }
            if (root == null) {
                root = n;
            }
            current = n;
        }
        return new BlockPattern(quantifierOrdinal, root);
    }


    public BlockMatcher matcher(LevelAccessor level) {

        return new BlockMatcher(quantifierCount, this, level);
    }

    public record BlockAccessor(Direction direction, BlockPos origin, LevelAccessor level) {

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
            matcher.last = idx;
            return blockInstance.isValid(accessor.getBlock(idx)) && (next == null || next.matches(matcher, accessor, idx + 1));
        }
    }

    /**
     * quantified node
     */
    private static class QuantifiedNode extends Node {
        private final Node element;
        private final SectionQuantifier instance;
        private final int ordinal;

        protected QuantifiedNode(Node element, SectionQuantifier instance, int ordinal) {
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
                idx = matcher.last + 1;
                c++;
            }
            return false;
        }
    }
    //endregion
}
