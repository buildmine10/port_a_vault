package port_a_vault.port_a_vault.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.PriorityQueue;

//When making a BigStack, the item type is that of the first item in the provided array
//It will stop adding items, when it reaches a different item type
public class BigStack {

    class Stack implements Comparable<Stack> {
        LinkedVariable<ItemStack> stack;

        public ItemStack getData(){
            return stack.getData();
        }

        public ItemStack setData(ItemStack value){
            return stack.setData(value);
        }

        public void delete(){
            stack.delete();
        }

        public boolean isDeleted(){
            return stack.isDeleted();
        }


        @Override
        public int compareTo(Stack other) {
            if(stack.isDeleted()){
                return 1;
            }
            if(other.isDeleted()){
                return -1;
            }

            return stack.getData().getCount() - other.getData().getCount();
        }

        public Stack(LinkedVariable<ItemStack> stack){
            this.stack = stack;
        }
    }

    //Allows traversal of the stack in order of most empty.
    //deleted stack are in the back
    class Data {
        PriorityQueue<Stack> sorted;
        ArrayList<Stack> used = new ArrayList<>();

        public LinkedVariable<ItemStack> pop(){
            if(sorted.isEmpty()){
                return null;
            }
            used.add(sorted.poll());
            return used.get(used.size() - 1).stack;
        }

        public LinkedVariable<ItemStack> peek(){
            if(sorted.isEmpty()){
                return null;
            }
            return sorted.peek().stack;
        }

        public void add(LinkedVariable<ItemStack> stack){
            sorted.add(new Stack(stack));
        }

        public void reset(){
            sorted.addAll(used);
            used.clear();
        }

        Data(ArrayList<LinkedVariable<ItemStack>> itemStacks){
            ArrayList<Stack> stacks = new ArrayList<>();
            itemStacks.forEach(stack->{
                if(!stack.isDeleted()){
                    if(stack.getData().getName().getString().compareTo(name) == 0){
                        //System.out.println(stack.getData().getCount());
                        stacks.add(new Stack(stack));
                    }

                }

            });
            sorted = new PriorityQueue<>(stacks);
        }
    }
    Data itemStacks = null;
    String name = null;

    public BigStack(ArrayList<LinkedVariable<ItemStack>> stacks){
        name = stacks.get(0).getData().getName().getString();
        itemStacks = new Data(stacks);
    }

    public int getCount(){
        int out = 0;
        while(!itemStacks.sorted.isEmpty()){
            LinkedVariable<ItemStack> stack = itemStacks.pop();
            if(!stack.isDeleted()){
                //System.out.println(stack.getData().getCount());
                out += stack.getData().getCount();
            }
        }
        itemStacks.reset();
        return out;
    }

    //returns the number of items not transferred
    public int insertStack(ItemStack stack){
        boolean isAddingToAir = false;
        if(stack.getName().getString().compareTo(name) != 0) {
            if(name.compareTo("Air") == 0){//if this big stack contains air
                isAddingToAir = true;
            }else{
                return stack.getCount();
            }
        }
        int maxCount = stack.getMaxCount();

        if(isAddingToAir){
            maxCount = stack.getMaxCount();
        }


        //goes through items stacks emptiest first
        while(!itemStacks.sorted.isEmpty() && !stack.isEmpty()){
            LinkedVariable<ItemStack> stack2 = itemStacks.pop();

            //if the stacks are full, then none of the remaining stacks have space
            if(stack2.getData().getCount() == maxCount){
                break;
            }

            if(stack.getCount() + stack2.getData().getCount() > maxCount) {
                //if combining the stack would be too large
                stack.decrement(maxCount - stack2.getData().getCount());
                if(isAddingToAir){
                    stack2.setData(new ItemStack(stack.getItem(), 0));
                }
                stack2.getData().setCount(maxCount);
                if(isAddingToAir){
                    itemStacks.used.remove(itemStacks.used.size() - 1);
                }
            }else{
                //if we are able to combine the stacks
                if(isAddingToAir){
                    stack2.setData(new ItemStack(stack.getItem(), 0));
                }
                stack2.getData().increment(stack.getCount());
                if(isAddingToAir){
                    itemStacks.used.remove(itemStacks.used.size() - 1);
                }
                stack.setCount(0);
            }
        }

        itemStacks.reset();

        return stack.getCount();
    }

    public ItemStack removeStack(){
        ItemStack out = ItemStack.EMPTY;
        int maxCount = 0;
        while(!itemStacks.sorted.isEmpty()){
            LinkedVariable<ItemStack> top = itemStacks.pop();
            if(!top.isDeleted()){
                maxCount = top.getData().getMaxCount();
                out = new ItemStack(top.getData().getItem(), 0);
                break;
            }
        }
        itemStacks.reset();
        //System.out.println(maxCount);

        while(!itemStacks.sorted.isEmpty() && out.getCount() < maxCount){
            LinkedVariable<ItemStack> top = itemStacks.pop();
            if(!top.isDeleted()){
                //System.out.println(out.getCount() + " " + top.getData().getCount());
                if(top.getData().getCount() + out.getCount() > maxCount){
                    top.getData().decrement(maxCount - out.getCount());
                    out.setCount(maxCount);
                }else{
                    out.increment(top.getData().getCount());
                    top.getData().setCount(0);
                }
                //System.out.println(out.getCount() + " " + top.getData().getCount());
                //if after removing items the stack we took from is empty we should remove it
                //but this stuff is remade every time the access point is opened
                // so it won't have an effect unless very little space is left

            }
        }

        itemStacks.reset();

        return out;
    }

    public ItemStack removeCount(int count){
        ItemStack out = ItemStack.EMPTY;
        int maxCount = 0;
        while(!itemStacks.sorted.isEmpty()){
            LinkedVariable<ItemStack> top = itemStacks.pop();
            if(!top.isDeleted()){
                maxCount = top.getData().getMaxCount();
                out = new ItemStack(top.getData().getItem(), 0);
                break;
            }
        }
        itemStacks.reset();

        if(count > maxCount){
            count = maxCount;
        }

        while(!itemStacks.sorted.isEmpty() && out.getCount() < count){
            LinkedVariable<ItemStack> top = itemStacks.pop();
            if(!top.isDeleted()){
                //System.out.println(out.getCount() + " " + top.getData().getCount());
                if(top.getData().getCount() + out.getCount() > count){
                    top.getData().decrement(count - out.getCount());
                    out.setCount(count);
                }else{
                    out.increment(top.getData().getCount());
                    top.getData().setCount(0);
                }
                //System.out.println(out.getCount() + " " + top.getData().getCount());
                //if after removing items the stack we took from is empty we should remove it
                //but this stuff is remade every time the access point is opened
                // so it won't have an effect unless very little space is left

            }
        }

        itemStacks.reset();

        return out;

    }

    public Item getItem(){
        while(!itemStacks.sorted.isEmpty()){
            var stack = itemStacks.pop();
            if(!stack.isDeleted()){
                itemStacks.reset();
                return stack.getData().getItem();
            }
        }
        itemStacks.reset();
        return null;
    }


}
