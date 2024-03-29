package port_a_vault.port_a_vault.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


//this red black tree had edge case bugs, that did not show up in initial testing
//It was replaced by a more specifically defined red black tree that omitted the ability to delete
//  Deletion was not needed, and it was causing the bug, so it was removed.
//  We did not have time to find the bug
public class RedBlackTree<T> {

    public class Node {
        LinkedVariable<T> data;

        Node left;
        Node right;
        Node parent;
        boolean isRed = true;

        Node(LinkedVariable<T> value){
            data = value;
        }

        boolean isLeftChild(){
            if(parent.left == null){
                return parent.right != this;
            }else{
                return parent.left == this;
            }
        }
    }

    Node root;
    Set<Node> markedForRemoval = new HashSet<Node>();
    boolean areMarkedBeingRemoved = false;

    Comparator<T> comparator;

    RedBlackTree(Comparator<T> _comparator){
        comparator = _comparator;
    }

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

    public void insert(LinkedVariable<T> value){
        if(root == null){
            root = new Node(value);
            root.isRed = false;
            return;
        }

        Node target = root;
        Node lastTarget = root;

        while(target != null){
            if(target.data.isDeleted()){//if the target's data has been deleted it should be removed from the tree
                remove(target);
                insert(value);
                return;
            }

            lastTarget = target;

            if(comparator.compare(value.getData(), target.data.getData()) < 0){//if value is less than target
                target = target.left;
            }else{
                target = target.right;
            }
        }

        Node temp = new Node(value);
        temp.parent = lastTarget;

        if(comparator.compare(value.getData(), lastTarget.data.getData()) < 0){//if value is less than target
            lastTarget.left = temp;
        }else{
            lastTarget.right = temp;
        }

        fixInsert(temp);
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

    public void remove(Node node){
        //if(node.parent == null && root != node){//The node is invalidly defined and cannot be removed
        //    return;
        //}

        boolean hasLeftChild = node.left != null;
        boolean hasRightChild = node.right != null;

        if(!hasLeftChild && !hasRightChild){
            if(node == root){
                root = null;
            }else{
                if(node.isLeftChild()){//this might not be the correct check to use
                    node.parent.left = null;
                }else{
                    node.parent.right = null;
                }
            }

            //node.data = null;

            if(!node.isRed)
                fixRemove(node);
        }else if(hasLeftChild != hasRightChild)
        {//it has one child
            Node child;
            if(hasLeftChild){
                child = node.left;
            }else{
                child = node.right;
            }

            child.parent = node.parent;

            if(node != root){
                if(node.isLeftChild()){//this might not be the correct check to use
                    node.parent.left = child;
                }else{
                    node.parent.right = child;
                }
            }

            child.isRed = node.isRed;

            //node.data = null;
            //fixDelete(child);
        }else{//it has two children
            if(node.left.right == null){
                Node child = node.left;

                //var temp = node.data;
                //node.data = child.data;
                //child.data = temp;

                //remove(child);

                //swaps node and child
                child.parent = node.parent;
                if(node.isLeftChild()){
                    child.parent.left = child;
                }else{
                    child.parent.right = child;
                }
                node.parent = child;


                child.right = node.right;
                node.left = child.left;
                child.left = node;
                node.right = null;

                if(node.left != null)
                    node.left.parent = node;

                child.right.parent = child;


                //node.parent.left = null;

                boolean nodeIsRed = node.isRed;
                node.isRed = child.isRed;
                child.isRed = nodeIsRed;

                if(node == root){
                    root = child;
                }
                //done swapping

                remove(node);
                return;
                //fixDelete(node.right);
            }else{
                Node child = node.left;
                while(child.right != null){
                    child = child.right;
                }

                //var temp = node.data;
                //node.data = child.data;
                //child.data = temp;
                //remove(child);

                //swaps node and child
                Node childOldParent = child.parent;
                child.parent = node.parent;
                node.parent = childOldParent;

                Node nodeOldLeft = node.left;
                node.left = child.left;
                child.left = nodeOldLeft;


                child.right = node.right;
                node.right = null;

                node.parent.right = null;
                child.left.parent = child;

                if(node.left != null)
                    node.left.parent = node;

                boolean nodeIsRed = node.isRed;
                node.isRed = child.isRed;
                child.isRed = nodeIsRed;

                if(node == root){
                    root = child;
                }

                remove(node);
                return;
            }
        }


        //removedMarked();

    }

    // Balance the tree after deletion of a node
    /*
    private void fixRemove(Node node) {
        if(node == null){//there is nothing to fix
            return;
        }
        if(node.data.isDeleted() && !areMarkedBeingRemoved){
            markedForRemoval.add(node);
        }
        if(node == root){
            root.isRed = false;
            return;
        }

        Node sibling;
        boolean isLeftChild = node.isLeftChild();
        if(isLeftChild){
            sibling = node.parent.right;
        }else{
            sibling = node.parent.left;
        }

        if(sibling == null || !sibling.isRed && (sibling.left == null || !sibling.left.isRed) && (sibling.right == null || !sibling.right.isRed)){
            //if(isLeftChild){
            //    node.parent.left = null;
            //}else{
            //    node.parent.right = null;
            //}
            //node.parent = null;
            if(sibling != null){
                sibling.isRed = true;
            }

            if(node.parent.isRed){
                node.parent.isRed = false;
            }else{
                fixRemove(node.parent);
                return;
            }

        }else if(sibling != null && sibling.isRed){
            {
                boolean temp = sibling.isRed;
                sibling.isRed = sibling.parent.isRed;
                sibling.parent.isRed = temp;
            }

            if(isLeftChild){
                leftRotate(sibling.parent);
            }else{
                rightRotate(sibling.parent);
            }

            fixRemove(node);
        }else if(isLeftChild){
            if(sibling.right == null || !sibling.right.isRed){
                if(sibling.left != null && sibling.left.isRed){
                    sibling.isRed = true;
                    sibling.left.isRed = false;

                    rightRotate(sibling);

                    {
                        boolean temp = sibling.parent.isRed;
                        sibling.parent.isRed = node.parent.isRed;
                        node.parent.isRed = temp;
                    }
                    leftRotate(node.parent);
                    sibling.isRed = false;
                }else{
                    return;
                }
            }else{
                Node farSiblingChild = sibling.right;
                {
                    boolean temp = sibling.isRed;
                    sibling.isRed = node.parent.isRed;
                    node.parent.isRed = temp;
                }
                leftRotate(node.parent);
                farSiblingChild.isRed = false;
            }
        }else{//if right child
            if(sibling.left == null || !sibling.left.isRed){
                if(sibling.right != null && sibling.right.isRed){
                    sibling.isRed = true;
                    sibling.right.isRed = false;

                    leftRotate(sibling);

                    {
                        boolean temp = sibling.parent.isRed;
                        sibling.parent.isRed = node.parent.isRed;
                        node.parent.isRed = temp;
                    }
                    rightRotate(node.parent);
                    sibling.isRed = false;
                }else{
                    return;
                }
            }else{
                Node farSiblingChild = sibling.left;
                {
                    boolean temp = sibling.isRed;
                    sibling.isRed = node.parent.isRed;
                    node.parent.isRed = temp;
                }
                rightRotate(node.parent);
                farSiblingChild.isRed = false;
            }
        }
    }
    */


    private void fixRemove(Node node){
        Node sibling;
        while (node != root && !node.isRed) {
            if (node == node.parent.left) {
                sibling = node.parent.right;
                if (sibling != null && sibling.isRed) {
                    sibling.isRed = false;
                    node.parent.isRed = true;
                    leftRotate(node.parent);
                    sibling = node.parent.right;
                }

                if(sibling == null){
                    node = node.parent;
                }else if ((sibling.left == null || !sibling.left.isRed) && (sibling.right == null || !sibling.right.isRed)) {
                    sibling.isRed = true;
                    node = node.parent;
                } else {
                    if (sibling.right == null || !sibling.right.isRed) {
                        sibling.left.isRed = false;
                        sibling.isRed = true;
                        rightRotate(sibling);
                        sibling = node.parent.right;
                    }

                    sibling.isRed = node.parent.isRed;
                    node.parent.isRed = false;
                    sibling.right.isRed = false;
                    leftRotate(node.parent);
                    node = root;
                }
            } else {
                sibling = node.parent.left;
                if (sibling != null && sibling.isRed) {
                    sibling.isRed = false;
                    node.parent.isRed = true;
                    rightRotate(node.parent);
                    sibling = node.parent.left;
                }

                if(sibling == null){
                    node = node.parent;
                }else if ((sibling.left == null || !sibling.left.isRed) && (sibling.right == null || !sibling.right.isRed)) {
                    sibling.isRed = true;
                    node = node.parent;
                } else {
                    if (sibling.left == null || !sibling.left.isRed) {
                        sibling.right.isRed = false;
                        sibling.isRed = true;
                        leftRotate(sibling);
                        sibling = node.parent.left;
                    }

                    sibling.isRed = node.parent.isRed;
                    node.parent.isRed = false;
                    sibling.left.isRed = false;
                    rightRotate(node.parent);
                    node = root;
                }
            }
        }
        node.isRed = false;
    }

    public void printPreOrder(){
        preOrder((Node node)->{
            System.out.print(node.data.getData());
            System.out.print(" ");
            if(node.isRed){
                System.out.println("red");
            }else{
                System.out.println("black");
            }

        });
    }


    public boolean validate(){
        if(root != null){
            int result = validate(root, 1);
            removedMarked();
            boolean isBST = bstCheck(root);
            return result != 0 && !root.isRed && isBST;
        }else{
            return true;
        }
    }

    private int validate(Node node, int blackCount){
        int leftBlackDepth, rightBlackDepth;
        if(node.data.isDeleted()){
            markedForRemoval.add(node);
        }

        if(node.left != null){
            if(node.left.isRed){
                leftBlackDepth = validate(node.left, blackCount);
            }else{
                leftBlackDepth = validate(node.left, blackCount + 1);
            }
        }else{
            leftBlackDepth = 1;
        }
        if(node.right != null) {
            if(node.right.isRed){
                rightBlackDepth = validate(node.right, blackCount);
            }else{
                rightBlackDepth = validate(node.right, blackCount + 1);
            }
        }else{
            rightBlackDepth = 1;
        }

        if(leftBlackDepth != rightBlackDepth || leftBlackDepth == 0){
            return 0;
        }else{
            if(node.isRed){
                if(node.left != null && node.left.isRed || node.right != null && node.right.isRed){
                    return 0;
                }
            }
            return leftBlackDepth;
        }
    }

    private boolean bstCheck(Node node){
        boolean isLeftValid = true;
        if(node.left != null){
            if(comparator.compare(node.left.data.getData(), node.data.getData()) <= 0){
                isLeftValid = bstCheck(node.left);
            }else{
                //System.out.println(node.right.data.getData());
                isLeftValid = false;
            }
        }

        boolean isRightValid = true;
        if(node.right != null){
            if(comparator.compare(node.right.data.getData(), node.data.getData()) < 0){
                //System.out.println(node.right.data.getData());
                isRightValid = false;
            }else{
                isRightValid = bstCheck(node.right);
            }
        }

        return isLeftValid && isRightValid;
    }


    private void removedMarked(){
        if(!areMarkedBeingRemoved){
            areMarkedBeingRemoved = true;
            int i = 0;
            for(var entry : markedForRemoval){
                remove(entry);//I am moving data around the nodes, so you need to update marked for removal
                //if(++i == markedForRemoval.size() - 1){
                //    break;
                //}
            }
            markedForRemoval.clear();
            areMarkedBeingRemoved = false;
        }
    }

    public void removeDeleted(){
        removeDeleted(root);
        removedMarked();
    }

    private void removeDeleted(Node node){
        if(node.data.isDeleted()){
            markedForRemoval.add(node);
        }
        if(node.left != null){
            removeDeleted(node.left);
        }
        if(node.right != null){
            removeDeleted(node.right);
        }
    }

    public void preOrder(Consumer<Node> func){
        if(root != null)
            preOrder(func, root);
        removedMarked();
    }

    public void inOrder(Consumer<Node> func){
        if(root != null)
            inOrder(func, root);
        removedMarked();
    }

    public void postOrder(Consumer<Node> func){
        if(root != null)
            postOrder(func, root);
        removedMarked();
    }

    public void preOrder(Consumer<Node> func, Node node){
        if(node.data.isDeleted()){
            markedForRemoval.add(node);
        }else{
            if(node != null)
                func.accept(node);
        }
        if(node.left != null){
            preOrder(func, node.left);
        }
        if(node.right != null){
            preOrder(func, node.right);
        }
    }

    public void inOrder(Consumer<Node> func, Node node){
        if(node.left != null){
            inOrder(func, node.left);
        }
        if(node.data.isDeleted()){
            markedForRemoval.add(node);
        }else{
            if(node != null)
                func.accept(node);
        }
        if(node.right != null){
            inOrder(func, node.right);
        }
    }

    public void postOrder(Consumer<Node> func, Node node){
        if(node.left != null){
            postOrder(func, node.left);
        }
        if(node.right != null){
            postOrder(func, node.right);
        }
        if(node.data.isDeleted()){
            markedForRemoval.add(node);
        }else{
            if(node != null)
                func.accept(node);
        }
    }

    public Node search(T key){
        if(root == null){
            return null;
        }

        Node target = root;

        while(target != null){
            if(target.data.isDeleted()){//if the target's data has been deleted it should be removed from the tree
                remove(target);
                return search(key);
            }


            int comparison = comparator.compare(key, target.data.getData());
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
