package port_a_vault.port_a_vault.util;

import java.util.ArrayList;
import java.util.Iterator;

/*
PUBLIC METHODS:
Search: contains(String val)
Get: get(String val), size(), capacity(), loadFactor(), isEmpty()
Insert: put(String key, T val)
Delete: remove(String val), clear()
Other: print()

HELPERS:
Hashing: hash(string key), hashReduce(String key), rehash()
Other: update(), reset()
*/

public class UnorderedSet<T> implements Iterable<T>
{
    private static final double REHASH_THRESHOLD = 0.75;

    private ArrayList<ArrayList<T>> buckets;

    private int size;
    private int capacity;
    private double loadFactor;



    UnorderedSet() { reset(16); }
    UnorderedSet(int _capacity) { reset(_capacity); }

    public boolean contains(T val)
    {
        for(T item : buckets.get(this.hashReduce(val)))
            if (item.equals(val))
                return true;
        return false;
    }

    T get(T val)
    {
        int index = this.hashReduce(val);
        for(T item : buckets.get(index))
            if(item.equals(val))
                return item;
        return null;
    }

    public int size() { return size; }

    public int capacity() { return capacity; }

    public double loadFactor() { return loadFactor; }

    public boolean isEmpty() { return size == 0; }

    public boolean put(T val)
    {
        int index = this.hashReduce(val);
        for(T item : buckets.get(index))
            if(item.equals(val))
                return false;

        buckets.get(index).add(val);
        size++;
        update();
        return true;
    }

    T remove(T val)
    {
        int index = this.hashReduce(val);
        for(int i = 0; i < buckets.get(index).size(); i++)
            if(val.equals(buckets.get(index).get(i)))
            {
                loadFactor = --size / (double)capacity;
                return buckets.get(index).remove(i);
            }
        return null;
    }

    public void clear() { reset(16); }



    public void printStats() { System.out.println("HashSet has capacity " + capacity + ", size " + size + ", and LF " + loadFactor); }
    public void print()
    {
        int cnt = 0;
        for(ArrayList<T> list : buckets)
        {
            System.out.print("Bucket " + cnt++ + ": ");
            for(T item : list)
                System.out.print(item + "   ");
            System.out.println();
        }
        printStats();
    }

    public void printKeys()
    {
        int cnt = 0;
        for(ArrayList<T> list : buckets)
        {
            System.out.print("Bucket " + cnt++ + ": ");
            for(T item : list)
                System.out.print(item + "   ");
            System.out.println();
        }
        printStats();
    }



    private int hashReduce(T item) { return Math.abs(item.hashCode()) % capacity; }

    // Rehash if loadFactor >= 0.75
    // Expands and rebuilds the hashtable
    private void rehash()
    {
        ArrayList<T> temp = new ArrayList<>();
        for(ArrayList<T> list : buckets)
            temp.addAll(list);

        reset(capacity *= 2);
        size = 0;
        for(T item : temp)
            this.put(item);
        System.out.println("REHASHING SET TO " + capacity + " BUCKETS...");
    }



    // Updates loadFactor and rehashes if necessary
    private void update()
    {
        loadFactor = (double)size / capacity;
        if(loadFactor > REHASH_THRESHOLD)
            rehash();
    }

    // For annoying null-initialized buckets ArrayList
    private void reset(int cap)
    {
        buckets = new ArrayList<>();
        size = 0;
        loadFactor = 0;
        capacity = cap;
        for(int i = 0; i < cap; i++)
            buckets.add(new ArrayList<>());
    }


    public Iterator<T> iterator()
    {
        Iterator<T> it = new Iterator<T>()
        {
            private int index = 0, subindex = 0;

            @Override
            public boolean hasNext()
            {
                // Skip over empty buckets
                if(subindex < buckets.get(index).size())
                    return true;
                subindex = 0;
                index++;
                while(index < capacity)
                {
                    if(buckets.get(index).isEmpty())
                        index++;
                    else return true;
                }
                return false;
            }

            @Override
            public T next() { return buckets.get(index).get(subindex++); }
        };
        return it;
    }

}
