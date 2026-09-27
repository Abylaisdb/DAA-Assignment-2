public class Metrics {

    public long comparisons = 0;
    public long accesses = 0;
    public long moves = 0;
    public long swaps = 0;

    public void reset() {
        comparisons = 0;
        accesses = 0;
        moves = 0;
        swaps = 0;
    }

    public long total() {
        return comparisons + accesses + moves + swaps;
    }

    @Override
    public String toString() {
        return String.format("comparisons=%d accesses=%d moves=%d swaps=%d",
                comparisons, accesses, moves, swaps);
    }
}