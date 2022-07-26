package port_a_vault.port_a_vault.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import port_a_vault.port_a_vault.gui.TerminalGui;
import port_a_vault.port_a_vault.gui.TerminalScreen;

public class Terminal extends Item {
    public Terminal(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        MinecraftClient.getInstance().setScreen(new TerminalScreen(new TerminalGui()));
        return super.use(world, user, hand);
    }
}
