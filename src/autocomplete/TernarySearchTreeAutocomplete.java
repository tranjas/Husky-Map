package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.*;

/**
 * Ternary search tree (TST) implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class TernarySearchTreeAutocomplete implements Autocomplete {
    /**
     * The overall root of the tree: the first character of the first autocompletion term added to this tree.
     */
    private Node overallRoot;

    /**
     * Constructs an empty instance.
     */
    public TernarySearchTreeAutocomplete() {
        overallRoot = null;
    }

    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        for (CharSequence s : terms) {
            overallRoot = put(overallRoot, s, 0);
        }
    }

    private Node put(Node x, CharSequence key, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node(c);
        }
        if (c < x.data) {
            x.left  = put(x.left,  key, d);
        }
        else if (c > x.data) {
            x.right = put(x.right, key, d);
        }
        else {
            if (d < key.length() - 1) {
                x.mid   = put(x.mid,   key,  d+1);
            }  else {
                x.isTerm = true;
            }
        }
        return x;
    }
    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        // TODO: Replace with your code
        List<CharSequence> result = new ArrayList<>();
        if (prefix == null || prefix.length() == 0) {
            return result;
        }
        Iterable<CharSequence> resultTree = keysWithPrefix(prefix);
        resultTree.forEach(result::add);
        return result;
        //throw new UnsupportedOperationException("Not implemented yet");
    }


    public Iterable<CharSequence> keysWithPrefix(CharSequence prefix) {
        Queue<CharSequence> queue = new LinkedList<>();
        Node x = get(overallRoot, prefix, 0);
        if (x == null) return queue;
        if (x.isTerm) queue.add(prefix);
        collect(x.mid, new StringBuilder(prefix), queue);
        return queue;
    }


// return subtrie corresponding to given key
    private Node get(Node x, CharSequence key, int d) {
        if (x == null) return null;
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        char c = key.charAt(d);
        if      (c < x.data)              return get(x.left,  key, d);
        else if (c > x.data)              return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid,   key, d+1);
        else                           return x;
    }

    // all keys in subtrie rooted at x with given prefix
    private void collect(Node x, StringBuilder prefix, Queue<CharSequence> queue) {
        if (x == null) return;
        collect(x.left,  prefix, queue);
        if (x.isTerm) queue.add(prefix.toString() + x.data);
        collect(x.mid,   prefix.append(x.data), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(x.right, prefix, queue);
    }

    private static class Node {
        private final char data;
        private boolean isTerm; //this is the term
        private Node left;
        private Node mid;
        private Node right;

        public Node(char data) {
            this.data = data;
            this.isTerm = false;
            this.left = null;
            this.mid = null;
            this.right = null;
        }
    }
}
