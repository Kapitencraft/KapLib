package net.kapitencraft.kap_lib.client.gui;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntityMenu<T extends BlockEntity> extends AbstractContainerMenu {
    private final int slotAmount;
    protected final Level level;
    public final Player player;
    protected final T blockEntity;

    protected BlockEntityMenu(@Nullable MenuType<?> menuType, int containerId, int slotAmount, Inventory inventory, T provider) {
        super(menuType, containerId);
        this.slotAmount = slotAmount;
        this.player = inventory.player;
        this.level = player.level();
        this.blockEntity = provider;
    }

    /**
     * adds the Player Inventory and Hotbar to the slots
     */
    public void addPlayerInventories(Inventory inventory, int xOffset, int yOffset) {
        addPlayerInventory(inventory, xOffset, yOffset);
        addPlayerHotbar(inventory, xOffset, yOffset);
    }

    /**
     * adds the Player Inventory to the slots
     */
    private void addPlayerInventory(Inventory playerInventory, int xOffset, int yOffSet) {
        MiscHelper.repeat(3, i -> MiscHelper.repeat(9, l -> this.addSlot(new Slot(playerInventory, l + i * 9 + 9, xOffset + 8 + l * 18, yOffSet + 84 + i * 18))));
    }

    /**
     * @param playerInventory
     * @param xOffset
     * @param yOffSet
     */
    private void addPlayerHotbar(Inventory playerInventory, int xOffset, int yOffSet) {
        MiscHelper.repeat(9, i -> this.addSlot(new Slot(playerInventory, i, xOffset + 8 + i * 18, yOffSet + 142)));
    }


    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    public static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    public static final int VANILLA_FIRST_SLOT_INDEX = 0;
    public static final int BE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    /**
     * quick move Items
     * override {@code moveItemStackTo} for checking what slot it should insert into
     * <br> (like fuel slot for fuels and normal slots for other ingredients)
     */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, BE_INVENTORY_FIRST_SLOT_INDEX, BE_INVENTORY_FIRST_SLOT_INDEX + slotAmount, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < BE_INVENTORY_FIRST_SLOT_INDEX + slotAmount) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            KapLibMod.LOGGER.warn("Invalid slotIndex: {}", index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    /**
     * @return the {@link ICapabilityProvider CapabilityProvider} this menu contains
     */
    public T getCapabilityProvider() {
        return blockEntity;
    }
}
