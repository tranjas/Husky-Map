package minpq;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Unsorted array (or {@link ArrayList}) implementation of the {@link ExtrinsicMinPQ} interface.
 *
 * @param <T> the type of elements in this priority queue.
 * @see ExtrinsicMinPQ
 */
public class UnsortedArrayMinPQ<T> implements ExtrinsicMinPQ<T> {
    /**
     * {@link List} of {@link PriorityNode} objects representing the item-priority pairs in no specific order.
     */
    private final List<PriorityNode<T>> items;

    /**
     * Constructs an empty instance.
     */
    public UnsortedArrayMinPQ() {
        items = new ArrayList<>();
    }

    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException("Already contains " + item);
        }
        // TODO: Replace with your code
        items.add(new PriorityNode<>(item, priority));
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean contains(T item) {
        // TODO: Replace with your code
        Boolean check = false;
        for (int i = 0; i < items.size(); i++) {
            PriorityNode<T> tempNode = items.get(i);
            if (tempNode.item().equals(item)){
                check = true;
            }
        }
        return check;

         //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public T peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        // TODO: Replace with your code
        Double min = Double.MAX_VALUE;
        PriorityNode<T> minNode = new PriorityNode<T>(null, 0);
        for (int i = 0; i < items.size(); i++) {
            PriorityNode<T> tempNode = items.get(i);
            Double tempPriority = tempNode.priority();
            if (tempPriority < min){
                min = tempPriority;
                minNode = tempNode;
            }
        }
        return minNode.item();
    }

    @Override
    public T removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("PQ is empty");
        }
        // TODO: Replace with your code
        T tempMin = peekMin();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).item().equals(tempMin)) {
                items.remove(i);
            }
        }
        return tempMin;
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException("PQ does not contain " + item);
        }
        // TODO: Replace with your code
        for (int i = 0; i < items.size(); i++) {
            PriorityNode<T> tempNode = items.get(i);
            if (tempNode.item().equals(item)) {
                items.remove(tempNode);
                items.add(items.size(), new PriorityNode<T>(item, priority));
            }
        }
       //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int size() {
        // TODO: Replace with your code
        return items.size();
        //throw new UnsupportedOperationException("Not implemented yet");
    }
}
