package port_a_vault.port_a_vault.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

//it's actually a B+ tree
//it is sorted by item name

//deletion is not fully functional
//front, back, and root might become valid
//check to see is next and previous are still valid
//explicit removal of deleted elements is slow
public class BTree{
    //removing a node from the tree is simple
    //  just remove it from the leaf
    //  the keys in the InnerBlock's still make for a valid tree


    public abstract class Block {
        InnerBlock parent = null;//I can do this (InnerBlock extends Block, which has an InnerBlock in it)?
        public boolean isInnerBlock = false;
        public abstract void split();

        public abstract void insert(LinkedVariable<ItemStack> value);

    }
    public class InnerBlock extends Block {
        //Inner blocks are made by the split function of leaf blocks and inner blocks
        //this is the only reason they should ever exist
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Block> children = new ArrayList<>();


        InnerBlock(){
            isInnerBlock = true;
        }
        public void split(){
            //this runs when keys.size > (maxChildren - 1)
            //the equation for the index to lift is keys.size / 2


            int removedIndex = keys.size() / 2;
            ArrayList<String> rightKeys = new ArrayList<>(keys.subList(removedIndex + 1, keys.size()));//copies the values
            String removedKey = keys.get(removedIndex);//stored the key to be elevated
            keys.subList(removedIndex, keys.size()).clear();//removed the keys that are no longer in the current block

            InnerBlock rightBlock = new InnerBlock();
            rightBlock.keys = rightKeys;

            rightBlock.children = new ArrayList<>(children.subList(removedIndex + 1, children.size()));//moves correct children to the right block
            children.subList(removedIndex + 1, children.size()).clear();//removes those children from the left block

            for(Block child : rightBlock.children){
                child.parent = rightBlock;
            }

            //at this point the left and right blocks are correct
            //the left block is this
            //the right block is rightBlock

            //at this point the key that needs to be moved up a level has been stored
            //it is removedKey


            //at this point the rightBlock need to be added to the parent

            if(parent == null){
                //the parent block needs to be made
                //the left block will be the first child, the right block will be the second child (but this will happen using the default code)

                //it is possible that this block in the root block
                //if so the new parent will be the new root

                parent = new InnerBlock();
                parent.children.add(this);

                if(root == this){
                    root = parent;
                }
            }

            //the removed key gets added at the same index as the left child's child-index in parent
            //if left child is the 0th child of parent, then the removedKey is the 0th key in parent
            //  because the 0th key in parent is more than left child and less than right child
            //likewise, right child gets added after left child in parent's child-list

            parent.keys.add(parent.children.indexOf(this), removedKey);

            parent.children.add(parent.children.indexOf(this) + 1, rightBlock);
            rightBlock.parent = parent;


            //at this point the tree is in the correct state
            //except maybe the parent now has too many keys
            //  so we check, and do a recursive call if that is the case

            if(parent.keys.size() > maxChildren - 1){
                parent.split();
            }


        }

        public Block getCorrespondingChild(String name){
            for(int i = 0; i < keys.size(); i++){
                //if the name is less than a key, then "i" is the child that the name corresponds to
                if(name.compareTo(keys.get(i)) < 0){//if the name is less than the key
                    //System.out.println(i);
                    return children.get(i);
                }
            }

            //System.out.println(keys.size());
            return children.get(keys.size());//this will always be the last child
        }

        public void merge(){
            //the root cannot merge, and it is not required to
            if(root == this){
                return;
            }

            int childIndex = parent.children.indexOf(this);

            if(childIndex == 0){
                //merge right
                mergeRight();
            }else{
                //merge left
                mergeLeft();
            }

            //if after merging, the parent does not have enough keys, merge the parent
            if(parent.keys.size() < (int)Math.ceil(maxChildren / 2) - 1){
                parent.merge();
            }
        }

        private void mergeRight(){
            int childIndex = parent.children.indexOf(this);
            String key = parent.keys.remove(childIndex);
            InnerBlock rightSibling = (InnerBlock) parent.children.remove(childIndex + 1);
            keys.add(key);
            keys.addAll(rightSibling.keys);
            children.addAll(rightSibling.children);
            for(var child : children){
                child.parent = this;
            }
        }

        private void mergeLeft(){
            int childIndex = parent.children.indexOf(this);
            String key = parent.keys.remove(childIndex - 1);
            InnerBlock leftSibling = (InnerBlock) parent.children.remove(childIndex);
            leftSibling.keys.add(key);
            leftSibling.keys.addAll(keys);
            leftSibling.children.addAll(children);
            for(var child : leftSibling.children){
                child.parent = leftSibling;
            }
        }

        public void insert(LinkedVariable<ItemStack> value){
            //if the value to be inserted is deleted, don't insert it
            if(value.isDeleted()){
                return;
            }

            getCorrespondingChild(value.getData().getName().getString()).insert(value);
        }
    }

    private class Node {
        LinkedVariable<ItemStack> data = null;
        String name = null;
        Node(LinkedVariable<ItemStack> data, String name){
            this.data = data;
            this.name = name;
        }
    }

    public class LeafBlock extends Block {

        ArrayList<Node> nodes = new ArrayList<>();
        LeafBlock next = null;
        LeafBlock previous = null;

        public void split(){
            //this runs when nodes.size > leafBlockMaxNodeCount
            //cases for when this runs
            //if nodes.size == 2, then node-1's key is lifted
            //if nodes.size == 3, then node-1's key is lifted
            //if nodes.size == 4, then node-2's key is lifted
            //the function for the index is: nodes.size / 2
            //the split happens just before the lifted key
            //  so the lifted key is put in the right block


            //A check needs to be performed to ensure that the node to lift is still valid
            //if not the node is deleted, and thus removed
            //  then the split no longer needs to occur
            //  because the number of nodes is now below the threshold

            int indexToLift = nodes.size() / 2;
            Node nodeToLift = nodes.get(indexToLift);
            if(nodeToLift.data.isDeleted()){
                remove(nodeToLift);
                return;
            }

            String keyToLift = nodeToLift.name;

            LeafBlock rightBlock = new LeafBlock();
            rightBlock.nodes = new ArrayList<>(nodes.subList(indexToLift, nodes.size()));//copies the right half of the nodes
            nodes.subList(indexToLift, nodes.size()).clear();//removed the right half of the nodes


            //if the left block (this) == back, then the right block would be the new back
            if(this == back){
                back = rightBlock;
            }

            rightBlock.next = this.next;
            if(this.next != null)
                this.next.previous = rightBlock;

            this.next = rightBlock;
            rightBlock.previous = this;

            //at this point the left and right blocks have the correct nodes in them
            //the parent needs to be made

            if(parent == null){
                //the parent block needs to be made
                //the left block will be the first child, the right block will be the second child (but this will happen using the default code)

                //if no root exists, then this is the first InnerBlock to exist, and it is the root

                parent = new InnerBlock();
                parent.children.add(this);

                if(root == null){
                    root = parent;
                }
            }

            //the keyToLift gets added at the same index as the left child's child-index in parent
            //if left child is the 0th child of parent, then the keyToLift is the 0th key in parent
            //  because the 0th key in parent is more than left child and less than or equal to the right child
            //likewise, right child gets added after left child in parent's child-list


            parent.keys.add(parent.children.indexOf(this), keyToLift);

            parent.children.add(parent.children.indexOf(this) + 1, rightBlock);
            rightBlock.parent = parent;


            //at this point the tree is in the correct state
            //except maybe the parent now has too many keys
            //  so we check, and do a recursive call if that is the case

            if(parent.keys.size() > maxChildren - 1){
                parent.split();
            }

        }

        public void insert(LinkedVariable<ItemStack> value){
            //if we are trying to insert a deleted value, don't
            if(value.isDeleted()){
                return;
            }

            int index = 0;
            while(index < nodes.size()){
                Node node = nodes.get(index);

                //if one of the nodes needs to be deleted
                //  then delete it and try to insert again
                if(node.data.isDeleted()){
                    remove(node);
                    insert(value);
                    return;
                }


                //the value we are inserting gets placed just before the first node that it is less than or equal to
                //  all the nodes before this are less than it, all the nodes after are greater or equal
                //by exiting the loop here, index equals this value
                //if it is greater than all the others, then index will equal the spot just after the last node
                if(value.getData().getName().getString().compareTo(node.name) <= 0){
                    break;
                }

                index++;
            }

            nodes.add(index, new Node(value, value.getData().getName().getString()));

            //at this point the new value has been put in the right spot
            //  but the block might have too many nodes now
            //  so we check, and deal with it if needed

            if(nodes.size() > maxChildren - 1){
                split();
            }
        }

        public void remove(Node value){
            //System.out.println("Attempted Removal: " + value.name);
            int minNodes = (int)Math.ceil(maxChildren / 2) - 1;

            if(nodes.size() > minNodes){
                //remove the node and update the keys if needed
                this.nodes.remove(value);
                int index = this.parent.children.indexOf(this) - 1;
                if(index <= 0){
                    updateKeySomewhere();
                }else{
                    this.parent.keys.set(index, this.nodes.get(0).name);
                }
            }else{
                //one of four options
                //  take from right sibling
                //  take from left sibling
                //  merge with right sibling
                //  merge with left sibling
                //then update the keys as needed
                this.nodes.remove(value);

                if(!takeLeft()){
                    if(!takeRight()){

                        if(!mergeLeft()){
                            if(!mergeRight()){
                                System.out.println("This should be impossible. Both merges from a B+ Tree deletion failed.");
                            }
                        }

                    }
                }

                //the rebinding of keys happens in the take and merge functions
            }
        }

        public boolean takeLeft(){
            int index = this.parent.children.indexOf(this);
            if(index <= 0){
                return false;
            }

            LeafBlock leftSibling = (LeafBlock) this.parent.children.get(index - 1);
            if(leftSibling.nodes.size() > minKeys){
                //moves the last node in the left sibling to this
                this.nodes.add(0, leftSibling.nodes.get(leftSibling.nodes.size() - 1));
                leftSibling.nodes.remove(leftSibling.nodes.size() - 1);

                //the first node in this just changed, so you must update it's key in the parent
                this.parent.keys.set(index - 1, this.nodes.get(0).name);

                return true;
            }

            return false;
        }

        public boolean takeRight(){
            int index = this.parent.children.indexOf(this);
            if(index >= this.parent.children.size() - 1){
                return false;
            }

            LeafBlock rightSibling = (LeafBlock) this.parent.children.get(index + 1);
            if(rightSibling.nodes.size() > minKeys){
                //moves the first node in the right sibling to this
                this.nodes.add(rightSibling.nodes.get(0));
                rightSibling.nodes.remove(0);

                //the first node in the right sibling just changed, so you must update it's key in the parent
                this.parent.keys.set(index, rightSibling.nodes.get(0).name);
                if(this.nodes.size() == 1){
                    //then we need to update this node's key in the parent
                    //  because the first node has changed
                    if(index > 0){//but the first child has no key in the immediate parent
                        this.parent.keys.set(index - 1, this.nodes.get(0).name);
                    }else{//it does however have a key somewhere
                        updateKeySomewhere();
                    }

                }

                return true;
            }

            return false;
        }

        private void updateKeySomewhere(){
            String key = this.nodes.get(0).name;

            Block previousBlock = this;
            InnerBlock block = this.parent;

            while(block != null && block.children.indexOf(previousBlock) == 0){
                previousBlock = block;
                block = block.parent;
            }

            if(block != null)
                block.keys.set(block.children.indexOf(previousBlock) - 1, key);

            //throw new RuntimeException("Implement this. One of the InnerBlocks has a key that needs to change.");
        }
        public boolean mergeLeft(){
            int index = this.parent.children.indexOf(this) - 1;
            if(index >= 0){
                merge((LeafBlock) this.parent.children.get(index), this);
                return true;
            }
            return false;
        }

        public boolean mergeRight(){
            int index = this.parent.children.indexOf(this) + 1;
            if(index < this.parent.children.size()){
                merge(this, (LeafBlock) this.parent.children.get(index));
                return true;
            }
            return false;
        }

        private void merge(LeafBlock left, LeafBlock right){
            //the right block is removed from the tree
            //  it is merged into the left block
            //the right block's key in the parent is removed
            //the right block is removed from the tree
            InnerBlock parent = left.parent;

            boolean leftHadNoKeys = left.nodes.size() == 0;


            parent.keys.remove(parent.children.indexOf(left));//this removes the key of the right child
            parent.children.remove(right);//removes the right child from the parent

            left.nodes.addAll(right.nodes);//gives the left block all the nodes of the right block

            //rebinds the linking between leaf blocks
            left.next = right.next;
            if(left.next != null)
                left.next.previous = left;

            if(back == right){
                back = left;
            }

            if(leftHadNoKeys){//this means that left now has a key
                int index = parent.children.indexOf(left);
                if(index <= 0){//if it is the first child, then the key is somewhere
                    updateKeySomewhere();
                }else{//else the key is in the parent
                    parent.keys.set(index - 1, left.nodes.get(0).name);
                }
            }

            if(parent.keys.size() < minKeys){
                parent.merge();
            }
        }
        public LeafIterator firstValid(String name){
            //-1 = not found
            //first valid = the index of the first node that is greater than or equal to name
            for(int i = 0; i < nodes.size(); i++){
               Node node = nodes.get(i);

                //if a node is found to be deleted, remove it, then try again
                if(node.data.isDeleted()){
                    nodes.remove(node);
                    return firstValid(name);
                }

                //System.out.println(node.name + "\t" + name);
                if(node.name.compareTo(name) >= 0){
                    return new LeafIterator(this, i);
                }
            }

            //the first valid result, could be in either this block, or it could be the first node of the next block
            if(next != null){
                return next.firstValid(name);
            }
            return null;
        }
    }

    public class LeafIterator {

        private LeafIterator(LeafBlock block, int index){
            this.block = block;
            this.index = index;
        }
        LeafBlock block = null;
        int index = 0;

        public LinkedVariable<ItemStack> get(){
            if(block == null){
                return null;
            }
            return block.nodes.get(index).data;
        }

        public void next(){
            if(block != null){
                index++;
                if(index >= block.nodes.size()){
                    //if it gets here we need to go to the next block
                    index = 0;
                    block = block.next;
                }
                if(get() != null && get().isDeleted()){
                    toRemove.add(get());
                    next();
                }
            }
        }
    }
    private int maxChildren = 5;
    private int minKeys = (int)Math.ceil(maxChildren / 2) - 1;

    private ArrayList<LinkedVariable<ItemStack>> toRemove = new ArrayList<>();//removal using this list is very slow

    public InnerBlock root = null;
    private LeafBlock front = new LeafBlock();
    private LeafBlock back = front;

    public LeafIterator getBegin(){
        return new LeafIterator(front, 0);
    }


    public LeafIterator findFirstValid(String name){
        //the first valid result is the first result that is greater than or equal to the query
        if(root == null){
            //if the root is null, then no splits have occurred
            return front.firstValid(name);
        }else{
            //if it gets here then we need to search the tree

            Block target = root;

            while(target.isInnerBlock){
                //System.out.println(target.);
                //target = ((InnerBlock)target).getCorrespondingChild(name);
                boolean broke = false;
                InnerBlock t = ((InnerBlock)target);
                for(int i = 0; i < t.keys.size(); i++){
                    //if the name is less than a key, then "i" is the child that the name corresponds to
                    if(name.compareTo(t.keys.get(i)) <= 0){//if the name is less than the key
                        //System.out.println(i);
                        target = t.children.get(i);
                        broke = true;
                        break;
                    }
                }

                //System.out.println(keys.size());
                if(!broke)
                    target = t.children.get(t.keys.size());//this will always be the last child

            }

            //once it is here, target is a leaf block
            //we already have the code for finding the first valid result in a leaf block
            //  so I copied it over
            return ((LeafBlock)target).firstValid(name);
        }
    }

    public void insert(LinkedVariable<ItemStack> stack){
        if(root == null){
            //System.out.println("Insert to front");
            //it only gets here if no splits have occurred
            front.insert(stack);
        }else{
            //System.out.println("Insert at root");
            root.insert(stack);
        }
    }

    public void remove(LinkedVariable<ItemStack> stack){
        LeafIterator location = find(stack);
        for(int i = 0; i < location.block.nodes.size(); i++){
            if(location.block.nodes.get(i).data == stack){
                location.block.remove(location.block.nodes.get(i));
                break;
            }
        }
    }

    private LeafIterator find(LinkedVariable<ItemStack> stack){
        if(stack.isDeleted()){
            LeafIterator iter = getBegin();

            while(iter.get() != stack){//goes through until it finds the target
                //System.out.println(iter.get().getData().getName().getString());
                iter.next();
            }
            if(iter.get() == null)
                return null;
            return iter;
        }else{
            String name = stack.getData().getName().getString();
            LeafIterator iter = findFirstValid(name);//find the first item with the same name
            String afterName = name.substring(0, name.length() - 1);
            afterName = afterName.concat(String.valueOf((char)(name.charAt(name.length() - 1) + 1)));

            //System.out.println("Target: " + name);
            while(iter.get() != stack){//goes through until it finds the target
                //System.out.println(iter.get().getData().getName().getString());
                if(afterName.compareTo(iter.get().getData().getName().getString()) <= 0){
                    return null;
                }
                iter.next();
            }
            return iter;
        }
    }
}

