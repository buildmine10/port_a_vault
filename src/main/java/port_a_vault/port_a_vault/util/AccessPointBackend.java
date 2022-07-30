package port_a_vault.port_a_vault.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.checkerframework.checker.units.qual.A;
import port_a_vault.port_a_vault.Port_a_vault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

//the trees only add chests when generate trees is run
//chests are automatically removed when chests are removed

//every access point has one of these

//generate trees, still needs to make the modded trees

//The screen manager gets an ArrayList of BigStacks
//When inserting pass in the item stack that is to be inserted
//When removing pass in the big stack that is being pulled from
//removeStack removes a full stack
//removeCount removes up to a full stack

//the Chest class has a function that searches for the channels of the surrounding positions.
//  This assigns the channel of the chest when the chest is placed

//running generateTrees will refresh the access point

public class AccessPointBackend {

    public void forTest(World world, BlockPos pos, PlayerEntity player, Hand hand){
        ArrayList<BigStack> stacks = toBigStacks(search("", ""));
        //System.out.println(stacks.get(0).name + ": " + stacks.get(0).getCount());

        for(int i = 0; i < stacks.size(); i++){
            if(stacks.get(i).name.compareTo("Air") != 0){
                //System.out.println(stacks.get(1).name + ": " + stacks.get(0).getCount());
                ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), removeStack(stacks.get(i)));
                break;
            }
        }

        //ItemScatterer.spawn(world, pos, (Inventory) be);
        //System.out.println(pos.toShortString());
        //ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), removeStack(stacks.get(0)));
        //insertStack(player.getStackInHand(hand));
    }

    public void forTest2(World world, BlockPos pos, PlayerEntity player, Hand hand){
        ArrayList<BigStack> stacks = toBigStacks(search("", ""));
        //System.out.println(stacks.get(0).name + ": " + stacks.get(0).getCount());


        //ItemScatterer.spawn(world, pos, (Inventory) be);
        //System.out.println(pos.toShortString());
        //ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), removeStack(stacks.get(0)));
        insertStack(player.getStackInHand(hand));
    }
    private boolean isUsingRedBlackTree = false;
    private String channel = "";//technically it should never be on this channel (the empty string channel)

    //searches are performed by finding a subtree
    //filters are performed by using the map
    //"" (empty string) is the filter for no filter
    HashMap<String, RedBlackTree2> redBlackTrees = new HashMap<>();
    HashMap<String, BTree> bTrees = new HashMap<>();


    public void generateTrees(){
        if(isUsingRedBlackTree){
            generateRedBlackTrees();
            bTrees = new HashMap<>();
        }else{
            generateBTrees();
            redBlackTrees = new HashMap<>();
        }
    }

    private void generateRedBlackTrees(){
        InventoryManager inventoryManager = Port_a_vault.inventoryManager;
        redBlackTrees.put("", new RedBlackTree2());

        HashSet<Chest> chests = inventoryManager.getChestsOnChannel(channel);//gets the chests on the same channel as the access point
        for(Chest chest : chests){
            for(LinkedVariable<ItemStack> stack : chest.inventory){
                redBlackTrees.get("").insert(stack);
            }
        }


    }

    private void generateBTrees(){
        InventoryManager inventoryManager = Port_a_vault.inventoryManager;
        bTrees.put("", new BTree());//this overwrites the existing value, or just makes a value (this make a new tree)

        HashSet<Chest> chests = inventoryManager.getChestsOnChannel(channel);//gets the chests on the same channel as the access point

        for(Chest chest : chests){
            for(LinkedVariable<ItemStack> stack : chest.inventory){
                bTrees.get("").insert(stack);
            }
        }

        //this is testing code (it can be safely removed)


    }


    //returns a list of every ItemStack that has items that are a named according to a valid search
    public ArrayList<LinkedVariable<ItemStack>> search(String name, String mod){
        if(isUsingRedBlackTree){
            return searchRedBlack(name, redBlackTrees.get(mod));
        }else{
            return searchBTree(name, bTrees.get(mod));
        }
    }

    private boolean searchTraversalPart2(RedBlackTree2.Node node, ArrayList<LinkedVariable<ItemStack>> out, String end){
        if(node.left != null){
            if(searchTraversalPart2(node.left, out, end)){
                return true;//if it quit early, it is done
            }
        }

        if(node.name.compareTo(end) >= 0){
            return true;
        }
        if(!node.data.isDeleted())
            out.add(node.data);

        if(node.right != null){
            if(searchTraversalPart2(node.right, out, end)){
                return true;//if it quit early, it is done
            }
        }

        return false;
    }

    private void searchTraversalPart1(RedBlackTree2.Node node, ArrayList<LinkedVariable<ItemStack>> out, String end, boolean fromLeftChild){
        if(node.name.compareTo(end) >= 0){
            return;
        }
        if(fromLeftChild){
            if(!node.data.isDeleted())
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
    private void searchTraversalPart1(RedBlackTree2.Node node, ArrayList<LinkedVariable<ItemStack>> out, String end){
        searchTraversalPart1(node, out, end, true);

    }

    private ArrayList<LinkedVariable<ItemStack>> searchRedBlack(String name, RedBlackTree2 tree){

        var minTarget = tree.root;
        if(tree.root == null){
            return new ArrayList<>();
        }

        if(name.compareTo("") != 0){
            RedBlackTree2.Node possibleTarget = null;
            while(true){
            /*
            if(minTarget.data.isDeleted()){
                tree.remove(minTarget);
                return searchRedBlack(name, tree);
            }
            */

                int comparison = name.compareTo(minTarget.name);
                if(comparison < 0){
                    if(minTarget.left != null){
                        //System.out.println("left min");
                        possibleTarget = minTarget;//if it goes left, it could then go right and never find a match. In this case, minTarget is possibleTarget
                        minTarget = minTarget.left;
                    }else{//if there is nothing less than the minTarget, then it is the first result
                        break;
                    }
                }else if(comparison == 0){//if equal we need to check if the left node isn't also equal
                /*
                if(minTarget.left.data.isDeleted()){
                    tree.remove(minTarget.left);
                    return searchRedBlack(name, tree);
                }
                */
                    if(minTarget.left != null && name.compareTo(minTarget.left.name) == 0){
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

            if(minTarget == null){
                minTarget = possibleTarget;
            }

            String afterName = name.substring(0, name.length() - 1);
            afterName = afterName.concat(String.valueOf((char)(name.charAt(name.length() - 1) + 1)));
            //System.out.println("AfterName: " + afterName);

            ArrayList<LinkedVariable<ItemStack>> out = new ArrayList<>();
            if(minTarget != null){
                searchTraversalPart1(minTarget, out, afterName);
            }
            //for(var stack : out){
            //    if(!stack.isDeleted()){
            //        System.out.println(stack.getData().getName().getString());
            //    }
            //}
            return out;
        }else{
            ArrayList<LinkedVariable<ItemStack>> out = new ArrayList<>();
            tree.inOrder(node->{
                out.add(node.data);
            });
            return out;
        }
    }

    private ArrayList<LinkedVariable<ItemStack>> searchBTree(String name, BTree tree){
        if (name.compareTo("") == 0) {
            ArrayList<LinkedVariable<ItemStack>> out = new ArrayList<>();
            BTree.LeafIterator iter = tree.getBegin();
            if(iter != null){
                while(iter.get() != null){
                    //System.out.println(iter.get().getData().getName().getString());
                    out.add(iter.get());
                    iter.next();
                }
            }

            return out;
        }

        BTree.LeafIterator iter = tree.findFirstValid(name);
        String afterName = name.substring(0, name.length() - 1);
        afterName = afterName.concat(String.valueOf((char)(name.charAt(name.length() - 1) + 1)));

        ArrayList<LinkedVariable<ItemStack>> out = new ArrayList<>();
        if(iter != null){
            while(iter.get() != null && iter.get().getData().getName().getString().compareTo(afterName) < 0){
                //System.out.println(iter.get().getData().getName().getString());
                out.add(iter.get());
                iter.next();
            }
        }



        return out;
    }

    //generates a list of big stacks
    //this is what the access point displays
    public ArrayList<BigStack> toBigStacks(ArrayList<LinkedVariable<ItemStack>> stacks){
        ArrayList<BigStack> out = new ArrayList<>();
        ArrayList<LinkedVariable<ItemStack>> buffer = new ArrayList<>();

        String currentName = null;

        for(int i = 0; i < stacks.size(); i++){
            if(!stacks.get(i).isDeleted()){
                String name = stacks.get(i).getData().getName().getString();
                if(currentName == null){
                    currentName = name;
                }

                //if new item type is encountered dump the buffer into a BigStack
                if(currentName.compareTo(name) != 0){
                    if(!buffer.isEmpty())
                        out.add(new BigStack(buffer));
                    buffer.clear();
                    currentName = name;
                }

                buffer.add(stacks.get(i));
            }
        }

        //any remaining stacks in the buffer are of the same type, so add them
        if(!buffer.isEmpty()){
            out.add(new BigStack(buffer));
            buffer.clear();
        }

        return out;
    }

    //insert the stack into the network
    public void insertStack(ItemStack stack){
        boolean isSuccess = false;

        var searchResults = search(stack.getName().getString(), "");
        if(!searchResults.isEmpty()){
            BigStack bigStack = new BigStack(searchResults);//searches for the item, make a big stack of that item

            if(bigStack.name.compareTo(stack.getName().getString()) == 0){//if the big stack is the same as the
                if(bigStack.insertStack(stack) == 0){//if it added the entire stack
                    isSuccess = true;
                }
            }
        }

        if(!isSuccess){

            //System.out.println("Searching for AIR");
            ArrayList<BigStack> possibleAirStacks = toBigStacks(search("Air", ""));
            for(BigStack _bigStack : possibleAirStacks){
                if(_bigStack.getItem() == Items.AIR){//if the big stack is the same as the
                    //System.out.println("hi");
                    _bigStack.insertStack(stack);
                    if(stack.getCount() == 0){
                        break;
                    }
                }
            }
        }

    }

    //removes a full stack from the big stack that is passed in
    public ItemStack removeStack(BigStack stack){
        ItemStack out = stack.removeStack();
        //airStacks.addAll(stack.getEmpty());
        return out;
    }

    //Removes the specified amount the from big stack that is passed in
    public ItemStack removeCount(BigStack stack, int count){
        return stack.removeCount(1);
    }

    public void setChannel(String channel){
        this.channel = channel;
    }

    public void switchTrees(){
        isUsingRedBlackTree = !isUsingRedBlackTree;
        generateTrees();
    }

    public boolean isUsingRedBlackTree(){
        return isUsingRedBlackTree;
    }
}
