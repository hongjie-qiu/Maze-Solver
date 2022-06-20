package maps;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;

public class ArrayMapTests extends BaseMapTests {
    @Override
    protected <K, V> Map<K, V> createMap() {
        return new ArrayMap<>();
    }

    protected <K, V> Map<K, V> createMap(int capacity) {
        return new ArrayMap<>(capacity);
    }

    @Test
    void iterator_hasNext_afterExhausted_whenArrayIsFull_returnsFalse() {
        int capacity = 373;
        Map<Integer, Integer> map = createMap(capacity);

        // fill array to capacity - 1
        for (int i = 0; i < capacity - 1; i++) {
            map.put(i, i);
        }

        // make sure iterator works during capacity - 1
        Iterator<Map.Entry<Integer, Integer>> iterator1 = map.entrySet().iterator();
        exhaust(iterator1);
        assertThat(iterator1).as("size == capacity - 1").isExhausted();

        // makes sure iterator works during full capacity
        map.put(-1, -1);
        Iterator<Map.Entry<Integer, Integer>> iterator2 = map.entrySet().iterator();
        exhaust(iterator2);
        assertThat(iterator2).as("size == capacity").isExhausted();
    }
}
