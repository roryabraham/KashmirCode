
public class SeparateChainedHashTable {

    private int size;
    private int tableSize;
    final static float MAX_LOAD_FACTOR = 0.75f;
    final static int MAX_TABLE_SIZE = Short.MAX_VALUE;
    private int rehashCount = 0;

    private Entry[] table;

    //Constructor
    public SeparateChainedHashTable(int table_size)
    {
        this.table = new Entry[table_size];
        this.tableSize = table_size;
        this.size = 0;
    }


    // Search method will return -1 if key not found
    public short get(String key)
    {
        int index = hash(key);
        for(Entry e = table[index]; e != null; e = e.next)
        {
            if(hashCodeOf(e.key) == hashCodeOf(key) && e.key.equals(key))
                return e.value;
        }
        return -1;
    }

    public void insert(String key, Short value)
    {
        Entry newEntry = new Entry(key, value);
        int index = hash(newEntry);
        if(table[index] == null)
            table[index] = newEntry;
        else
        {
            Entry e = table[index];
            while(e.next != null)
            {
                e = e.next;
            }
            e.next = newEntry;
        }
        size++;

        if(loadFactor() >= MAX_LOAD_FACTOR && tableSize < MAX_TABLE_SIZE)
            rehash();
    }

    public int getSize() { return size; }

    //returns true if there is an entry for this key
    public boolean containsKey(String key)
    {
        //Entry<> tab[] = table;
        int index = hash(key);
        for(Entry e = table[index]; e != null; e = e.next)
        {
            if(hashCodeOf(e.key) == hashCodeOf(key) && e.key.equals(key))
                return true;
        }
        return false;
    }

    public void rehash()
    {
        rehashCount++;
        int oldCapacity = tableSize;
        Entry[] oldMap = table;

        int newCapacity = nextPrime(oldCapacity*2);

        if (newCapacity - MAX_TABLE_SIZE > 0)
        {
            if (oldCapacity == MAX_TABLE_SIZE)
                // do not rehash
                return;

            tableSize = MAX_TABLE_SIZE;
        }

        table = new Entry[newCapacity];
        tableSize = newCapacity;

        for(int i = oldCapacity; i-- > 0;)
        {
            for(Entry e = oldMap[i]; e != null; e = e.next)
                insert(e.key,e.value);
        }
    }

    public int getRehashCount()
    {
        return rehashCount;
    }

    public float loadFactor()
    {
        return (float) this.size/this.tableSize;
    }

    public float averageListLength()
    {
        int average = 0;
        int[] lengths = new int[tableSize];
        for(int i = 0; i < tableSize; i++)
        {
            int listLength = 0;
            Entry e = table[i];
            if(e != null)
            {
                for(Entry entry = e; entry != null; entry = entry.next)
                    listLength++;
            }
            lengths[i] = listLength;
        }
        for(int i = 0; i < tableSize; i++)
            average += lengths[i];

        return (float)
                average/tableSize;
    }

    public int longestList()
    {
        int longest = 0;
        for(Entry e: table)
        {
            int length = 0;
            if(e != null)
            {
                for(Entry entry = e; entry != null; entry = entry.next)
                {
                    length++;
                    if(length >= longest)
                        longest = length;
                }
            }
        }
        return longest;
    }

    private int hash(Entry e)
    {
        int hashVal = e.hashCode();
        hashVal %= tableSize;
        if(hashVal < 0)
            hashVal += tableSize;
        return hashVal;
    }

    private int hash(String s)
    {
        int hashVal = hashCodeOf(s);
        hashVal %= tableSize;
        if(hashVal < 0)
            hashVal += tableSize;
        return hashVal;
    }

    private int hashCodeOf(String key)
    {
        int hashVal = 0;
        for(int i = 0; i < key.length(); i++)
            hashVal = 37 * hashVal + key.charAt(i);

        return hashVal;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < tableSize; i++)
            builder.append(table[i].toString());

        return builder.toString();
    }

    private class Entry
    {
        private final String key;
        private Short value;

        Entry next;

        public Entry(String key, Short value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() { return this.key; }
        public Short getValue() { return value; }

        public boolean equals(Entry e) {
            return (this.key.equals(e.key) && this.value.equals(e.value));
        }

        public int hashCode()
        {
            int hashVal = 0;
            for(int i = 0; i < key.length(); i++)
                hashVal = 37 * hashVal + key.charAt(i);

            return hashVal;
        }

        public String toString() { return key + "-->" + Short.toString(value); }
    }

    public static boolean isPrime(int n) {
        for(int i=2; i<n; i++) {
            if(n%i==0)return false;
        }
        return true;
    }

    public static int nextPrime(int n) {
        for(int i=n+1; true; i++) {
            if(isPrime(i)) return i;
        }
    }
}