package port_a_vault.port_a_vault.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class CustomChestScreenHandler extends ScreenHandler {

    public static ScreenHandlerType<CustomChestScreenHandler> SCREEN_HANDLER;
    private final Inventory inventory; // Chest inventory
    private static final int INVENTORY_SIZE = 27; // 3 rows * 9 cols

    public CustomChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory){
        super(null, syncId);
        this.inventory = inventory;
        checkSize(inventory, INVENTORY_SIZE);
        inventory.onOpen(playerInventory.player);

        // Creating Slots for GUI. A Slot is essentially a corresponding from inventory ItemStacks to the GUI position.
        int i;
        int j;

        // Chest Inventory
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, i * 9 + j, 8 + j * 18, 18 + i * 18));
            }
        }


        // Player Inventory (27 storage + 9 hotbar)
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, i * 9 + j + 9, 8 + j * 18, 18 + i * 18 + 103 + 18));
            }
        }

        for (j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 18 + 161 + 18));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }


}
