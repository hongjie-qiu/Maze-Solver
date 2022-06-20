package maps;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.MapAssert;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.error.GroupTypeDescription;
import org.assertj.core.internal.ComparisonStrategy;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Objects;
import org.assertj.core.internal.StandardComparisonStrategy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.error.GroupTypeDescription.getGroupTypeDescription;

/**
 * Assertions for {@link Map}s.
 *
 * Includes slight changes from AssertJ's provided assertions to improve feedback
 * provided by failure messages.
 */
public class CustomMapAssert<K, V> extends MapAssert<K, V> {

    Failures failures = Failures.instance();

    public CustomMapAssert(Map<K, V> actual) {
        super(actual);
    }

    @Override
    public MapAssert<K, V> containsAllEntriesOf(Map<? extends K, ? extends V> other) {
        assertNotNull(info, actual);

        Map<K, V> getValueMismatch = new LinkedHashMap<>();
        Set<K> missingNullValuedEntries = new LinkedHashSet<>();

        for (Map.Entry<? extends K, ? extends V> entry : other.entrySet()) {
            if (entry.getValue() == null) {
                if (!actual.containsKey(entry.getKey())) {
                    missingNullValuedEntries.add(entry.getKey());
                    continue;
                }
            }
            Optional<Optional<V>> mismatchedGetValue = obtainGetValueMismatch(actual, entry);
            mismatchedGetValue.ifPresent(v -> getValueMismatch.put(entry.getKey(), v.orElse(null)));
        }
        if (getValueMismatch.isEmpty() && missingNullValuedEntries.isEmpty()) {
            return myself;
        }

        throw failures.failure(info, ShouldContainMultiple.shouldContain(
            actual, other, getValueMismatch, missingNullValuedEntries));
    }

    @Override
    public MapAssert<K, V> containsEntry(K key, V value) {
        assertNotNull(info, actual);

        Map.Entry<K, V> entry = entry(key, value);

        if (value == null) {
            // throw if value is null and key is not in actual
            containsKey(key);
        }
        Optional<Optional<V>> mismatchedGetValue = obtainGetValueMismatch(actual, entry);
        if (mismatchedGetValue.isPresent()) {
            throw failures.failure(info, MismatchedGet.shouldMatch(actual, entry,
                mismatchedGetValue.get().orElse(null)));
        }

        return myself;
    }

    /**
     * returns an optional:
     *  - empty if get on the actual map matched the value of the entry;
     *  - containing an optional:
     *      - empty if the actual get value was null
     *      - containing the actual get value if it was non-null
     *      (this needs to be a nested Optional since a null-valued Optional is treated as empty,
     *      meaning that the Optional would always be empty if actual.get returned null.)
     */
    private static <K, V> Optional<Optional<V>> obtainGetValueMismatch(Map<K, V> actual,
                                                                       Map.Entry<? extends K, ? extends V> entry) {
        V value = actual.get(entry.getKey());
        if (java.util.Objects.deepEquals(value, entry.getValue())) {
            return Optional.empty();
        }
        return Optional.of(Optional.ofNullable(value));
    }

    @Override
    public MapAssert<K, V> containsKey(K key) {
        assertNotNull(info, actual);

        if (!actual.containsKey(key)) {
            throw failures.failure(info, ContainsKeyFailure.shouldContainKey(actual, key));
        }
        return myself;
    }

    /**
     * Verifies that the actual map contains the given keys.
     * <p>
     * Example :
     * <pre><code class='java'> Map&lt;Ring, TolkienCharacter&gt; ringBearers = new HashMap&lt;&gt;();
     * ringBearers.put(nenya, galadriel);
     * ringBearers.put(narya, gandalf);
     * ringBearers.put(oneRing, frodo);
     *
     * // assertions will pass
     * assertThat(ringBearers).containsKeys(List.of(nenya, oneRing));
     *
     * // assertions will fail
     * assertThat(ringBearers).containsKeys(List.of(vilya));
     * assertThat(ringBearers).containsKeys(List.of(vilya, oneRing));</code></pre>
     *
     * @param keys the given keys
     * @return {@code this} assertions object
     * @throws AssertionError if the actual map is {@code null}.
     * @throws AssertionError if the actual map does not contain the given key.
     */
    public MapAssert<K, V> containsKeys(Collection<? extends K> keys) {
        assertNotNull(info, actual);

        Set<K> missingKeys = new LinkedHashSet<>();
        for (K key : keys) {
            if (!actual.containsKey(key)) {
                missingKeys.add(key);
            }
        }
        if (!missingKeys.isEmpty()) {
            throw failures.failure(info, ContainsKeyFailure.shouldContainKeys(actual, missingKeys));
        }
        return myself;
    }

    @Override
    public MapAssert<K, V> doesNotContainKey(K key) {
        assertNotNull(info, actual);

        if (actual.containsKey(key)) {
            throw failures.failure(info, ContainsKeyFailure.shouldNotContainKey(actual, key));
        }
        return myself;
    }

    private void assertNotNull(AssertionInfo info, Map<?, ?> actual) {
        Objects.instance().assertNotNull(info, actual);
    }

    public static final class ContainsKeyFailure extends BasicErrorMessageFactory {
        private <K, V> ContainsKeyFailure(Map<K, V> actual, K key, boolean expectedBool, boolean actualBool) {
            super("%nExpecting:%n"
                    + " <%s>%n"
                    + "to " + (expectedBool ? "contain" : "not contain") + " key:%n"
                    + " <%s>%n"
                    + "but containsKey returned " + actualBool,
                actual, key);
        }

        private <K, V> ContainsKeyFailure(Map<K, V> actual, Set<K> keys, boolean expectedBool, boolean actualBool) {
            super("%nExpecting:%n"
                    + " <%s>%n"
                    + "to " + (expectedBool ? "contain" : "not contain") + " keys:%n"
                    + " <%s>%n"
                    + "but containsKey returned " + actualBool + " for each",
                actual, keys);
        }

        /**
         * Creates a new {@code ContainsKeyFailure}.
         *
         * @param actual the actual value in the failed assertion.
         * @param key the expected key
         * @return the created {@code ErrorMessageFactory}.
         */
        public static <K, V> ErrorMessageFactory shouldContainKey(Map<K, V> actual, K key) {
            return new ContainsKeyFailure(actual, key, true, false);
        }

        /**
         * Creates a new {@code ContainsKeyFailure}.
         *
         * @param actual the actual value in the failed assertion.
         * @param keys the expected keys
         * @return the created {@code ErrorMessageFactory}.
         */
        public static <K, V> ErrorMessageFactory shouldContainKeys(Map<K, V> actual, Set<K> keys) {
            if (keys.size() == 1) {
                return new ContainsKeyFailure(actual, keys.iterator().next(), true, false);
            }
            return new ContainsKeyFailure(actual, keys, true, false);
        }

        /**
         * Creates a new {@code ContainsKeyFailure}.
         *
         * @param actual the actual value in the failed assertion.
         * @param key the unexpected key
         * @return the created {@code ErrorMessageFactory}.
         */
        public static <V, K> ErrorMessageFactory shouldNotContainKey(Map<K, V> actual, K key) {
            return new ContainsKeyFailure(actual, key, false, true);
        }
    }

    /**
     * Creates an error message indicating that an assertion that verifies a map
     * contains a given entry failed because the map returned an unexpected and non-null value.
     * It also mentions the {@link ComparisonStrategy} used.
     */
    public static final class MismatchedGet extends BasicErrorMessageFactory {
        private <K, V> MismatchedGet(Map<K, V> actual,
                                     Map.Entry<K, V> expected,
                                     V actualGetValue,
                                     ComparisonStrategy comparisonStrategy,
                                     GroupTypeDescription groupTypeDescription) {
            super("%nExpecting " + groupTypeDescription.getGroupTypeName() + ":%n"
                    + " <%s>%n"
                    + "to contain:%n"
                    + " <%s>%n"
                    + "but get returned:%n"
                    + " <%s>%n"
                    + "%s",
                actual, expected, actualGetValue,
                comparisonStrategy);
        }

        /**
         * Creates a new {@code MismatchedGet}.
         *
         * @param actual the actual value in the failed assertion.
         * @param expected entry expected to be in {@code actual}.
         * @param actualGetValue the value returned by {@code actual.get}.
         * @return the created {@code ErrorMessageFactory}.
         */
        public static <K, V> ErrorMessageFactory shouldMatch(Map<K, V> actual,
                                                             Map.Entry<K, V> expected,
                                                             V actualGetValue) {
            return shouldMatch(actual, expected, actualGetValue, StandardComparisonStrategy.instance());
        }

        /**
         * Creates a new {@code MismatchedGet}.
         *
         * @param actual the actual value in the failed assertion.
         * @param expected entry expected to be in {@code actual}.
         * @param actualGetValue the value returned by {@code actual.get}.
         * @param comparisonStrategy the {@link ComparisonStrategy} used to evaluate assertion.
         * @return the created {@code ErrorMessageFactory}.
         */
        public static <K, V> ErrorMessageFactory shouldMatch(Map<K, V> actual,
                                                             Map.Entry<K, V> expected,
                                                             V actualGetValue,
                                                             ComparisonStrategy comparisonStrategy) {
            GroupTypeDescription groupTypeDescription = getGroupTypeDescription(actual);
            return new MismatchedGet(actual, expected, actualGetValue, comparisonStrategy, groupTypeDescription);
        }
    }

    /**
     * Creates an error message indicating that an assertion that verifies a {@code Map}
     * contains a given set of entries failed.
     * It also mentions the {@link ComparisonStrategy} used.
     */
    private static final class ShouldContainMultiple extends BasicErrorMessageFactory {
        private <K, V> ShouldContainMultiple(Map<K, V> actual,
                                             Map<? extends K, ? extends V> expected,
                                             Map<K, V> mismatches,
                                             Set<K> notFound,
                                             ComparisonStrategy comparisonStrategy,
                                             GroupTypeDescription groupTypeDescription) {
            super("%nExpecting " + groupTypeDescription.getGroupTypeName() + ":%n"
                    + " <%s>%n"
                    + "to contain:%n"
                    + " <%s>%n"
                    + "but get calls returned unexpected values for the following:%n"
                    + " <%s>%n"
                    + "and some keys were not contained:%n"
                    + " <%s>%n"
                    + "%s",
                actual, expected, mismatches, notFound, comparisonStrategy);
        }

        private <K, V> ShouldContainMultiple(Map<K, V> actual,
                                             Map<? extends K, ? extends V> expected,
                                             Map<K, V> mismatches,
                                             ComparisonStrategy comparisonStrategy,
                                             GroupTypeDescription groupTypeDescription) {
            super("%nExpecting " + groupTypeDescription.getGroupTypeName() + ":%n"
                    + " <%s>%n"
                    + "to contain:%n"
                    + " <%s>%n"
                    + "but get calls returned unexpected values for the following:%n"
                    + " <%s>%n"
                    + "%s",
                actual, expected, mismatches, comparisonStrategy);
        }

        private <K, V> ShouldContainMultiple(Map<K, V> actual,
                                             Map<? extends K, ? extends V> expected,
                                             Set<K> notFound,
                                             ComparisonStrategy comparisonStrategy,
                                             GroupTypeDescription groupTypeDescription) {
            super("%nExpecting " + groupTypeDescription.getGroupTypeName() + ":%n"
                    + " <%s>%n"
                    + "to contain:%n"
                    + " <%s>%n"
                    + "but some keys were not contained:%n"
                    + " <%s>%n"
                    + "%s",
                actual, expected, notFound, comparisonStrategy);
        }

        /**
         * Creates a new {@code MismatchedGet}.
         *
         * @param actual the actual value in the failed assertion.
         * @param expected map containing entries expected to be in {@code actual}.
         * @param mismatches map containing mismatched values returned by {@code actual.get}.
         * @param notFound set of expected keys not contained by the actual map
         *                 (only includes keys which were expected to have null values)
         * @return the created {@code ErrorMessageFactory}.
         */
        public static <K, V> ErrorMessageFactory shouldContain(Map<K, V> actual,
                                                               Map<? extends K, ? extends V> expected,
                                                               Map<K, V> mismatches,
                                                               Set<K> notFound) {
            return shouldContain(actual, expected, mismatches, notFound, StandardComparisonStrategy.instance());
        }

        /**
         * Creates a new {@code MismatchedGet}.
         *
         * @param actual the actual value in the failed assertion.
         * @param expected map containing entries expected to be in {@code actual}.
         * @param mismatches map containing mismatched values returned by {@code actual.get}.
         * @param notFound set of expected keys not contained by the actual map
         *                 (only includes keys which were expected to have null values)
         * @param comparisonStrategy the {@link ComparisonStrategy} used to evaluate assertion.
         * @return the created {@code ErrorMessageFactory}.
         */
        public static <K, V> ErrorMessageFactory shouldContain(Map<K, V> actual,
                                                               Map<? extends K, ? extends V> expected,
                                                               Map<K, V> mismatches,
                                                               Set<K> notFound,
                                                               ComparisonStrategy comparisonStrategy) {
            GroupTypeDescription groupTypeDescription = getGroupTypeDescription(actual);
            if (mismatches.isEmpty()) {
                return new ShouldContainMultiple(
                    actual,
                    expected,
                    notFound,
                    comparisonStrategy,
                    groupTypeDescription);
            }
            if (notFound.isEmpty()) {
                return new ShouldContainMultiple(
                    actual,
                    expected,
                    mismatches,
                    comparisonStrategy,
                    groupTypeDescription);
            }
            return new ShouldContainMultiple(
                actual,
                expected,
                mismatches,
                notFound,
                comparisonStrategy,
                groupTypeDescription);
        }
    }
}
