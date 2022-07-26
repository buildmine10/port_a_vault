package port_a_vault.port_a_vault.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import port_a_vault.port_a_vault.Port_a_vault;

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
}
