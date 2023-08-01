package autocomplete;

import java.util.ArrayList;
import java.util.List;

public class SimpleExample {
    public static void main(String[] args) {
        List<CharSequence> terms = new ArrayList<>();
        terms.add("a");
        terms.add("bc");
        terms.add("belugado");
        terms.add("delta");
        terms.add("do");
        terms.add("dodgy");
        terms.add("pilot");
        terms.add("dog");
        terms.add("doooodoo");
        terms.add("dodge");

// Choose your Autocomplete implementation.
        Autocomplete autocomplete = new BinarySearchAutocomplete();
        autocomplete.addAll(terms);
// Choose your prefix string.
        CharSequence prefix = "do";
        List<CharSequence> matches = autocomplete.allMatches(prefix);
        for (CharSequence match : matches) {
            System.out.println(match);
        }
    }
}
