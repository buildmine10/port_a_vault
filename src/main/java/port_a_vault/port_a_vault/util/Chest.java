package port_a_vault.port_a_vault.util;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import port_a_vault.port_a_vault.Port_a_vault;

public class Chest {
    public DefaultedList<LinkedVariable<ItemStack>> inventory;
    public String channel = "";
    int x;
    int y;
    int z;

    public Chest(String pos){
        inventory = DefaultedList.ofSize(InventoryManager.rowsPerChest * 9, new LinkedVariable<>(ItemStack.EMPTY));
        for(int i = 0; i < InventoryManager.rowsPerChest * 9; i++){
            inventory.set(i, new LinkedVariable<>(new ItemStack((Item)null)));
        }

        //gets the position
        String[] parts = pos.split(",", 3);
        Integer[] coords = new Integer[3];
        for(int i = 0; i < 3; i++){
            coords[i] = Integer.parseInt(parts[i].trim());
        }

        x = coords[0];
        y = coords[1];
        z = coords[2];

        channel = getAdjacentChannel();
    }

    public Chest(NbtCompound nbt, String pos){
        inventory = DefaultedList.ofSize(InventoryManager.rowsPerChest * 9, new LinkedVariable<>(ItemStack.EMPTY));

        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt.getCompound("inventory"), stacks);
        for(int i = 0; i < InventoryManager.rowsPerChest * 9; i++){
            inventory.set(i, new LinkedVariable<>(stacks.get(i)));
        }

        channel = nbt.getString("channel");

        //gets the position
        String[] parts = pos.split(",", 3);
        Integer[] coords = new Integer[3];
        for(int i = 0; i < 3; i++){
            coords[i] = Integer.parseInt(parts[i].trim());
        }

        x = coords[0];
        y = coords[1];
        z = coords[2];
    }

    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), getUnsafeList()));
        nbt.putString("channel", channel);

        return nbt;
    }

    //invalidates the inventory
    public void delete(){
        inventory.forEach(LinkedVariable::delete);
    }

    public DefaultedList<ItemStack> getUnsafeList(){
        DefaultedList<ItemStack> out = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for(int i = 0; i < inventory.size(); i++){
            out.set(i, inventory.get(i).getData());
        }
        return out;
    }



    public static String coordToString(int x, int y, int z){
        return Integer.toString(x) + ", " + Integer.toString(y) + ", " + Integer.toString(z);
    }

    public String getAdjacentChannel(){
        InventoryManager inventoryManager = Port_a_vault.inventoryManager;

        Chest neighbor = inventoryManager.getChest(coordToString(x + 1, y, z));
        if(neighbor != null){
            return neighbor.channel;
        }

        neighbor = inventoryManager.getChest(coordToString(x - 1, y, z));
        if(neighbor != null){
            return neighbor.channel;
        }

        neighbor = inventoryManager.getChest(coordToString(x, y, z + 1));
        if(neighbor != null){
            return neighbor.channel;
        }

        neighbor = inventoryManager.getChest(coordToString(x, y, z - 1));
        if(neighbor != null){
            return neighbor.channel;
        }

        neighbor = inventoryManager.getChest(coordToString(x, y + 1, z));
        if(neighbor != null){
            return neighbor.channel;
        }

        neighbor = inventoryManager.getChest(coordToString(x, y - 1, z));
        if(neighbor != null){
            return neighbor.channel;
        }

        return "";
    }

    public String getPos(){
        return coordToString(x, y, z);
    }
}
