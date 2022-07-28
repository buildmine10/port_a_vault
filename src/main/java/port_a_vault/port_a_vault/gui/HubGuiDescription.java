package port_a_vault.port_a_vault.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import port_a_vault.port_a_vault.Port_a_vault;

public class HubGuiDescription extends SyncedGuiDescription {

    private static final int INVENTORY_SIZE = 27;

    public HubGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Port_a_vault.HUB_SCREEN_HANDLER_TYPE , syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));

        WGridPanel root = (WGridPanel) rootPanel;
        WBox box = new WBox(Axis.VERTICAL);

        for (int i = 0; i < 20; i++) {
            box.add(new WLabeledSlider(0, 10, Text.literal("Slider #" + i)));
        }

        box.add(new WButton(new ItemIcon(Items.APPLE)));

        root.add(new WLabel(Text.literal("Scrolling test")).setVerticalAlignment(VerticalAlignment.CENTER), 0, 0, 5, 2);
        root.add(new WScrollPanel(box), 0, 2, 5, 3);

        root.validate(this);
    }
}
