package port_a_vault.port_a_vault.block;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.Nullable;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.gui.HubGuiDescription;
import port_a_vault.port_a_vault.util.AccessPointBackend;
import port_a_vault.port_a_vault.util.BigStack;

import java.util.ArrayList;

public class HubBlockEntity extends LootableContainerBlockEntity implements NamedScreenHandlerFactory, Inventory, PropertyDelegateHolder {

    String channel = "";
    String searchQuery = "";
    int scrollAmount = 0;
    DefaultedList<ItemStack> displayStacks = DefaultedList.ofSize(9 * 5, ItemStack.EMPTY);
    public AccessPointBackend backend = new AccessPointBackend();

    ArrayList<BigStack> items = new ArrayList<>();

    protected HubBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        backend.setChannel(channel);
        backend.generateTrees();
        updateDisplayList();
    }
    public HubBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(Port_a_vault.HUB_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("");
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return super.createMenu(syncId, inv, player);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        updateDisplayList();
        backend.generateTrees();
        HubGuiDescription out = new HubGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, this.getPos()));

        return out;
    }

    @Override
    public int size() {
        return 9 * 5;
    }

    @Override
    public boolean isEmpty() {
        this.checkLootInteraction(null);
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        this.checkLootInteraction(null);

        //if(slot + scrollAmount * 9 < items.size()){
        //    return new ItemStack(items.get(slot + scrollAmount * 9).getItem(),items.get(slot + scrollAmount * 9).getCount());//
        //}else {
        //    return ItemStack.EMPTY;
        //}

        return displayStacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.checkLootInteraction(null);
        //ItemStack out = backend.removeCount(items.get(slot + scrollAmount * 9), amount);
        updateDisplayList();
        //Port_a_vault.inventoryManager.markDirty();
        //return out;
        //return displayStacks.get(slot);//new ItemStack(Items.AIR);//

        ItemStack result = Inventories.splitStack(getInvStackList(), slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        //System.out.println("hi");
        this.checkLootInteraction(null);
        //ItemStack out = backend.removeStack(items.get(slot + scrollAmount * 9));
        updateDisplayList();
        //Port_a_vault.inventoryManager.markDirty();
        //return out;
        //return displayStacks.get(slot);//new ItemStack(Items.AIR);//
        return Inventories.removeStack(getInvStackList(), slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        //System.out.println(slot);
        this.checkLootInteraction(null);
        this.displayStacks.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        this.markDirty();
        Port_a_vault.inventoryManager.markDirty();
        updateDisplayList();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {

    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return displayStacks;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        displayStacks = list;//.get(slot);
    }

    public void scroll(int amount){
        scrollAmount += amount;
        updateDisplayList();
    }

    public void updateDisplayList(){
        //System.out.println("Updating List");

        putAway();
        //displayStack

        backend.generateTrees();

        items = backend.toBigStacks(backend.search(searchQuery, ""));


        //default sort is alphabetical
        //max to min item count sort
        items.sort((a, b)->{
            return b.getCount() -a.getCount();
        });


        //traverse this in reverse to get the reversed sort
        for(int i = 0; i < size(); i++){
            if(i + scrollAmount * 9 < items.size()){
                displayStacks.set(i, backend.removeStack(items.get(i + scrollAmount * 9)));
            }else{
                displayStacks.set(i, ItemStack.EMPTY);
            }

        }
    }

    public void setSearchQuery(String name){
        searchQuery = name;
        //System.out.println(name);

        scrollAmount = 0;
        updateDisplayList();
    }


    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int size() {
            // This is how many properties you have. We have two of them, so we'll return 2.
            return 3;
        }

        @Override
        public int get(int index) {
            // Each property has a unique index that you can choose.
            // Our properties will be 0 for the progress and 1 for the maximum.

            if (index == 0) {
                return getPos().getX();
            } else if (index == 1) {
                return getPos().getY();
            }else if(index == 2){
                return getPos().getZ();
            }

            // Unknown property IDs will fall back to -1
            return -1;
        }

        @Override
        public void set(int index, int value) {
            // This is used on the other side of the sync if you're using extended screen handlers.
            // Generally you'll want to have a working implementation for mutable properties, such as our progress.

        }
    };

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    public void putAway(){
        for(ItemStack stack : displayStacks){
            if(stack.getItem() != Items.AIR){
                //System.out.println(stack.getName().getString() + ": " + stack.getCount());
                backend.insertStack(stack);
            }
        }
    }
}
