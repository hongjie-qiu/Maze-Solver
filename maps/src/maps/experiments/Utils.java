package maps.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    /**
     * Generates and returns a list of `numStrings` random "strings", each of length `stringLength`.
     * (Uses `stringConstructor` to construct strings, so outputs can be custom string-like types.)
     */
    public static <STRING> List<STRING> generateRandomStrings(long numStrings,
                                                              int stringLength,
                                                              Function<char[], STRING> stringConstructor) {
        // define a seed for the random number generator, so that experiment results are consistent
        Random rand = new Random(numStrings);

        // use Java's Stream API to build a list of random STRING objects
        return Stream.generate(() -> {
            // define code to generate each STRING
            char[] array = new char[stringLength];
            for (int j = 0; j < stringLength; j++) {
                array[j] = (char) (rand.nextInt('z' - 'a') + 'a');
            }
            return stringConstructor.apply(array);
        })
            .limit(numStrings)                                  // run the code numStrings times
            .collect(Collectors.toCollection(ArrayList::new));  // add all items into an ArrayList
    }
}
