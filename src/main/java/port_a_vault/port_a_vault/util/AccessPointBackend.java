package port_a_vault.port_a_vault.util;

import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class AccessPointBackend {

    private boolean isUsingRedBlackTree = true;
    private String channel = "";//technically it should never be on this channel (the empty string channel)

    //searches are performed by finding a subtree
    //filters are performed by using the map
    RedBlackTree<ItemStack> redBlackTree;//sorted by name
    HashMap<String, RedBlackTree<ItemStack>> modFilteredRedBlackTrees;

    


}
