package port_a_vault.port_a_vault;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.PersistentState;
import port_a_vault.port_a_vault.block.LinkedVariable;

import java.util.HashMap;

public class NetworkGlobals extends PersistentState {

    public static int rows = 3;
    public HashMap<String, DefaultedList<LinkedVariable<ItemStack>>> chests = new HashMap<>();

    boolean isUsingRedBlackTree = true;

    public static boolean areAllChestsLinked = false;

    public void addChest(String pos){
        //System.out.print("addedChest: ");
        //System.out.println(pos);

        if(areAllChestsLinked)
            pos = "0";//override to make all chests linked

        DefaultedList<LinkedVariable<ItemStack>> items = DefaultedList.ofSize(rows * 9, new LinkedVariable<>(ItemStack.EMPTY));
        for(int i = 0; i < rows * 9; i++){
            items.set(i, new LinkedVariable<>(new ItemStack((Item)null)));
        }
        chests.putIfAbsent(pos, items);
    }

    public void removeChest(String pos){
        //System.out.print("removedChest: ");
        //System.out.println(pos);
        if(areAllChestsLinked)
            pos = "0";//override to make all chests linked

        chests.get(pos).forEach((LinkedVariable::delete));
        chests.remove(pos);
    }

    public DefaultedList<LinkedVariable<ItemStack>> getChest(String pos){
        //System.out.print("getChest: ");
        //System.out.println(pos);
        if(areAllChestsLinked)
            pos = "0";//override to make all chests linked

        return chests.get(pos);
    }

    public NetworkGlobals(){
        super();
    }


    private static DefaultedList<ItemStack> toList(DefaultedList<LinkedVariable<ItemStack>> inv) {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);
        for (int i = 0; i < inv.size(); i++)
            stacks.set(i, inv.get(i).getData());
        return stacks;
    }

    private static DefaultedList<LinkedVariable<ItemStack>> fromList(NbtCompound tag) {
        DefaultedList<LinkedVariable<ItemStack>> inventory = DefaultedList.ofSize(rows * 9, new LinkedVariable<>(ItemStack.EMPTY));
        for(int i = 0; i < rows * 9; i++){
            inventory.set(i, new LinkedVariable<>(new ItemStack((Item)null)));
        }

        //reads the data
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        Inventories.readNbt(tag, stacks);

        //puts the data in the proper format
        for (int i = 0; i < stacks.size(); i++)
            inventory.get(i).setData(stacks.get(i));
        return inventory;
    }

    //this function only runs if it is marked dirty
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound inventories = new NbtCompound();

        for(String key : chests.keySet()){
            if (!chests.get(key).isEmpty())
                inventories.put(key, Inventories.writeNbt(new NbtCompound(), toList(chests.get(key))));
        }

        nbt.put("inventories", inventories);
        return nbt;
    }

    public static PersistentState readNbt(NbtCompound nbt) {
        NetworkGlobals network = new NetworkGlobals();

        NbtCompound inventories = nbt.getCompound("inventories");
        for (String key : inventories.getKeys()) {
            network.chests.put(key, fromList(inventories.getCompound(key)));
        }

        return network;
    }


}
