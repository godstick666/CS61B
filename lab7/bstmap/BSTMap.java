package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    private Node root;
    private int size = 0;
    private class Node{
        final K label;
        V value;
        Node left;
        Node right;
        Node(K l, V v){
            label = l;
            value = v;
        }
    }
    private Node search(Node T, K k){
        if (T == null || k.compareTo(T.label)==0)
            return T;
        else if (k.compareTo(T.label) < 0)
            return search(T.left, k);
        else return search(T.right, k);
    }
    private Node insert(Node T, K k, V v)
    {
        if (T == null)
            return new Node(k, v);
        if (k.compareTo(T.label) < 0)
            T.left = insert(T.left, k, v);
        else if(k.compareTo(T.label) > 0)
            T.right = insert(T.right, k, v);
        else
            T.value = v;
        return T;
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        root = null;
    }
    @Override
    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        if(search(root, key)==null)
            return false;
        return true;
    }
    @Override
    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        Node N = search(root, key);
        if(N == null)
            return null;
        return N.value;
    }
    @Override
    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return size;
    }
    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value){
        root = insert(root, key, value);
        size = size + 1;
    }
    private void printInOrder(Node T){
        if(T == null){
            return;
        }
//        System.out.print(T.label);
//        printInOrder(T.left);
//        printInOrder(T.right); wrong
        printInOrder(T.left);
        System.out.println(T.label + " -> " + T.value);
        printInOrder(T.right);
    }
    public void printInOrder(){
        printInOrder(root);
    }
    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        addKeys(root, set);
        return set;
    }
    private void addKeys(Node node, Set<K> set) {
        if (node == null) {
            return;
        }
        set.add(node.label);
        addKeys(node.left, set);
        addKeys(node.right, set);
    }
    @Override
    public V remove(K key) {
        //node.value == key, node.left ?= null node.right ?= null
        //if 0 node.parent.left/right == null;
        //if 1 node.parent.left/right == node.left/right;
        //if 2 find leftChild until leftChild.right == null,
        //          remove(leftChild), leftChild.left == node.left, leftChild.right == node.right,
        //          node.parent.left/right == leftChild;
        Node N = search(root, key);
        if (N != null){
            root = remove(root, key);
            size -= 1;
            return N.value;
        }
        return null;
    }

    private Node remove(Node N, K key){
        if (N == null){
            return null;
        }
        int cmp = key.compareTo(N.label);
        if (cmp < 0){
            N.left = remove(N.left, key);
        }else if (cmp > 0){
            N.right = remove(N.right, key);
        }else {
            if (N.left == null){
                return N.right;
            }
            if (N.right == null){
                return N.left;
            }
            Node newRoot = findNewRoot(N.left);
            newRoot.right = N.right;
            newRoot.left = remove(N.left, newRoot.label);
            N = newRoot;
        }
        return N;
    }
    private Node findNewRoot(Node N){
        if (N.right == null){
            return N;
        }
        return findNewRoot(N.right);
    }

    @Override
    public V remove(K key, V value) {
        Node N = search(root, key);
        if (N != null){
            if (N.value == value){
                return remove(key);
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
