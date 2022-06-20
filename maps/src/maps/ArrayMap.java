package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    private int size = 0;

    SimpleEntry<K, V>[] entries;

    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */

    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    private void resize() {
        SimpleEntry<K, V>[] newEntries = createArrayOfEntries(entries.length * 2);
        System.arraycopy(entries, 0, newEntries, 0, entries.length);
        entries = newEntries;
    }

    @Override
    public V get(Object key) {
        if (this.containsKey(key)) {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(key, entries[i].getKey())) {
                    return entries[i].getValue();
                }
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (this.containsKey(key)) {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(key, entries[i].getKey())) {
                    V previous = entries[i].getValue();
                    entries[i].setValue(value);
                    resize();
                    return previous;
                }
            }
        } else {
            entries[size] = new SimpleEntry<>(key, value);
            size++;
            if (entries[entries.length - 1] != null) {
                resize();
            }
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        if (this.containsKey(key)) {
            for (int i = 0; i < size; i++) {
                if (Objects.equals(key, entries[i].getKey())) {
                    V prev = entries[i].getValue();
                    entries[i] = entries[size - 1];
                    entries[size - 1] = null;
                    size--;
                    return prev;
                }
            }
        }
        return null;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            entries[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < size; i++) {
            if ((entries[i].getKey() == null && key == null) || Objects.equals(key, entries[i].getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new ArrayMapIterator<>(this.entries);
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int index = 0;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
        }

        @Override
        public boolean hasNext() {
            if (index > entries.length - 1) {
                return false;
            } else {
                return entries[index] != null;
            }
        }

        @Override
        public Map.Entry<K, V> next() {
            if (this.hasNext()) {
                SimpleEntry<K, V> prev = entries[index];
                index++;
                return prev;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
