package maps.experiments;

import edu.washington.cse373.experiments.AnalysisUtils;
import edu.washington.cse373.experiments.PlotWindow;
import maps.AbstractIterableMap;
import maps.ArrayMap;

import java.util.List;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

public class Experiment1ArrayMapRemove {
    /*
    Note: please do not change these constants (or the constants in any of the other experiments)
    while working on your writeup
    */
    public static final long MAX_MAP_SIZE = 15000;
    public static final long STEP = 100;

    public static void main(String[] args) {
        new Experiment1ArrayMapRemove().run();
    }

    public void run() {
        List<Long> sizes = AnalysisUtils.range(0L, MAX_MAP_SIZE, STEP);

        PlotWindow.launch("Experiment 1", "Map Size", "Elapsed Time (ns)",
                new LongUnaryOperator[]{this::runtime1, this::runtime2},
                new String[]{"runtime1", "runtime2"}, sizes, 1000, .05);
    }

    public long runtime1(long mapSize) {
        // don't include the cost of constructing the map when running the tests
        AbstractIterableMap<Long, Long> map = createArrayMapOfSize(mapSize);

        long start = System.nanoTime();

        map.remove(0L);
        for (long i = mapSize - 1; i >= 1; i--) {
            map.remove(i);
        }

        return System.nanoTime() - start;
    }

    public long runtime2(long mapSize) {
        AbstractIterableMap<Long, Long> map = createArrayMapOfSize(mapSize);

        long start = System.nanoTime();

        for (long i = mapSize - 1; i >= 0; i--) {
            map.remove(i);
        }

        return System.nanoTime() - start;
    }

    /**
     * Constructs an array map containing keys from 0 to `size` (with dummy values).
     */
    protected AbstractIterableMap<Long, Long> createArrayMapOfSize(long mapSize) {
        return createMapOfSize(mapSize, ArrayMap::new);
    }

    protected static AbstractIterableMap<Long, Long> createMapOfSize(
        long size,
        Supplier<AbstractIterableMap<Long, Long>> mapConstructor) {
        AbstractIterableMap<Long, Long> map = mapConstructor.get();
        for (long i = 0; i < size; i += 1) {
            map.put(i, -1L);
        }
        return map;
    }
}
