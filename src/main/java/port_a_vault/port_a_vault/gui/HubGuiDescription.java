package port_a_vault.port_a_vault.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import port_a_vault.port_a_vault.Port_a_vault;

public class HubGuiDescription extends SyncedGuiDescription {

    private static final int INVENTORY_SIZE = 27;

    public HubGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Port_a_vault.HUB_SCREEN_HANDLER_TYPE , syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));

        WGridPanel root = new WGridPanel(9);
        setRootPanel(root);
        root.setSize(18 * 9, 175);
        root.setInsets(Insets.ROOT_PANEL);

        for(int x = 0; x < 9; x++){
            for(int y = 0; y < 3; y++){
                WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
                root.add(itemSlot, 2*x, 2*y + 1);


            }
        }

        root.add(this.createPlayerInventoryPanel(), 0, 2*4);
        root.validate(this);
    }
}
