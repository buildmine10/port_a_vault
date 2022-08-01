package port_a_vault.port_a_vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.util.Chest;
import port_a_vault.port_a_vault.util.InventoryManager;

public class ChestGenerator extends Block {
    public ChestGenerator(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            InventoryManager manager = Port_a_vault.inventoryManager;
            for(int x = 0; x < 20; x++){
                for(int y = 0; y < 20; y++){
                    for(int z = 0; z < 20; z++){
                        world.setBlockState(pos.add(x, y, z), Port_a_vault.CUSTOM_CHEST_BLOCK.getDefaultState());
                        manager.addChest(pos.add(x, y, z).toShortString());
                        manager.setChestChannel(manager.getChest(pos.add(x, y, z).toShortString()), pos.toShortString());
                    }
                }
            }

            var chests = manager.getChestsOnChannel(pos.toShortString());
            for(var chest : chests){
                for(var stack : chest.inventory){
                    stack.setData(new ItemStack(Item.byRawId((int)Math.floor(Math.random() * 100)), (int)(Math.random() * 64)));
                }
                //manager.setChestChannel(chest, "");
            }
        }

        return ActionResult.SUCCESS;
    }
}
