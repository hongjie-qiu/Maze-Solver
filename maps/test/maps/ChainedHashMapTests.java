package maps;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ChainedHashMapTests extends BaseMapTests {
    @Override
    protected <K, V> Map<K, V> createMap() {
        return new ChainedHashMap<>();
    }

    protected <K, V> Map<K, V> createMap(double resizingLoadFactorThreshold,
                                         int initialChainCount,
                                         int chainInitialCapacity) {
        return new ChainedHashMap<>(resizingLoadFactorThreshold, initialChainCount, chainInitialCapacity);
    }

    protected <K, V> AbstractIterableMap<K, V>[] extractChains(Map<K, V> map) {
        return ((ChainedHashMap<K, V>) map).chains;
    }

    protected <K, V> Map.Entry<K, V>[] extractEntries(Map<K, V> map) {
        return ((ArrayMap<K, V>) map).entries;
    }

    @Test
    void constructor_withInitialChainCount_hasCorrectInitialChainCount() {
        Map<String, Integer> map = createMap(100, 1, 10);
        map.put("foo", 1);
        Map<String, Integer>[] chains = extractChains(map);
        assertThat(chains).hasSize(1);
    }

    @Test
    void constructor_withInitialChainCapacity_hasCorrectInitialChainCapacity() {
        Map<String, Integer> map = createMap(100, 1, 10);
        map.put("foo", 1);
        Map<String, Integer>[] chains = extractChains(map);
        Map.Entry<String, Integer>[] chainEntries = extractEntries(chains[0]);
        assertThat(chainEntries).hasSize(10);
    }

    @Test
    void constructor_withResizeLoadFactorThreshold_resizesAtCorrectTime() {
        Map<Integer, Integer> map = createMap(100, 1, 10);
        for (int i = 0; i < 99; i++) {
            map.put(i, i);
        }
        Map<Integer, Integer>[] chains = extractChains(map);
        assertThat(chains).hasSize(1);

        map.put(99, 99);
        map.put(100, 100);

        Map<Integer, Integer>[] newChains = extractChains(map);
        assertThat(newChains).hasSizeGreaterThan(1);
    }

    @Test
    void get_afterPutKeyWithLargeHashCode_returnsCorrectValue() {
        Map<Wrapper<Integer>, Integer> map = createMap();
        map.put(new Wrapper<>(1, 10000000), 5);
        assertThat(map).containsEntry(new Wrapper<>(1, 10000000), 5);
    }

    @Test
    void get_afterPutKeyWithNegativeHashCode_returnsCorrectValue() {
        Map<Wrapper<Integer>, Integer> map = createMap();
        map.put(new Wrapper<>(1, -100), 5);
        assertThat(map).containsEntry(new Wrapper<>(1, -100), 5);
    }

    @Test
    void getEach_afterPutManyKeysWithSameHashCode_returnsCorrectValues() {
        Map<Wrapper<Integer>, Integer> map = createMap(2, 8, 16);
        Map<Wrapper<Integer>, Integer> actual = new HashMap<>();
        final int size = 100;
        for (int i = 0; i < size; i++) {
            map.put(new Wrapper<>(i, 3), i);
            actual.put(new Wrapper<>(i, 3), i);
        }
        assertThat(map).containsAllEntriesOf(actual);
    }

    @Test
    void ensureCreateChainMethodCanBeOverridden() {
        // The grader will use similar code to override `createChain`, so make sure this compiles.
        new ChainedHashMap<Integer, Integer>() {
            @Override
            protected AbstractIterableMap<Integer, Integer> createChain(int initialSize) {
                return null;
            }
        };
    }

}
