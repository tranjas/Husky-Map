package autocomplete.cities;

import autocomplete.Autocomplete;
import autocomplete.SequentialSearchAutocomplete;

/**
 * Tests for the {@link SequentialSearchAutocomplete} class.
 *
 * @see SequentialSearchAutocomplete
 */
public class SequentialSearchAutocompleteTests extends AutocompleteTests {
    @Override
    public Autocomplete createAutocomplete() {
        return new SequentialSearchAutocomplete();
    }
}
