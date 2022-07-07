package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;
/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Xinpeng WU
 */
public class MyHashMap<K, V> implements hashmap.Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.75;
    private final double loadFactor;
    private Collection<Node>[] buckets;
    private int size;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() { this (DEFAULT_INITIAL_SIZE, DEFAULT_MAX_LOAD_FACTOR); }

    public MyHashMap(int initialSize) { this (initialSize, DEFAULT_MAX_LOAD_FACTOR); }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        if (initialSize < 1 || maxLoad <= 0.0)
            throw new IllegalArgumentException ();
        buckets = createTable(initialSize);
        size = 0; loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() { return new LinkedList<>(); }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        buckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++){
            buckets[i] = createBucket();
        }
        return buckets;
    }

    private int hash(K key){
        return (0x7fffffff & key.hashCode ()) % buckets.length;
    }
    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear(){
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        size = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key){
        if (size <= 0) return false;
        Collection<Node> bucket = buckets[hash(key)];
        if (!bucket.isEmpty()){
            for (Node n : bucket){
                if (n.key.equals(key)) { return true; }
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key){
        if (size <= 0) return null;
        Collection<Node> bucket = buckets[hash(key)];
        if (!bucket.isEmpty()){
            for (Node n : bucket){
                if (n.key.equals(key)) { return n.value; }
            }
        }
        return null;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size(){
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value){
        Collection<Node> bucket = buckets[hash(key)];
        if(containsKey(key)){
            for (Node n : bucket){
                if (n.key.equals(key)) { n.value = value;}
            }
        }else{
            bucket.add(new Node(key, value));
            size += 1;
            if (size > loadFactor * buckets.length){
                resize(buckets.length * 2);
            }
        }
    }
    private void resize(int s){
        MyHashMap<K, V> temp = new MyHashMap<>(s);
        for (int i = 0; i < buckets.length; i ++){
            for (Node n : buckets[i]){
                temp.put(n.key, n.value);
            }
        }
        this.buckets = temp.buckets;
    }

    /** Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet(){
        Set<K> keySet = new HashSet<>();
        for (Collection<Node> bucket : buckets){
            for (Node n : bucket){
                keySet.add(n.key);
            }
        }
        return keySet;
    }

    @Override
    public Iterator<K> iterator(){
        return keySet().iterator();
    }
    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key){
        if (size <= 0) return null;
        if(containsKey(key)){
            for (Node n : buckets[hash(key)]){
                if (n.key.equals(key)) {
                    buckets[hash(key)].remove(n);
                    size -= 1;
                    return n.value;
                }
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value){
        if (size <= 0) return null;
        Collection<Node> bucket = buckets[hash(key)];
        if(containsKey(key)){
            for (Node n : bucket){
                if (n.key.equals(key) && n.value.equals(value)) {
                    bucket.remove(n);
                    size -= 1;
                    return n.value;
                }
            }
        }
        return null;
    }
}
