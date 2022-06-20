package maps.experiments;

import edu.washington.cse373.experiments.AnalysisUtils;
import edu.washington.cse373.experiments.PlotWindow;
import maps.ChainedHashMap;

import java.util.List;
import java.util.Map;
import java.util.function.LongUnaryOperator;

public class Experiment3HashingResizingLoadFactor {
    public static final long MAX_MAP_SIZE = 50000;
    public static final long STEP = 500;
    public static final int INITIAL_CHAIN_COUNT = 10;
    public static final int CHAIN_INITIAL_CAPACITY = 8;

    public static void main(String[] args) {
        new Experiment3HashingResizingLoadFactor().run();
    }

    public void run() {
        List<Long> sizes = AnalysisUtils.range(0L, MAX_MAP_SIZE, STEP);

        PlotWindow.launch("Experiment 3", "Map Size", "Elapsed Time (ns)",
            new LongUnaryOperator[]{this::runtime1, this::runtime2},
            new String[]{"runtime1", "runtime2"}, sizes, 1, .01);
    }

    protected Map<Long, Long> constructChainedHashMap(double resizingLoadFactor) {
        return new ChainedHashMap<>(resizingLoadFactor, INITIAL_CHAIN_COUNT, CHAIN_INITIAL_CAPACITY);
    }

    public long runtime1(long mapSize) {
        return timePuts(mapSize, constructChainedHashMap(0.75));
    }

    public long runtime2(long mapSize) {
        return timePuts(mapSize, constructChainedHashMap(300));
    }

    protected long timePuts(long numPuts, Map<Long, Long> map) {
        long start = System.nanoTime();
        for (long i = 0L; i < numPuts; i++) {
            map.put(i, 0L);
        }
        return System.nanoTime() - start;
    }
}
