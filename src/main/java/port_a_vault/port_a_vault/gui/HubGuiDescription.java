package port_a_vault.port_a_vault.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import port_a_vault.port_a_vault.Port_a_vault;

public class HubGuiDescription extends SyncedGuiDescription {

    private static final int INVENTORY_SIZE = 9*5;
    public static WScrollBar scrollBar = new WScrollBar(Axis.VERTICAL);

    public HubGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Port_a_vault.HUB_SCREEN_HANDLER_TYPE , syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));
        WGridPanel root = new WGridPanel(3);
        setRootPanel(root);
        //root.setSize(18 * 9, 200);
        root.setInsets(Insets.ROOT_PANEL);

        WGridPanel invenPanel = new WGridPanel();

        //WItemSlot itemSlots = WItemSlot.of(blockInventory, 0, 9, rows);

        for(int x = 0; x < 9; x++){
            for(int y = 0; y < 5; y++){
                WItemSlot itemSlot = WItemSlot.of(blockInventory, x + y * 9);
                invenPanel.add(itemSlot, x, y);
            }
        }

        root.add(invenPanel, 0, 3);
        root.add(scrollBar, 55, 3, 5, 30);



        root.add(this.createPlayerInventoryPanel(), 0, 6*5+4);



        root.validate(this);
    }
}
