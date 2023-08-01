package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Binary search implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class BinarySearchAutocomplete implements Autocomplete {
    /**
     * {@link List} of added autocompletion terms.
     */
    private final List<CharSequence> terms;

    /**
     * Constructs an empty instance.
     */
    public BinarySearchAutocomplete() {
        this.terms = new ArrayList<>();
    }

    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        // TODO: Replace with your code
        this.terms.addAll(terms);
        Collections.sort(this.terms, CharSequence::compare);
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        List<CharSequence> result = new ArrayList<>();
        //Collections.sort(terms, CharSequence::compare);
        if (prefix == null || prefix.length() == 0) {
            return result;
        }
        int start = Collections.binarySearch(terms, prefix, CharSequence::compare);
        if (start < 0) {
            start = -(start + 1);
        }
        while (Autocomplete.isPrefixOf(prefix, terms.get(start))) {
            result.add(terms.get(start));
            start++;
        }
       return result;
       // throw new UnsupportedOperationException("Not implemented yet");
    }
}
