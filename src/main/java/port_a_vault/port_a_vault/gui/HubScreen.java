package port_a_vault.port_a_vault.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class HubScreen extends CottonInventoryScreen<HubGuiDescription> {

    public HubScreen(HubGuiDescription gui, PlayerEntity player, Text title) {
        super(gui, player, title);
    }
}
