package priorityqueues;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.NoSuchElementException;


public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    static final int START_INDEX = 1;
    List<PriorityNode<T>> items;
    private HashMap<T, Integer> map;
    private int size;

    public ArrayHeapMinPQ() {
        this.items = new ArrayList<>();
        this.items.add(null);
        this.size = 0;
        this.map = new HashMap<T, Integer>();
    }

    @Override
    public void add(T item, double priority) {
        if (this.contains(item)) {
            throw new IllegalArgumentException();
        }
        size++;
        this.items.add(new PriorityNode<>(item, priority));
        map.put(item, size);
        percolateUp(size);
    }

    @Override
    public boolean contains(T item) {
        return map.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return items.get(1).getItem();
    }

    @Override
    public T removeMin() {
        T min = peekMin();
        items.set(1, items.get(size));
        map.remove(min);
        map.put(peekMin(), 1);
        items.remove(size);
        size--;
        if (size > 1) {
            percolateDown(1);
        }
        return min;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!this.contains(item)) {
            throw new NoSuchElementException();
        }
        int index = map.get(item);
        this.items.get(index).setPriority(priority);
        percolateDown(index);
        percolateUp(index);
    }

    @Override
    public int size() {
        return size;
    }

    // Helper methods

    private void swap(int curr, int par) {
        PriorityNode<T> temp = items.get(curr);
        items.set(curr, items.get(par));
        items.set(par, temp);

        map.put(items.get(curr).getItem(), curr);
        map.put(items.get(par).getItem(), par);
    }

    private void percolateUp(int curr) {
        if (curr == 1) {
            return;
        }
        int par = curr / 2;
        if (curr > 1 && isSmaller(curr, par)) {
            swap(curr, par);
            percolateUp(curr / 2);
        }
    }

    private void percolateDown(int curr) {
        int leftNode = curr * 2;
        int rightNode = curr * 2 + 1;

        if (leftNode <= size) {
            if (rightNode <= size) {
                int smaller = min(leftNode, rightNode);
                if (this.items.get(smaller) != null) {
                    if (isSmaller(smaller, curr)) {
                        swap(curr, smaller);
                        percolateDown(smaller);
                    }
                }
            } else {
                if (leftNode == min(curr, leftNode)) {
                    swap(curr, leftNode);
                    percolateDown(leftNode);
                }
            }
        }
    }

    private boolean isSmaller(int a, int b) {
        return items.get(a).getPriority() < items.get(b).getPriority();
    }

    private int min(int a, int b) {
        if (isSmaller(a, b)) {
            return a;
        }
        return b;
    }

}
