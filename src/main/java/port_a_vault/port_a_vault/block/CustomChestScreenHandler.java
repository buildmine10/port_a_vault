package port_a_vault.port_a_vault.block;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import port_a_vault.port_a_vault.NetworkGlobals;
import port_a_vault.port_a_vault.Port_a_vault;

public class CustomChestScreenHandler extends GenericContainerScreenHandler {

    public CustomChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory){
        super(Port_a_vault.CUSTOM_CHEST_SCREEN_HANDLER, syncId, playerInventory, inventory, NetworkGlobals.rows);
    }

    public CustomChestScreenHandler(int syncId, PlayerInventory playerInventory){
        super(Port_a_vault.CUSTOM_CHEST_SCREEN_HANDLER, syncId, playerInventory, new SimpleInventory(9 * NetworkGlobals.rows), NetworkGlobals.rows);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return Port_a_vault.CUSTOM_CHEST_SCREEN_HANDLER;
    }
}
