package port_a_vault.port_a_vault.gui;
import io.github.cottonmc.cotton.gui.*;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import net.minecraft.screen.ScreenHandlerType;
import port_a_vault.port_a_vault.Port_a_vault;

public class HubGui extends LightweightGuiDescription {
    public HubGui() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(300,200);
    }


}
