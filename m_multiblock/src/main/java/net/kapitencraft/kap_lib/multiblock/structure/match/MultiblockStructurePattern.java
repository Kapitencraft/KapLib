package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class MultiblockStructurePattern {
    public static MultiblockStructurePattern build(MultiblockStructureConfiguration structure) {
        Map<Direction.Axis, List<Quantifier>> quantifiers = structure.getQuantifiers();

        Vec3i size = structure.getSize();

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    Quantifier quantifier = findQuantifier(z, quantifiers.get(Direction.Axis.Z));
                    if (quantifier != null) {
                        
                    }
                }
            }
        }
    }

    private static Quantifier findQuantifier(int offset, List<Quantifier> quantifiers) {
        for (Quantifier quantifier : quantifiers) {
            if (quantifier.fromPosition() == offset && quantifier.toPosition() == offset) {
                return quantifier;
            }
        }
        return null;
    }

    /**
     * base pattern node. stores the next node for the next element
     */
    private abstract static class Node {
        protected final Node next;

        protected Node(Node next) {
            this.next = next;
        }

        protected abstract boolean matches(BlockPos.MutableBlockPos pos, Level level);
    }

    /**
     * branch node to increase dimensions
     */
    private static class BranchNode extends Node {
        private final Node branch;

        protected BranchNode(Node next, Node branch) {
            super(next);
            this.branch = branch;
        }

        @Override
        protected boolean matches(BlockPos.MutableBlockPos pos, Level level) {

            return false;
        }
    }

    /**
     * simple literal node
     */
    private static class LiteralNode extends Node {
        private final MultiblockStructureConfiguration.BlockInstance blockInstance;

        protected LiteralNode(Node next, MultiblockStructureConfiguration.BlockInstance blockInstance) {
            super(next);
            this.blockInstance = blockInstance;
        }

        @Override
        public boolean matches(BlockPos.MutableBlockPos pos, Level level) {
            return blockInstance.isValid(level.getBlockState(pos));
        }
    }

    /**
     * quantified node
     */
    private static class QuantifiedNode extends Node {
        private final Node element;
        private final Quantifier instance;

        protected QuantifiedNode(Node next, Node element, Quantifier instance) {
            super(next);
            this.element = element;
            this.instance = instance;
        }

        @Override
        protected boolean matches(BlockPos.MutableBlockPos pos, Level level) {
            int c = 0;
            while (c < instance.maxCount() || instance.maxCount() == -1) {

                c++;
            }
            return false;
        }
    }

}
