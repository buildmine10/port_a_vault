package port_a_vault.port_a_vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import port_a_vault.port_a_vault.Port_a_vault;

public class Test extends Block {

    boolean used = false;
    public Test(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            Port_a_vault.inventoryManager.backend.generateTrees();
            Port_a_vault.inventoryManager.backend.forTest(world, pos, player, hand);
        }

        return ActionResult.SUCCESS;
    }
}
