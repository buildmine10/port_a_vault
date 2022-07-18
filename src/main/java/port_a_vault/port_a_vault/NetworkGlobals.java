package port_a_vault.port_a_vault;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import port_a_vault.port_a_vault.block.LinkedVariable;

import java.util.HashMap;

public class NetworkGlobals {
    public static HashMap<String, DefaultedList<LinkedVariable<ItemStack>>> chests = new HashMap<>();

    public static void addChest(String pos){
        //System.out.print("addedChest: ");
        //System.out.println(pos);
        DefaultedList<LinkedVariable<ItemStack>> items = DefaultedList.ofSize(3 * 9, new LinkedVariable<>(ItemStack.EMPTY));
        for(int i = 0; i < 3 * 9; i++){
            items.set(i, new LinkedVariable<>(new ItemStack((Item)null)));
        }
        chests.putIfAbsent(pos, items);
    }

    public static void removeChest(String pos){
        //System.out.print("removedChest: ");
        //System.out.println(pos);

        chests.get(pos).forEach((LinkedVariable::delete));
        chests.remove(pos);
    }

    public static DefaultedList<LinkedVariable<ItemStack>> getChest(String pos){
        //System.out.print("getChest: ");
        //System.out.println(pos);
        return chests.get(pos);
    }
}
