package port_a_vault.port_a_vault.util;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableTextContent;
import port_a_vault.port_a_vault.Port_a_vault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;


public class AccessPointBackend {

    private boolean isUsingRedBlackTree = true;
    private String channel = "";//technically it should never be on this channel (the empty string channel)

    //searches are performed by finding a subtree
    //filters are performed by using the map
    //"" (empty string) is the filter for no filter
    HashMap<String, RedBlackTree<ItemStack>> redBlackTrees = new HashMap<>();


    private void makeRedBlackTree(String name){
        redBlackTrees.putIfAbsent(name, new RedBlackTree<>((a, b)->{
            //System.out.print("compare: ");
            //System.out.print(a.getName().getString() + ", ");
            //System.out.print(b.getName().getString() + ": ");
            //System.out.println(a.getName().getString().compareTo(b.getName().getString()) < 0);
            return a.getName().getString().compareTo(b.getName().getString());
        }));
    }
    public void generateTrees(){
        if(isUsingRedBlackTree){
            generateRedBlackTrees();
            //clear b tree
        }else{
            generateBTrees();
            //clear red black tree
        }
    }

    private void generateRedBlackTrees(){
        InventoryManager inventoryManager = Port_a_vault.inventoryManager;
        makeRedBlackTree("");
        redBlackTrees.get("").root = null;
        HashSet<Chest> chests = inventoryManager.getChestsOnChannel(channel);
        for(Chest chest : chests){
            for(LinkedVariable<ItemStack> stack : chest.inventory){
                redBlackTrees.get("").insert(stack);
            }
        }

        var hi = search("Bl", "");
        int i = 0;
        for(LinkedVariable<ItemStack> h : hi){
            System.out.println(i++ + " " + h.getData().getName().getString());
        }
    }

    private void generateBTrees(){

    }


    public ArrayList<LinkedVariable<ItemStack>> search(String name, String mod){
        if(isUsingRedBlackTree){
            return searchRedBlack(name, redBlackTrees.get(mod));
        }else{
            return searchBTree(name);
        }
    }

    private boolean searchTraversalPart2(RedBlackTree<ItemStack>.Node node, ArrayList<LinkedVariable<ItemStack>> out, String end){
        if(node.left != null){
            if(searchTraversalPart2(node.left, out, end)){
                return true;//if it quit early, it is done
            }
        }

        if(node.data.getData().getName().getString().compareTo(end) >= 0){
            return true;
        }
        out.add(node.data);

        if(node.right != null){
            if(searchTraversalPart2(node.right, out, end)){
                return true;//if it quit early, it is done
            }
        }

        return false;
    }

    private void searchTraversalPart1(RedBlackTree<ItemStack>.Node node, ArrayList<LinkedVariable<ItemStack>> out, String end, boolean fromLeftChild){
        if(node.data.getData().getName().getString().compareTo(end) >= 0){
            return;
        }
        if(fromLeftChild){
            out.add(node.data);
        }


        if(node.right != null && fromLeftChild){
            if(searchTraversalPart2(node.right, out, end)){//if it quit early, then it is done
                return;
            }
        }
        if(node.parent != null){
            searchTraversalPart1(node.parent, out, end, node.isLeftChild());
        }
    }
    private void searchTraversalPart1(RedBlackTree<ItemStack>.Node node, ArrayList<LinkedVariable<ItemStack>> out, String end){
        searchTraversalPart1(node, out, end, true);
    }

    private ArrayList<LinkedVariable<ItemStack>> searchRedBlack(String name, RedBlackTree<ItemStack> tree){
        var minTarget = tree.root;

        while(true){
            int comparison = name.compareTo(minTarget.data.getData().getName().getString());
            if(comparison < 0){
                if(minTarget.left != null){
                    //System.out.println("left min");
                    minTarget = minTarget.left;
                }else{//if there is nothing less than the minTarget, then it is the first result
                    break;
                }
            }else if(comparison == 0){//if equal we need to check if the left node isn't also equal
                if(minTarget.left != null && name.compareTo(minTarget.left.data.getData().getName().getString()) == 0){
                    //System.out.println("left min");
                    minTarget = minTarget.left;
                }else{
                    break;//we are at the first result
                }

            }else{
                if(minTarget.right != null){
                    //System.out.println("right min");
                    minTarget = minTarget.right;
                }else{
                    minTarget = null;//if it is going right and there is nothing left, then there are no valid results
                    break;
                }
            }
        }

        String afterName = name.substring(0, name.length() - 1);
        afterName = afterName.concat(String.valueOf((char)(name.charAt(name.length() - 1) + 1)));
        System.out.println("AfterName: " + afterName);

        ArrayList<LinkedVariable<ItemStack>> out = new ArrayList<>();
        searchTraversalPart1(minTarget, out, afterName);
        return out;
    }

    private ArrayList<LinkedVariable<ItemStack>> searchBTree(String name){
        return new ArrayList<>();
    }
}
