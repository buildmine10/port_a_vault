package port_a_vault.port_a_vault.util;

import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.function.Consumer;

public class RedBlackTree2 {
    public class Node {
        LinkedVariable<ItemStack> data;
        String name = null;

        Node left;
        Node right;
        Node parent;
        boolean isRed = true;

        Node(LinkedVariable<ItemStack> value){
            data = value;
            if(!data.isDeleted()){
                name = data.getData().getName().getString();
            }
        }

        boolean isLeftChild(){
            if(parent.left == null){
                return parent.right != this;
            }else{
                return parent.left == this;
            }
        }

        public int compareTo(Node other){
            return this.name.compareTo(other.name);
        }
    }

    Node root;

    private void leftRotate(Node node){
        if(node.right != null){
            node.right.parent = node.parent;
            if(node.parent != null){
                if(node.isLeftChild()){
                    node.parent.left = node.right;
                }else{
                    node.parent.right = node.right;
                }
            }

            node.parent = node.right;

            if(node == root){
                //System.out.println("set new root");
                root = node.right;
                //System.out.println(root.data.getData());
            }

            Node grandChild = node.right.left;
            node.right.left = node;
            node.right = grandChild;
            if(grandChild != null){
                grandChild.parent = node;
            }
        }
    }

    private void rightRotate(Node node){
        if(node.left != null){
            node.left.parent = node.parent;
            if(node.parent != null){
                if(node.isLeftChild()){
                    node.parent.left = node.left;
                }else{
                    node.parent.right = node.left;
                }
            }

            node.parent = node.left;

            if(node == root){
                root = node.left;
            }

            Node grandChild = node.left.right;
            node.left.right = node;
            node.left = grandChild;
            if(grandChild != null){
                grandChild.parent = node;
            }
        }
    }

    public void insert(LinkedVariable<ItemStack> value){
        if(root == null){
            root = new Node(value);
            root.isRed = false;
            return;
        }
        //if the value is deleted there is nothing to insert
        if(value.isDeleted()){
            return;
        }

        Node target = root;
        Node lastTarget = root;
        Node toInsert = new Node(value);

        while(target != null){
            lastTarget = target;

            if(toInsert.compareTo(target) < 0){//if value is less than target
                target = target.left;
            }else{
                target = target.right;
            }
        }

        //Node temp = new Node(value);
        toInsert.parent = lastTarget;

        if(toInsert.compareTo(lastTarget) < 0){//if value is less than target
            lastTarget.left = toInsert;
        }else{
            lastTarget.right = toInsert;
        }

        fixInsert(toInsert);
    }

    private void fixInsert(Node node) {
        if(node == root){
            node.isRed = false;
        } else if(node.parent.isRed){
            Node uncle;
            if(node.parent.isLeftChild()){
                uncle = node.parent.parent.right;
            }else{
                uncle = node.parent.parent.left;
            }

            if(uncle == null || !uncle.isRed){//if the uncle is black
                if(node.parent.isLeftChild()){
                    if(node.isLeftChild()){//left left case
                        node.parent.parent.isRed = true;
                        node.parent.isRed = false;
                        rightRotate(node.parent.parent);
                    }else{//left right case
                        leftRotate(node.parent);

                        node.parent.isRed = true;
                        node.isRed = false;
                        rightRotate(node.parent);
                    }
                }else{
                    if(node.isLeftChild()){//right left case
                        rightRotate(node.parent);

                        node.parent.isRed = true;
                        node.isRed = false;
                        leftRotate(node.parent);
                    }else{//right right case
                        node.parent.parent.isRed = true;
                        node.parent.isRed = false;
                        leftRotate(node.parent.parent);
                    }
                }
            }else{//if the uncle is red
                node.parent.isRed = false;
                uncle.isRed = false;
                node.parent.parent.isRed = true;
                fixInsert(node.parent.parent);
            }
        }
    }

    public void preOrder(Consumer<Node> func){
        if(root != null)
            preOrder(func, root);
    }

    public void inOrder(Consumer<Node> func){
        if(root != null)
            inOrder(func, root);
    }

    public void postOrder(Consumer<Node> func){
        if(root != null)
            postOrder(func, root);
    }

    public void preOrder(Consumer<Node> func, Node node){
        if(node != null){
            if(!node.data.isDeleted())
                func.accept(node);

            if(node.left != null){
                preOrder(func, node.left);
            }
            if(node.right != null){
                preOrder(func, node.right);
            }
        }
    }

    public void inOrder(Consumer<Node> func, Node node){
        if(node != null){
            if(node.left != null){
                preOrder(func, node.left);
            }

            if(!node.data.isDeleted())
                func.accept(node);

            if(node.right != null){
                preOrder(func, node.right);
            }
        }
    }

    public void postOrder(Consumer<Node> func, Node node){
        if(node != null){
            if(node.left != null){
                preOrder(func, node.left);
            }
            if(node.right != null){
                preOrder(func, node.right);
            }

            if(!node.data.isDeleted())
                func.accept(node);
        }
    }

    public Node search(String name){
        if(root == null){
            return null;
        }

        Node target = root;

        while(target != null){
            /*
            if(target.data.isDeleted()){//if the target's data has been deleted it should be removed from the tree
                remove(target);
                return search(key);
            }
            */


            int comparison = name.compareTo(target.name);//comparator.compare(key, target.data.getData());
            if(comparison < 0){//if value is less than target
                target = target.left;
            }else if(comparison == 0){
                return target;
            }else{
                target = target.right;
            }
        }

        return null;
    }
}
