package port_a_vault.port_a_vault.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.block.HubScreen;

@Environment(EnvType.CLIENT)
public class Port_a_vaultClient implements ClientModInitializer {
    @Override
    public void onInitializeClient()
    {
        ScreenRegistry.register(Port_a_vault.HUB_SCREEN_HANDLER, HubScreen::new);
    }
}
