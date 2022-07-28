package port_a_vault.port_a_vault.block;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.util.InventoryManager;

public class CustomChestScreenHandler extends GenericContainerScreenHandler {

    public CustomChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory){
        super(Port_a_vault.CUSTOM_CHEST_SCREEN_HANDLER, syncId, playerInventory, inventory, InventoryManager.rowsPerChest);
    }

    public CustomChestScreenHandler(int syncId, PlayerInventory playerInventory){
        super(Port_a_vault.CUSTOM_CHEST_SCREEN_HANDLER, syncId, playerInventory, new SimpleInventory(9 * InventoryManager.rowsPerChest), InventoryManager.rowsPerChest);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return Port_a_vault.CUSTOM_CHEST_SCREEN_HANDLER;
    }
}
