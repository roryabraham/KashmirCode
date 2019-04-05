public class Ideal_HashTable
{
    private Entry[] table;
    private int size;
    private int tableSize;
    private int rehashCount;

    public Ideal_HashTable(int tableSize)
    {
        this.table = new Entry[tableSize];
        this.tableSize = tableSize;
    }

    public String get(short key)
    {
        return table[key].value;
    }

    public void insert(short key, String value)
    {
        try
        {
            Entry newEntry = new Entry(key, value);
            table[key] = newEntry;
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            rehash();
            insert(key, value);
        }

        size++;
    }

    public void rehash()
    {
        rehashCount++;
        int oldCapacity = tableSize;
        int newCapacity = nextPrime(tableSize*2);

        Entry[] oldMap = table;
        tableSize = newCapacity;
        table = new Entry[newCapacity];

        System.arraycopy(oldMap,0,table,0,oldCapacity);
    }

    public int getRehashCount() {
        return rehashCount;
    }

    public boolean containsKey(short key)
        {
            try
            {
                return (table[key] != null);
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                return false;
            }
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

    private class Entry
    {
        private short key;
        private String value;

        public Entry(short key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String getValue() { return value; }
        public short getKey() { return key; }

        @Override
        public String toString()
        {
            return key + " --> " + value;
        }
    }
}
