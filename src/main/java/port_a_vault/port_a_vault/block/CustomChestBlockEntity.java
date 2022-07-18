package port_a_vault.port_a_vault.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import port_a_vault.port_a_vault.NetworkGlobals;
import port_a_vault.port_a_vault.Port_a_vault;

public class CustomChestBlockEntity extends LootableContainerBlockEntity {

    String pos;

    private DefaultedList<LinkedVariable<ItemStack>> getItems(){
        return NetworkGlobals.getChest(pos);
    }
    protected CustomChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        pos = blockPos.toShortString();
        //items = NetworkGlobals.getChest(blockPos);
    }

    public CustomChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(Port_a_vault.CUSTOM_CHEST_ENTITY, blockPos, blockState);
    }

    //this might cause issues
    //copies over the linked item stacks into a list of unlinked item stacks
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        DefaultedList<ItemStack> output = DefaultedList.ofSize(getItems().size(), ItemStack.EMPTY);

        for(int i = 0; i < getItems().size(); i++){
            output.set(i, getItems().get(i).getData());
        }

        return output;
    }

    //this might cause issues
    //this changes the data in the linked item stacks
    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        if(list.size() == getItems().size()){
            for(int i = 0; i < getItems().size(); i++){
                getItems().get(i).setData(list.get(i));
            }
        }
    }

    @Override
    protected Text getContainerName() {
        return Text.literal("name of container");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        //System.out.println(items.size());
        return new CustomChestScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return 3 * 9;
    }


    public boolean isEmpty() {
        this.checkLootInteraction(null);
        return this.getItems().stream().allMatch((item)->{return item.isDeleted() || item.getData().isEmpty();});
    }

    public ItemStack getStack(int slot) {
        this.checkLootInteraction(null);
        return this.getItems().get(slot).getData();
    }

    public ItemStack removeStack(int slot, int amount) {
        this.checkLootInteraction(null);
        //the next line might be the source of a bug. This is Inventories.splitStack() remade for my wrapper class
        ItemStack itemStack = slot >= 0 && slot < getItems().size() && !(getItems().get(slot).isDeleted() || (getItems().get(slot).getData()).isEmpty()) && amount > 0 ? (getItems().get(slot).getData()).split(amount) : ItemStack.EMPTY;
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }


    public ItemStack removeStack(int slot) {
        this.checkLootInteraction(null);
        //the next line might be the source of a bug. This is Inventories.removeStack() remade for my wrapper class
        return slot >= 0 && slot < getItems().size() ? getItems().get(slot).setData(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public void setStack(int slot, ItemStack stack) {
        this.checkLootInteraction(null);
        this.getItems().get(slot).setData(stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        this.markDirty();
    }

    public void clear() {
        this.getItems().forEach(LinkedVariable::delete);
        this.getItems().clear();
    }
}
