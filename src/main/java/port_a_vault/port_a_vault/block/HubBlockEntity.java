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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.gui.HubGuiDescription;
import port_a_vault.port_a_vault.util.AccessPointBackend;
import port_a_vault.port_a_vault.util.BigStack;
import port_a_vault.port_a_vault.util.Chest;

import java.util.*;

public class HubBlockEntity extends LootableContainerBlockEntity implements NamedScreenHandlerFactory, Inventory, PropertyDelegateHolder {

    String channel = "";
    String searchQuery = "";
    int scrollAmount = 0;
    public boolean isUsingAlphabetical = false;
    public boolean isAccengingSort = false;
    DefaultedList<ItemStack> displayStacks = DefaultedList.ofSize(9 * 5, ItemStack.EMPTY);
    public AccessPointBackend backend = new AccessPointBackend();

    ArrayList<BigStack> items = new ArrayList<>();

    protected HubBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        //setChannel(channel);
        //backend.generateTrees();
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
            Port_a_vault.inventoryManager.markDirty();
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
        Port_a_vault.inventoryManager.markDirty();
        markDirty();
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
        if (scrollAmount < 0) scrollAmount = 0;
        if(items.size() / 9 > 0 && scrollAmount > items.size() / 9){
            scrollAmount = items.size() / 9;
        }
        //System.out.println(scrollAmount);
        updateDisplayList();
    }

    public void updateDisplayList(){
        //System.out.println("Updating List");

        putAway();
        //displayStack

        backend.generateTrees();

        items = backend.toBigStacks(backend.search(searchQuery, ""));

        //This is only done because for some reason it doesn't come out sorted when using redblack trees.
        //It gets all the items, but somehow not sorted
        //This doesn't make sense because the search relies on the data being sorted
        //The search wouldn't work if it wasn't sorted. So at some point it gets unsorted
        //Probably in the traversal that gets the items from the search result
        //The way that items are added to the list might not be stable
        if(backend.isUsingRedBlackTree()){
            items.sort((a, b)->{
                return a.getItem().getName().getString().compareTo(b.getItem().getName().getString());
            });
        }

        //default sort is alphabetical
        //max to min item count sort
        if(!isUsingAlphabetical){
            items.sort((a, b)->{
                return a.getCount() - b.getCount();
            });
        }

        if(!isAccengingSort){
            Collections.reverse(items);
        }


        //traverse this in reverse to get the reversed sort
        for(int i = 0; i < size(); i++){
            if (scrollAmount >= 0) {
                if (i + scrollAmount * 9 < items.size()) {
                    displayStacks.set(i, backend.removeStack(items.get(i + scrollAmount * 9)));
                } else {
                    displayStacks.set(i, ItemStack.EMPTY);
                }
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
        //backend.generateTrees();//only needed because of inadequate tracking of air item stacks//but this crashes the game (For some reason)
        for(ItemStack stack : displayStacks){
            if(stack.getItem() != Items.AIR){
                //System.out.println(stack.getName().getString() + ": " + stack.getCount());
                backend.insertStack(stack);
            }
        }

        Port_a_vault.inventoryManager.markDirty();
    }


    public String getChannel(){
        return channel;
    }
    public void setChannel(String channel){
        this.channel = channel;
        backend.setChannel(channel);

        updateChannel();
        this.markDirty();
    }


    ArrayDeque<BlockPos> placesToUpdate = new ArrayDeque<>();
    private void updateChannel(){
        //System.out.println("UPDATEING CHANNEL");
        placesToUpdate.clear();
        placesToUpdate.add(pos.add(1, 0, 0));
        placesToUpdate.add(pos.add(-1, 0, 0));
        placesToUpdate.add(pos.add(0, 0, 1));
        placesToUpdate.add(pos.add(0, 0, -1));
        placesToUpdate.add(pos.add(0, 1, 0));
        placesToUpdate.add(pos.add(0, -1, 0));
        while(!placesToUpdate.isEmpty()){
            BlockPos currentPos = placesToUpdate.pop();
            //System.out.println(currentPos.toShortString());
            Chest chest = Port_a_vault.inventoryManager.getChest(currentPos.toShortString());
            if(chest != null){
                //System.out.println("Not null: " + currentPos.toShortString() + " - " + chest.channel);
                if(chest.channel.compareTo(channel) != 0){//if the chest is not on the same channel
                    //System.out.println("Changing: " + currentPos.toShortString());
                    Port_a_vault.inventoryManager.setChestChannel(chest, channel);
                    //chest.channel = channel;
                    placesToUpdate.add(currentPos.add(1, 0, 0));
                    placesToUpdate.add(currentPos.add(-1, 0, 0));
                    placesToUpdate.add(currentPos.add(0, 0, 1));
                    placesToUpdate.add(currentPos.add(0, 0, -1));
                    placesToUpdate.add(currentPos.add(0, 1, 0));
                    placesToUpdate.add(currentPos.add(0, -1, 0));
                }
            }
        }

        //System.out.println("done changing");
        //for(var chest : Port_a_vault.inventoryManager.chests.values()){
        //    System.out.println(chest.getPos() + ": " + Port_a_vault.inventoryManager.getChest(chest.getPos()));
        //}
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        tag.putString("channel", channel);
        tag.putBoolean("isAlphabetical", isUsingAlphabetical);
        tag.putBoolean("isAscending", isAccengingSort);

        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        channel = tag.getString("channel");
        isUsingAlphabetical = tag.getBoolean("isAlphabetical");
        isAccengingSort = tag.getBoolean("isAscending");
        backend.setChannel(channel);
    }
}
