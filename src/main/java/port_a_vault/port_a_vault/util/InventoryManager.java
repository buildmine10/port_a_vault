package port_a_vault.port_a_vault.util;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.HashSet;


public class InventoryManager extends PersistentState {

    private static DefaultedList<ItemStack> toList(DefaultedList<LinkedVariable<ItemStack>> inv) {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);
        for (int i = 0; i < inv.size(); i++)
            stacks.set(i, inv.get(i).getData());
        return stacks;
    }

    private static DefaultedList<LinkedVariable<ItemStack>> fromList(NbtCompound tag) {
        DefaultedList<LinkedVariable<ItemStack>> inventory = DefaultedList.ofSize(rowsPerChest * 9, new LinkedVariable<>(ItemStack.EMPTY));
        for(int i = 0; i < rowsPerChest * 9; i++){
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound chestsNbt = new NbtCompound();

        for(String pos : chests.getKeys()){
            //System.out.println(chests.get(pos));
            chestsNbt.put(pos, chests.get(pos).toNbt());
        }

        nbt.put("chests", chestsNbt);
        return nbt;
    }

    public static PersistentState readNbt(NbtCompound nbt) {
        InventoryManager manager = new InventoryManager();

        NbtCompound chestsNbt = nbt.getCompound("chests");
        for(String pos : chestsNbt.getKeys()){
            Chest chest = new Chest(chestsNbt.getCompound(pos), pos);
            manager.chests.put(pos, chest);
            manager.chestChannels.putIfAbsent(chest.channel, new UnorderedSet<>());
            manager.chestChannels.get(chest.channel).put(chest);
        }

        manager.printChestsOnChannel("");
        return manager;
    }



    public static final int rowsPerChest = 3;
    public UnorderedMap<Chest> chests = new UnorderedMap<>();//chest coordinates to chest data
    private UnorderedMap<UnorderedSet<Chest>> chestChannels = new UnorderedMap<>();


    public Chest getChest(String pos){
        return chests.get(pos);
    }

    public void addChest(String pos){
        Chest chest = new Chest(pos);
        if(chests.putIfAbsent(pos, chest) == null){//if it needed to be created
            chestChannels.putIfAbsent(chest.channel, new UnorderedSet<>());
            chestChannels.get(chest.channel).put(chest);
        }

        markDirty();
    }

    public void removeChest(String pos){
        //System.out.println("removed chest");
        chestChannels.getOrDefault(chests.get(pos).channel, new UnorderedSet<>()).remove(chests.get(pos));
        if(chestChannels.getOrDefault(chests.get(pos).channel, new UnorderedSet<>()).isEmpty()){
            chestChannels.remove(chests.get(pos).channel);
        }
        chests.get(pos).delete();
        chests.remove(pos);
        markDirty();
    }

    public void printUsedChannels(){
        UnorderedSet<String> channels = new UnorderedSet<>();

        System.out.println("Listing channels");
        for(Chest chest : chests.getValues()){
            if(!channels.contains(chest.channel)){
                channels.put(chest.channel);
                System.out.println("\t" + chest.channel);
            }
        }
        System.out.println("Done listing channels");
    }

    public void printChestsOnChannel(String channel){
        UnorderedSet<Chest> chests = getChestsOnChannel(channel);
        System.out.println("Printing chests on channel: " + channel);
        for(Chest chest : chests){
            System.out.println("\t" + Chest.coordToString(chest.x, chest.y, chest.z));
        }
        System.out.println("Done printing chests on channel: " + channel);
    }

    public UnorderedSet<Chest> getChestsOnChannel(String channel){
        return chestChannels.getOrDefault(channel, new UnorderedSet<>());
    }

    public void setChestChannel(Chest chest, String channel){
        //System.out.println(chest.channel + " | " + channel);
        chestChannels.get(chest.channel).remove(chest);
        chestChannels.putIfAbsent(channel, new UnorderedSet<>());
        chestChannels.get(channel).put(chest);
        chest.channel = channel;
    }
}
