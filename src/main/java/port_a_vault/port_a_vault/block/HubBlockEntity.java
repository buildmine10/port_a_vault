package port_a_vault.port_a_vault.block;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import port_a_vault.port_a_vault.Port_a_vault;

public class HubBlockEntity extends BlockEntity implements BlockEntityProvider, HubInventory, NamedScreenHandlerFactory
{
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public HubBlockEntity(BlockPos pos, BlockState state)
    {
        super(Port_a_vault.HUB_BLOCK_ENTITY, pos, state);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new HubBlockEntity(pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        Inventories.writeNbt(tag, items);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
    }

    public DefaultedList<ItemStack> getItems()
    {
        return items;
    }


    //These Methods are from the NamedScreenHandlerFactory Interface
    //createMenu creates the ScreenHandler itself
    //getDisplayName will Provide its name which is normally shown at the top

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide *this* to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new HubScreenHandler(syncId, playerInventory, this);
    }

    // IDK
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.hub");
    }

    // CALL .markDirty() IF ANY NBT DATA CHANGES
}

