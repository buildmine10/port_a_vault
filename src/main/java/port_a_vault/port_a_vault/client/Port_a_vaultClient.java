package port_a_vault.port_a_vault.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.gui.HubGuiDescription;
import port_a_vault.port_a_vault.gui.HubScreen;

@Environment(EnvType.CLIENT)
public class Port_a_vaultClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.<HubGuiDescription, HubScreen>register(Port_a_vault.HUB_SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new HubScreen(gui, inventory.player, title));
    }
}
