package maps.experiments;

import edu.washington.cse373.experiments.AnalysisUtils;
import edu.washington.cse373.experiments.PlotWindow;
import maps.AVLTreeMap;
import maps.ChainedHashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;

public class Experiment2HashCodesAndAVLTrees {
    public static final long MAX_MAP_SIZE = 50000;
    public static final long STEP = 500;

    public static final int STRING_LENGTH = 50;

    public static void main(String[] args) {
        new Experiment2HashCodesAndAVLTrees().run();
    }

    public void run() {
        List<Long> sizes = AnalysisUtils.range(0L, MAX_MAP_SIZE, STEP);

        PlotWindow.launch("Experiment 2", "Map Size", "Elapsed Time (ms)",
            new LongUnaryOperator[]{this::runtime1, this::runtime2, this::runtime3, this::runtime4},
            new String[]{"runtime1", "runtime2", "runtime3", "runtime4"},
            sizes, 5, .05);
    }

    protected <K, V> Map<K, V> constructChainedHashMap() {
        return new ChainedHashMap<>();
    }

    public long runtime1(long mapSize) {
        return timePuts(mapSize, constructChainedHashMap(), FakeString1::new);
    }

    public long runtime2(long mapSize) {
        return timePuts(mapSize, constructChainedHashMap(), FakeString2::new);
    }

    public long runtime3(long mapSize) {
        return timePuts(mapSize, constructChainedHashMap(), FakeString3::new);
    }

    public long runtime4(long mapSize) {
        // This test uses an AVLTreeMap instead of a ChainedHashMap, and thus does not
        // use hash codes at all.
        return timePuts(mapSize, new AVLTreeMap<>(), ComparableFakeString::new);
    }

    public <STRING> long timePuts(long numPuts,
                                  Map<STRING, Integer> map,
                                  Function<char[], STRING> stringConstructor) {
        List<STRING> chars = Utils.generateRandomStrings(numPuts, STRING_LENGTH, stringConstructor);

        long start = System.currentTimeMillis();
        for (STRING string : chars) {
            map.put(string, 0);
        }
        return System.currentTimeMillis() - start;
    }


    public abstract static class FakeString {
        protected char[] chars;

        public FakeString(char[] chars) {
            this.chars = chars;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof FakeString)) {
                return false;
            }
            FakeString otherFake = (FakeString) other;
            if (this.chars.length != otherFake.chars.length) {
                return false;
            }
            for (int i = 0; i < this.chars.length; i++) {
                if (this.chars[i] != otherFake.chars[i]) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException();
        }
    }

    public static class FakeString1 extends FakeString {
        public FakeString1(char[] chars) {
            super(chars);
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other);
        }

        @Override
        public int hashCode() {
            // sum the first 4 chars
            return this.chars[0] + this.chars[1] + this.chars[2] + this.chars[3];
        }
    }

    public static class FakeString2 extends FakeString {
        public FakeString2(char[] chars) {
            super(chars);
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other);
        }

        @Override
        public int hashCode() {
            // sum all the chars
            int out = 0;
            for (char c : this.chars) {
                out += c;
            }
            return out;
        }
    }

    public static class FakeString3 extends FakeString {
        public FakeString3(char[] chars) {
            super(chars);
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other);
        }

        @Override
        public int hashCode() {
            // Note: this is basically what Java's `List.hashCode` implementation does.
            // See https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/List.html#hashCode()
            int out = 1;
            for (char c : this.chars) {
                out = out * 31 + c;
            }
            return out;
        }
    }

    public static class ComparableFakeString extends FakeString implements Comparable<ComparableFakeString> {
        public ComparableFakeString(char[] chars) {
            super(chars);
        }

        public int compareTo(ComparableFakeString other) {
            // include a `compareTo` implementation for the AVL tree
            for (int i = 0; i < Math.min(this.chars.length, other.chars.length); i++) {
                int difference = this.chars[i] - other.chars[i];
                if (difference != 0) {
                    return difference;
                }
            }
            return this.chars.length - other.chars.length;
        }
    }
}
