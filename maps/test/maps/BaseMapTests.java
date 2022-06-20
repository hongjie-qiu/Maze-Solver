package maps;

import edu.washington.cse373.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class BaseMapTests extends BaseTest {
    // some keys and values used across multiple tests
    protected static final String KEY = "someKey";
    protected static final String VAL = "someVal";
    protected static final String OLD_VAL = "oldVal";

    protected abstract <K, V> Map<K, V> createMap();

    public <K, V> CustomMapAssert<K, V> assertThat(Map<K, V> map) {
        return new CustomMapAssert<>(map);
    }


    // ---------------- Empty ----------------

    @Test
    void Put_100_Then_Remove95_Size_Is_5() {
        final int size = 100;
        Map<Integer, Integer> map = createMap();
        Map<Integer, Integer> expected = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(i, i * i);
            expected.put(i, i * i);
        }

        for (int i = 0; i < 95; i++) {
            map.remove(i);
        }

        assertThat(map).hasSize(5);
    }

    @Test
    void size_is0() {
        Map<String, String> map = createMap();
        assertThat(map).hasSize(0);
    }

    @Test
    void containsKey_isFalse() {
        Map<String, String> map = createMap();
        assertThat(map).doesNotContainKey("foo");
    }

    @Test
    void get_returnsNull() {
        Map<String, String> map = createMap();
        String output = map.get("foo");
            assertThat(output).as("return value").isNull();
    }

    @Test
    void put_returnsNull() {
        Map<String, String> map = createMap();
        String output = map.put("foo", "bar");
            assertThat(output).as("return value").isNull();
    }

    @Test
    void remove_returnsNull() {
        Map<String, String> map = createMap();
        String output = map.remove("foo");
            assertThat(output).as("return value").isNull();
    }

    @Test
    void iterator_hasNext_returnsFalse() {
        Map<String, String> map = createMap();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        assertThat(iterator).isExhausted();
    }

    @Test
    void iterator_hasNext_twice_returnsFalse() {
        Map<String, String> map = createMap();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        iterator.hasNext();
        assertThat(iterator).isExhausted();
    }

    @Test
    void iterator_next_throwsNoSuchElement() {
        Map<String, String> map = createMap();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    // ---------------- 1 Entry ----------------

    Map<String, String> createMapWith1Entry() {
        Map<String, String> map = createMap();
        map.put(KEY, VAL);
        return map;
    }

    @Test
    void size_is1() {
        Map<String, String> map = createMapWith1Entry();
        assertThat(map).hasSize(1);
    }

    @Test
    void containsKey_returnsTrue() {
        Map<String, String> map = createMapWith1Entry();
        assertThat(map).containsKey(KEY);
    }

    @Test
    void containsKey_withNewKey_returnsFalse() {
        Map<String, String> map = createMapWith1Entry();
        assertThat(map).doesNotContainKey("someOtherKey");
    }

    @Test
    void get_returnsCorrectValue() {
        Map<String, String> map = createMapWith1Entry();
        String output = map.get(KEY);
        assertThat(output).as("return value").isEqualTo(VAL);
    }

    @Test
    void get_newKey_returnsNull() {
        Map<String, String> map = createMapWith1Entry();
        String output = map.get("someOtherKey");
        assertThat(output).as("return value").isNull();
    }

    @Test
    void put_newKey_returnsNull() {
        Map<String, String> map = createMapWith1Entry();
        String output = map.put("someOtherKey", VAL);
        assertThat(output).as("return value").isNull();
    }

    @Test
    void put_sameKey_returnsCorrectValue() {
        Map<String, String> map = createMapWith1Entry();
        String output = map.put(KEY, "someOtherVal");
        assertThat(output).as("return value").isEqualTo(VAL);
    }

    @Test
    void put_sameKeyAndValue_returnsCorrectValue() {
        Map<String, String> map = createMapWith1Entry();
        String output = map.put(KEY, VAL);
        assertThat(output).as("return value").isEqualTo(VAL);
    }

    @Test
    void remove_returnsCorrectValue() {
        Map<String, String> map = createMapWith1Entry();
        String output = map.remove(KEY);
        assertThat(output).as("return value").isEqualTo(VAL);
    }

    @Test
    void remove_newKey_returnsNull() {
        Map<String, String> map = createMapWith1Entry();
        String output = map.remove("someOtherKey");
        assertThat(output).as("return value").isNull();
    }

    @Test
    void size_afterRemove_newKey_is1() {
        Map<String, String> map = createMapWith1Entry();
        map.remove("someOtherKey");
        assertThat(map).hasSize(1);
    }

    @Test
    void iterator_hasNext_returnsTrue() {
        Map<String, String> map = createMapWith1Entry();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        assertThat(iterator).hasNext();
    }

    @Test
    void iterator_hasNext_twice_returnsTrue() {
        Map<String, String> map = createMapWith1Entry();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        iterator.hasNext();
        assertThat(iterator).hasNext();
    }

    @Test
    void iterator_yieldsCorrectEntry() {
        Map<String, String> map = createMapWith1Entry();
        assertThat(map)
            .as("entries yielded by iterator")
            .containsExactly(entry(KEY, VAL));
    }

    @Test
    void iterator_hasNext_afterExhausted_returnsFalse() {
        Map<String, String> map = createMapWith1Entry();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        exhaust(iterator);
        assertThat(iterator).isExhausted();
    }

    @Test
    void iterator_next_afterExhausted_throwsNoSuchElement() {
        Map<String, String> map = createMapWith1Entry();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        exhaust(iterator);
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    // ---------------- Put And Remove Duplicate Key ----------------

    Map<String, String> createMapAfterPutAndRemoveDuplicateKey() {
        Map<String, String> map = createMap();
        map.put(KEY, OLD_VAL);
        map.put(KEY, VAL);
        map.remove(KEY);
        return map;
    }

    @Test
    void containsKey_withRemovedKey_returnsFalse() {
        Map<String, String> map = createMapAfterPutAndRemoveDuplicateKey();
        assertThat(map).doesNotContainKey(KEY);
    }

    @Test
    void get_removedKey_returnsNull() {
        Map<String, String> map = createMapAfterPutAndRemoveDuplicateKey();
        String output = map.get(KEY);
        assertThat(output).as("return value").isNull();
    }

    @Test
    void put_removedKey_returnsNull() {
        Map<String, String> map = createMapAfterPutAndRemoveDuplicateKey();
        String output = map.put(KEY, VAL);
        assertThat(output).as("return value").isNull();
    }

    @Test
    void remove_removedKey_returnsNull() {
        Map<String, String> map = createMapAfterPutAndRemoveDuplicateKey();
        String output = map.remove(KEY);
        assertThat(output).as("return value").isNull();
    }

    // ---------------- 5 Entries ----------------

    Map<Integer, Integer> createMapWith5Entries() {
        Map<Integer, Integer> map = createMap();
        for (int i = 0; i < 5; i++) {
            map.put(i, i * i);
        }
        return map;
    }

    @Test
    void size_5entries_is5() {
        Map<Integer, Integer> map = createMapWith5Entries();
        assertThat(map).hasSize(5);
    }

    @Test
    void containsKey_onEachKey_5entries_returnsTrue() {
        Map<Integer, Integer> map = createMapWith5Entries();
        for (int i = 0; i < 5; i++) {
            assertThat(map).as("key: " + i).containsKey(i);
        }
    }

    @Test
    void containsKey_withNewKey_5entries_returnsFalse() {
        Map<Integer, Integer> map = createMapWith5Entries();
        assertThat(map).doesNotContainKey(-1);
    }

    @Test
    void get_5entries_returnsCorrectValue() {
        Map<Integer, Integer> map = createMapWith5Entries();
        Integer output = map.get(3);
        assertThat(output).isEqualTo(9);
    }

    @Test
    void get_newKey_5entries_returnsNull() {
        Map<Integer, Integer> map = createMapWith5Entries();
        Integer output = map.get(-1);
        assertThat(output).isNull();
    }

    @Test
    void put_newKey_5entries_returnsNull() {
        Map<Integer, Integer> map = createMapWith5Entries();
        Integer output = map.put(5, 25);
        assertThat(output).isNull();
    }

    @Test
    void put_sameKey_5entries_returnsCorrectValue() {
        Map<Integer, Integer> map = createMapWith5Entries();
        Integer output = map.put(2, -5);
        assertThat(output).isEqualTo(4);
    }

    @Test
    void remove_5entries_returnsCorrectValue() {
        Map<Integer, Integer> map = createMapWith5Entries();
        Integer output = map.remove(1);
        assertThat(output).isEqualTo(1);
    }

    @Test
    void iterator_5entries_yieldsCorrectEntries() {
        Map<Integer, Integer> map = createMapWith5Entries();
        assertThat(map)
            .as("entries yielded by iterator")
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                0, 0,
                1, 1,
                2, 4,
                3, 9,
                4, 16
            ));
    }


    @Test
    void iterator_hasNext_afterExhausted_5entries_returnsFalse() {
        Map<Integer, Integer> map = createMapWith5Entries();
        Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
        exhaust(iterator);
        assertThat(iterator).isExhausted();
    }

    @Test
    void iterator_next_afterExhausted_5entries_throwsNoSuchElement() {
        Map<Integer, Integer> map = createMapWith5Entries();
        Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
        exhaust(iterator);
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void iterator_afterRemoveOldestKey_5entries_yieldsCorrectEntries() {
        Map<Integer, Integer> map = createMapWith5Entries();
        map.remove(0);
        assertThat(map)
            .as("entries yielded by iterator")
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                1, 1,
                2, 4,
                3, 9,
                4, 16
            ));
    }

    @Test
    void iterator_afterRemoveNewestKey_5entries_yieldsCorrectEntries() {
        Map<Integer, Integer> map = createMapWith5Entries();
        map.remove(4);
        assertThat(map)
            .as("entries yielded by iterator")
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                0, 0,
                1, 1,
                2, 4,
                3, 9
            ));
    }

    // ---------------- Custom Object Keys ----------------

    @Test
    void containsKey_wrapper_returnsTrue() {
        Map<Wrapper<Integer>, Integer> map = createMap();
        map.put(new Wrapper<>(1), 1);
        assertThat(map).containsKey(new Wrapper<>(1));
    }

    @Test
    void get_wrapper_returnsCorrectValue() {
        Map<Wrapper<Integer>, Integer> map = createMap();
        map.put(new Wrapper<>(1), 1);
        assertThat(map).containsEntry(new Wrapper<>(1), 1);
    }

    @Test
    void put_wrapper_sameKey_returnsCorrectValue() {
        Map<Wrapper<Integer>, Integer> map = createMap();
        map.put(new Wrapper<>(1), 1);
        Integer output = map.put(new Wrapper<>(1), 15);
        assertThat(output).as("return value").isEqualTo(1);
    }

    @Test
    void remove_wrapper_returnsCorrectValue() {
        Map<Wrapper<Integer>, Integer> map = createMap();
        map.put(new Wrapper<>(1), 1);
        Integer output = map.remove(new Wrapper<>(1));
        assertThat(output).as("return value").isEqualTo(1);
    }

    @Test
    void containsKey_afterPutNullKey_returnsTrue() {
        Map<Integer, Integer> map = createMap();
        map.put(null, 10);

        assertThat(map).containsKey(null);
    }

    @Test
    void get_afterPutNullKey_returnsCorrectValue() {
        Map<Integer, Integer> map = createMap();
        map.put(null, 10);

        assertThat(map).containsEntry(null, 10);
    }

    @Test
    void containsKey_afterPutNullValue_returnsTrue() {
        Map<Integer, Integer> map = createMap();
        map.put(100, null);

        assertThat(map).containsKey(100);
    }

    @Test
    void get_afterPutNullValue_returnsNull() {
        Map<Integer, Integer> map = createMap();
        map.put(100, null);

        assertThat(map).containsEntry(100, null);
    }

    @Test
    void containsKeyEach_afterPutMany_returnsCorrectValues() {
        final int size = 100;
        Map<Integer, Integer> map = createMap();
        List<Integer> expectedKeys = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            map.put(i, i*i);
            expectedKeys.add(i);
        }

        assertThat(map).containsKeys(expectedKeys);
    }

    @Test
    void getEach_afterPutMany_returnsCorrectValues() {
        final int size = 100;
        Map<Integer, Integer> map = createMap();
        Map<Integer, Integer> expected = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(i, i*i);
            expected.put(i, i*i);
        }

        assertThat(map).containsAllEntriesOf(expected);
    }

    @Test
    void iterator_afterPutMany_yieldsCorrectValues() {
        final int size = 100;
        Map<Integer, Integer> map = createMap();
        Map<Integer, Integer> expected = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(i, i*i);
            expected.put(i, i*i);
        }

        assertThat(map)
            .as("entries yielded by iterator")
            .containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void iterator_yieldsCorrectEntries_afterUsingAnotherIterator() {
        final int size = 100;
        Map<Integer, Integer> map = createMap();
        Map<Integer, Integer> expected = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(i, i*i);
            expected.put(i, i*i);
        }
        exhaust(map.keySet().iterator());

        assertThat(map)
            .as("entries yielded by iterator")
            .containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void alternatingRemoveAndIterator_yieldsCorrectEntriesEachTime() {
        final int size = 100;
        Map<Integer, Integer> map = createMap();
        Map<Integer, Integer> expected = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(i, i*i);
            expected.put(i, i*i);
        }

        for (int i = 0; i < size; i++) {
            map.remove(i);
            expected.remove(i);
            assertThat(map)
                .as("entries yielded by iterator after removing %d/%d", i + 1, size)
                .containsExactlyInAnyOrderEntriesOf(expected);
        }
    }

    void exhaust(Iterator<?> iterator) {
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    /**
     * This is a wrapper class for arbitrary objects that additionally allows us to
     * define a custom hash code.
     *
     * If no hash code is provided, the object's existing hash code is used instead.
     *
     * It is up to the user to make sure that the hash codes assigned are valid.
     * (E.g., the user must ensure that two Wrapper objects with equal inner objects
     * also have equal hash codes).
     */
    protected static class Wrapper<T> {
        private final T inner;

        private final int hashCode;

        public Wrapper(T inner) {
            this(inner, inner == null ? 0 : inner.hashCode());
        }

        public Wrapper(T inner, int hashCode) {
            this.inner = inner;
            this.hashCode = hashCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            Wrapper<?> wrapper = (Wrapper<?>) o;

            return Objects.equals(inner, wrapper.inner);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public String toString() {
            return "Wrapper{" +
                "inner=" + inner +
                ", hashCode=" + hashCode +
                '}';
        }
    }


}
