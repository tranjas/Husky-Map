package minpq;

import java.util.*;
/**
 * Optimized binary heap implementation of the {@link ExtrinsicMinPQ} interface.
 *
 * @param <T> the type of elements in this priority queue.
 * @see ExtrinsicMinPQ
 */
public class OptimizedHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    /**
     * {@link List} of {@link PriorityNode} objects representing the heap of item-priority pairs.
     */
    private final List<PriorityNode<T>> items;
    /**
     * {@link Map} of each item to its associated index in the {@code items} heap.
     */
    private final Map<T, Integer> itemToIndex;

    /**
     * Constructs an empty instance.
     */
    public OptimizedHeapMinPQ() {
        items = new ArrayList<>();
        items.add(0,null);
        itemToIndex = new HashMap<>();
    }

    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException("Already contains " + item);
        }
        // TODO: Replace with your code

        items.add(new PriorityNode<T>(item, priority));
        itemToIndex.put(item, size());
        swim(size());
    }

    @Override
    public boolean contains(T item) {
        // TODO: Replace with your code
        return itemToIndex.get(item) != null;
        //throw new UnsupportedOperationException("Not implemented yet");

    }

    @Override
    public T peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        // TODO: Replace with your code
        return items.get(1).item();
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public T removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        T min = peekMin();
        swap(1, size());
        itemToIndex.remove(min); //update index
        items.remove(size());
        sink(1);

        return min;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException("PQ does not contain " + item);
        }
        // TODO: Replace with your code
        int tempIndex = itemToIndex.get(item);
        items.get(tempIndex).setPriority(priority);
        sink(tempIndex);
        swim(tempIndex);
    }

    @Override
    public int size() {
        // TODO: Replace with your code
        if (items.size() == 0) {
            return 0;
        }
        return items.size()-1;
    }

    /** Bubbles up the node currently at the given index. */
    private void swim(int index) {
        int parent = parent(index);
          while (accessible(parent) && items.get(index).priority() < items.get(parent).priority()) {
            swap(index, parent);
            index = parent;
            parent = parent(index);
        }
    }

    /** Bubbles down the node currently at the given index. */
    private void sink(int index) {
        int child = min(left(index), right(index));
        while (accessible(child) && items.get(index).priority() > items.get(child).priority()) {
            swap(index, child);
            index = child;
            child = min(left(index), right(index));
        }
    }

    private static int parent(int index) {
        return index / 2;
    }

    private boolean accessible(int index) {
        return 1 <= index && index <= size();
    }

    private int min(int index1, int index2) {
        if (!accessible(index1) && !accessible(index2)) {
            return 0;
        } else if (accessible(index1) && (!accessible(index2)
                || items.get(index1).priority() < items.get(index2).priority())) {
            return index1;
        } else {
            return index2;
        }
    }

    /** Swap the nodes at the two indices. */
    private void swap(int index1, int index2) {
        PriorityNode<T> temp = items.get(index1);
        items.set(index1, items.get(index2));
        items.set(index2, temp);
        T temp1 = items.get(index1).item();
        T temp2 = items.get(index2).item();
        itemToIndex.put(temp1, index1);
        itemToIndex.put(temp2, index2);
    }

    /** Returns the index of the given index's left child. */
    private static int left(int index) {
        return index * 2;
    }

    /** Returns the index of the given index's right child. */
    private static int right(int index) {
        return left(index) + 1;
    }
}
