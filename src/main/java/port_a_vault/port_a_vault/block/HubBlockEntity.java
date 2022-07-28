package port_a_vault.port_a_vault.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.gui.HubGui;
import port_a_vault.port_a_vault.gui.HubScreen;
import port_a_vault.port_a_vault.gui.TerminalGui;
import port_a_vault.port_a_vault.gui.TerminalScreen;

public class HubBlockEntity extends BlockEntity {

    String pos;

    protected HubBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        pos = blockPos.toShortString();
        //items = NetworkGlobals.getChest(blockPos);
    }
    public HubBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(Port_a_vault.HUB_BLOCK_ENTITY, blockPos, blockState);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // You need a Block.createScreenHandlerFactory implementation that delegates to the block entity,
        // such as the one from BlockWithEntity
        MinecraftClient.getInstance().setScreen(new HubScreen(new HubGui()));
        return ActionResult.SUCCESS;
    }


}
