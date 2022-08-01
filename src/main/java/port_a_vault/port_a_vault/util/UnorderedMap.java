package port_a_vault.port_a_vault.util;

import java.util.ArrayList;

/*
PUBLIC METHODS:
Search: containsKey(String key), containsValue(T value), containsPair(String key, T value)
Get: get(String key), size(), capacity(), loadFactor(), isEmpty()
Insert: put(String key, T value)
Delete: remove(String key), remove(String key, T value), clear()
Other: print()

HELPERS:
Hashing: hash(string key), hashReduce(String key), rehash()
Other: update(), reset()

NESTED CLASSES:
Pair: holds generics K and T
*/

public class UnorderedMap<T>
{
    private static final int POWER = 31;
    private static final int MOD = 1000000009;
    private static final double REHASH_THRESHOLD = 0.75;

    private ArrayList<ArrayList<Pair<String, T>>> buckets;
    private UnorderedSet<String> keys;
    private UnorderedSet<T> values;
    private int size;
    private int capacity;
    private double loadFactor;



    UnorderedMap() {
        keys = new UnorderedSet<>();
        values = new UnorderedSet<>();
        reset(16);
    }
    UnorderedMap(int _capacity) {
        keys = new UnorderedSet<>();
        values = new UnorderedSet<>();
        reset(_capacity);
    }

    // Search functions
    public boolean containsKey(String key) { return !buckets.get(this.hashReduce(key)).isEmpty(); }

    public boolean containsValue(T value)
    {
        for(ArrayList<Pair<String, T>> list : buckets)
            for(Pair<String, T> pair : list)
                if (pair.second == value)
                    return true;
        return false;
    }

    public boolean containsPair(String key, T value)
    {
        int index = this.hashReduce(key);
        for(Pair<String, T> pair : buckets.get(index))
            if(key.equals(pair.first) && value == pair.second)
                return true;
        return false;
    }



    // Getter functions
    T get(String key)
    {
        int index = this.hashReduce(key);
        for(Pair<String, T> pair : buckets.get(index))
            if(key.equals(pair.first))
                return pair.second;
        return null;
    }

    T getOrDefault(String key, T def) { return containsKey(key) ? get(key) : def; }

    UnorderedSet<String> getKeys() { return keys; }

    UnorderedSet<T> getValues() { return values; }

    public int size() { return size; }

    public int capacity() { return capacity; }

    public double loadFactor() { return loadFactor; }

    public boolean isEmpty() { return size == 0; }



    // Insertion function
    public T put(String key, T value)
    {
        int index = this.hashReduce(key);
        for(Pair<String, T> pair : buckets.get(index))
            if(key.equals(pair.first))
            {
                T temp = pair.second;
                pair.second = value;
                return temp;
            }

        buckets.get(index).add(new Pair<>(key, value));
        keys.put(key);
        values.put(value);
        size++;
        update();
        return null;
    }

    public T putIfAbsent(String key, T value)
    {
        int index = this.hashReduce(key);
        for(Pair<String, T> pair : buckets.get(index))
            if(key.equals(pair.first))
                return pair.second;

        buckets.get(index).add(new Pair<>(key, value));
        keys.put(key);
        values.put(value);
        size++;
        update();
        return null;
    }



    // Delete functions
    T remove(String key)
    {
        int index = this.hashReduce(key);
        for(int i = 0; i < buckets.get(index).size(); i++)
            if(key.equals(buckets.get(index).get(i).first))
            {
                loadFactor = --size / (double)capacity;
                keys.remove(key);
                values.remove(buckets.get(index).get(i).second);
                return buckets.get(index).remove(i).second;
            }
        return null;
    }

    boolean remove(String key, T value)
    {
        int index = this.hashReduce(key);
        for(int i = 0; i < buckets.get(index).size(); i++)
            if(key.equals(buckets.get(index).get(i).first) && value == buckets.get(index).get(i).second)
            {
                loadFactor = --size / (double)capacity;
                keys.remove(key);
                values.remove(value);
                buckets.get(index).remove(i);
                return true;
            }
        return false;
    }

    public void clear() { reset(16); }


    public void printStats() { System.out.println("HashMap has capacity " + capacity + ", size " + size + ", and LF " + loadFactor); }
    public void print()
    {
        int cnt = 0;
        for(ArrayList<Pair<String, T>> list : buckets)
        {
            System.out.print("Bucket " + cnt++ + ": ");
            for(Pair<String, T> pair : list)
                System.out.print(pair + "   ");
            System.out.println();
        }
        printStats();
    }

    public void printKeys()
    {
        int cnt = 0;
        System.out.print("Bucket " + cnt++ + ": ");
        for(String s : keys)
            System.out.print(s + "   ");
        System.out.println();
    }

    public void printValues()
    {
        int cnt = 0;
        System.out.print("Bucket " + cnt++ + ": ");
        for(T item : values)
            System.out.print(item + "   ");
        System.out.println();
    }



    // Generates String hashes
    private static int hash(String str)
    {
        long hashCode = 0;
        long p = 1;
        for(int i = 0; i < str.length(); i++)
        {
            hashCode = (hashCode + (str.charAt(i) % 26) * p) % MOD;
            p = (p * POWER) % MOD;
        }
        return (int)hashCode;
    }

    private int hashReduce(String str) { return hash(str) % capacity; }

    // Expands and rebuilds the hashtable
    private void rehash()
    {
        ArrayList<Pair<String, T>> temp = new ArrayList<>();
        for(ArrayList<Pair<String, T>> list : buckets)
            temp.addAll(list);

        reset(capacity *= 2);
        size = 0;
        for(Pair<String, T> pair : temp)
            this.put(pair.first, pair.second);
        System.out.println("REHASHING MAP TO " + capacity + " BUCKETS...");
    }

    // Updates loadFactor and rehashes if loadFactor >= 0.75
    private void update()
    {
        loadFactor = (double)size / capacity;
        if(loadFactor > REHASH_THRESHOLD)
            rehash();

    }

    // For annoying null-initialized data structures
    private void reset(int cap)
    {
        buckets = new ArrayList<>();
        size = 0;
        loadFactor = 0;
        capacity = cap;
        for(int i = 0; i < cap; i++)
            buckets.add(new ArrayList<>());
    }



    private static class Pair<F, S>
    {
        public F first;
        public S second;

        Pair(F _first, S _second)
        {
            first = _first;
            second = _second;
        }

        public String toString() { return first.toString() + ',' + second.toString(); }
    }

}