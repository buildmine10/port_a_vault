package port_a_vault.port_a_vault;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import port_a_vault.port_a_vault.blocks.CustomChest;
import port_a_vault.port_a_vault.blocks.CustomChestScreenHandler;

public class Port_a_vault implements ModInitializer {
    @Override
    public void onInitialize() {
        CustomChest.register();
        Registry.register(Registry.SCREEN_HANDLER, new Identifier("port_a_vault", "custom_chest_screen_handler"), CustomChestScreenHandler.SCREEN_HANDLER);
    }
}
