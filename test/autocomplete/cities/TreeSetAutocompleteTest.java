package autocomplete.cities;

import autocomplete.Autocomplete;
import autocomplete.SequentialSearchAutocomplete;
import autocomplete.TreeSetAutocomplete;

public class TreeSetAutocompleteTest extends AutocompleteTests{
    public Autocomplete createAutocomplete() {
        return new TreeSetAutocomplete();
    }
}





