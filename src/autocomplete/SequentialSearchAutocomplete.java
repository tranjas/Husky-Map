package autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sequential search implementation of the {@link Autocomplete} interface.
 *
 * @see Autocomplete
 */
public class SequentialSearchAutocomplete implements Autocomplete {
    /**
     * {@link List} of added autocompletion terms.
     */
    private final List<CharSequence> terms;

    /**
     * Constructs an empty instance.
     */
    public SequentialSearchAutocomplete() {
        this.terms = new ArrayList<>();
    }

    @Override
    public void addAll(Collection<? extends CharSequence> terms) {
        // TODO: Replace with your code
        this.terms.addAll(terms);
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<CharSequence> allMatches(CharSequence prefix) {
        // TODO: Replace with your code
        List<CharSequence> result = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            CharSequence word = terms.get(i);
            if (startsWith(word, prefix)){
                result.add(word);
            }
//            if (Autocomplete.isPrefixOf(word, prefix)) {
//                result.add(word);
//            }
        }

        return result;
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    public static boolean startsWith(CharSequence word, CharSequence prefix) {
        if (word == null) {
            return false;
        }

        if (prefix == null || prefix.length() == 0) {
            return true;
        }

        int len = prefix.length();

        if (word.length() < len) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            char ch = word.charAt(i);
            char prefixCh = prefix.charAt(i);

            if (ch != prefixCh) {
                return false;
            }
        }
        return true;
    }
}
