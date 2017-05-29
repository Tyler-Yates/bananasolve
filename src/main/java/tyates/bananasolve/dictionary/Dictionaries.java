package tyates.bananasolve.dictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Helper class for instantiating Dictionary instances.
 */
public final class Dictionaries {
    /**
     * Creates a dictionary that uses all valid words from all the given dictionaries.
     * <p>
     * If a word is considered valid by at least one dictionary, then that word is considered valid by the compound
     * dictionary.
     *
     * @param dictionaries the given dictionaries
     * @return the compound dictionary
     */
    public static Dictionary compoundDictionary(final Dictionary... dictionaries) {
        final Set<String> validWords = new HashSet<>();
        for (final Dictionary dictionary : dictionaries) {
            validWords.addAll(dictionary.validWords());
        }
        return new ImmutableSetDictionary(validWords);
    }

    private static Set<String> loadDictionaryFile(final String resourceFileName) throws FileNotFoundException {
        final URL dictFileUrl = AbstractDictionary.class.getClassLoader().getResource(resourceFileName);
        final Scanner scanner = new Scanner(new File(dictFileUrl.getFile()));

        final Set<String> validWords = new HashSet<>();
        while (scanner.hasNext()) {
            validWords.add(scanner.nextLine());
        }
        return validWords;
    }

    public static Dictionary oneThousandEnglishWordsDictionary() throws FileNotFoundException {
        return new ImmutableSetDictionary(loadDictionaryFile("thousand_english_dictionary.txt"));
    }

    public static Dictionary fiveThousandEnglishWordsDictionary() throws FileNotFoundException {
        return new ImmutableSetDictionary(loadDictionaryFile("five_thousand_english_dictionary.txt"));
    }

    public static Dictionary tenThousandEnglishWordsDictionary() throws FileNotFoundException {
        return new ImmutableSetDictionary(loadDictionaryFile("ten_thousand_english_dictionary.txt"));
    }
}
