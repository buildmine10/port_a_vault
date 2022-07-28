package port_a_vault.port_a_vault.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import io.github.cottonmc.cotton.gui.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import port_a_vault.port_a_vault.gui.HubGui;
import port_a_vault.port_a_vault.gui.HubScreen;
import port_a_vault.port_a_vault.gui.TerminalGui;
import port_a_vault.port_a_vault.gui.TerminalScreen;

public class Hub extends BlockWithEntity {
    public Hub(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HubBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // You need a Block.createScreenHandlerFactory implementation that delegates to the block entity,
        // such as the one from BlockWithEntity
        MinecraftClient.getInstance().setScreen(new HubScreen(new HubGui()));
        return ActionResult.SUCCESS;
    }

}
