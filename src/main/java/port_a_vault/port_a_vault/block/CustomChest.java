package port_a_vault.port_a_vault.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import port_a_vault.port_a_vault.Port_a_vault;

public class CustomChest extends BlockWithEntity {


    public CustomChest(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        Port_a_vault.inventoryManager.addChest(pos.toShortString());
        return new CustomChestBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) be);
                world.updateComparators(pos, this);
                Port_a_vault.inventoryManager.removeChest(pos.toShortString());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {

            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                //System.out.println("succeeded to make screen handler factory");
                player.openHandledScreen(screenHandlerFactory);
            }else{
                //System.out.println("failed to make screen handler factory");
            }
        }

        return ActionResult.SUCCESS;
    }

}
