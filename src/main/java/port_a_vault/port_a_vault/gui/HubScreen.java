package port_a_vault.port_a_vault.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class HubScreen extends CottonInventoryScreen<HubGuiDescription> {
    HubGuiDescription gui;
    public HubScreen(HubGuiDescription gui_in, PlayerEntity player, Text title) {
        super(gui_in, player, title);
        gui = gui_in;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        gui.getEntity().scroll((int)amount * -1);
        gui.scrollBar.onMouseScroll((int)mouseX, (int)mouseY, amount);
        System.out.println(amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

}
