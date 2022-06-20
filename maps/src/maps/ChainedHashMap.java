package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */

public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 1;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 10;

    private int size;
    private int capacity;
    private int chainCount;
    private double factor;
    AbstractIterableMap<K, V>[] chains;

    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD,
            DEFAULT_INITIAL_CHAIN_COUNT,
            DEFAULT_INITIAL_CHAIN_CAPACITY);
        this.factor = DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD;
        this.size = 0;
    }

    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.chains = createArrayOfChains(initialChainCount);
        for (int i = 0; i < initialChainCount; i++) {
            chains[i] = createChain(chainInitialCapacity);
        }
        this.chainCount = initialChainCount;
        this.factor = resizingLoadFactorThreshold;
        this.capacity = chainInitialCapacity;
    }

    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        if (key != null) {
            int hashCode = Math.abs(key.hashCode() % chains.length);
            if (chains[hashCode] == null) {
                return null;
            }
            return chains[hashCode].get(key);
        }
        return chains[0].get(key);
    }

    @Override
    public V put(K key, V value) {
        resize();
        if (!this.containsKey(key)) {
            size++;
        }
        int hashCode;
        if (key != null) {
            hashCode = Math.abs(key.hashCode() % chains.length);
            if (chains[hashCode] == null) {
                chains[hashCode] = createChain(capacity);
            }
            return chains[hashCode].put(key, value);
        }
        return chains[0].put(key, value);
    }

    @Override
    public V remove(Object key) {
        if (key != null) {
            int hashCode = Math.abs(key.hashCode() % chains.length);

            if (this.containsKey(key)) {
                V removed = chains[hashCode].remove(key);
                if (removed != null) {
                    size--;
                }
                return removed;
            } else {
                return null;
            }
        }
        size--;
        return chains[0].remove(key);
    }

    @Override
    public void clear() {
        this.chains = createArrayOfChains(chainCount);
        for (int i = 0; i < chainCount; i++) {
            chains[i] = createChain(capacity);
        }

        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int hashCode = 0;
        if (key != null) {
            hashCode = Math.abs(key.hashCode()) % chains.length;
        }
        if (chains[hashCode] == null) {
            return false;
        }
        return chains[hashCode].containsKey(key);

    }

    @Override
    public int size() {
        return this.size;
    }

    private void resize() {
        double load = (double) size / (double) chains.length;

        if (load >= factor) {

            int newLength = chains.length * 2;
            AbstractIterableMap<K, V>[] oldChains = chains;
            chains = createArrayOfChains(newLength);

            for (AbstractIterableMap<K, V> chain : oldChains) {
                if (chain != null) {
                    for (Map.Entry<K, V> entry : chain) {
                        int hashCode = 0;
                        if (entry.getKey() != null) {
                            hashCode = Math.abs(entry.getKey().hashCode()) % chains.length;
                        }
                        if (chains[hashCode] == null) {
                            chains[hashCode] = createChain(capacity);
                        }
                        chains[hashCode].put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new ChainedHashMapIterator<>(this.chains);
    }

    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private Iterator<Entry<K, V>> curr;
        private AbstractIterableMap<K, V>[] chains;
        private int index = 0;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            if (chains[index] != null) {
                this.curr = chains[index].iterator();
            } else {
                this.curr = null;
            }
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = false;
            if (index < chains.length) {
                if (curr == null || !curr.hasNext()) {
                    int temp = index;
                    temp++;
                    while (temp < chains.length && (chains[temp] == null || chains[temp].isEmpty())) {
                        temp++;
                    }
                    if (temp < chains.length) {
                        curr = chains[temp].iterator();
                        hasNext = true;
                    } else {
                        hasNext = false;
                    }
                    index = temp;
                } else {
                    hasNext = true;
                }
            }
            return hasNext;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return curr.next();
        }
    }
}
