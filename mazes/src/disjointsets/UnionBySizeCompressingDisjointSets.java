package disjointsets;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * A quick-union-by-size data structure with path compression.
 *
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    List<Integer> pointers;
    private Map<T, Integer> map;
    private int index;

    public UnionBySizeCompressingDisjointSets() {
        pointers = new ArrayList<>();
        map = new HashMap<>();
        index = 0;
    }


    @Override
    public void makeSet(T item) {
        if (map.containsKey(item)) {
            throw new IllegalArgumentException();
        } else {
            pointers.add(-1);
            map.put(item, index);
            index++;
        }
    }

    @Override
    public int findSet(T item) {
        if (!map.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        List<Integer> processed = new ArrayList<>();
        int pointer = pointers.get(map.get(item));
        if (pointer >= 0) {
            processed.add(map.get(item));
        }
        int parent = pointer;
        while (parent >= 0 && pointers.get(parent) >= 0) {
            processed.add(parent);
            parent = pointers.get(parent);
        }
        for (int curr : processed) {
            pointers.set(curr, parent);
        }
        if (parent < 0) {
            return map.get(item);
        }
        return parent;
    }

    @Override
    public boolean union(T item1, T item2) {
        if (this.findSet(item1) != this.findSet(item2)) {
            int item1Index = this.findSet(item1);
            int item1Weight = Math.abs(pointers.get(item1Index));
            int item2Index = this.findSet(item2);
            int item2Weight = Math.abs(pointers.get(item2Index));
            int totalWeight = -item2Weight - item1Weight;
            if (item1Weight <= item2Weight) {
                pointers.set(item1Index, item2Index);
                pointers.set(item2Index, totalWeight);
            } else {
                pointers.set(item2Index, item1Index);
                pointers.set(item1Index, totalWeight);
            }
            return true;
        } else {
            return false;
        }
    }
}
