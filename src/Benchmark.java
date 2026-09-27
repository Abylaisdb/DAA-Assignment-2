import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Random;

public class Benchmark {

    static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    static final int REPEATS = 5;
    static final long SEED = 42L;

    public static void main(String[] args) throws IOException {
        String outDir = args.length > 0 ? args[0] : "results/tables";
        new java.io.File(outDir).mkdirs();

        System.out.println("Running Workload 1: Random Access ...");
        runWorkload1RandomAccess(outDir + "/workload1_random_access.csv");

        System.out.println("Running Workload 2: Search ...");
        runWorkload2Search(outDir + "/workload2_search.csv");

        System.out.println("Running Workload 3: Insertion and Removal ...");
        runWorkload3InsertRemove(outDir + "/workload3_insert_remove.csv");

        System.out.println("Running Workload 4: Priority Processing (Min-Heap) ...");
        runWorkload4Heap(outDir + "/workload4_heap.csv");

        System.out.println("Done. CSV files written to " + outDir);
    }

    private static int[] randomInts(int n, Random rnd, int bound) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = rnd.nextInt(bound);
        return arr;
    }

    private static DynamicArray buildArray(int[] initial) {
        DynamicArray a = new DynamicArray(Math.max(8, initial.length));
        for (int v : initial) a.add(v);
        a.metrics.reset();
        return a;
    }

    private static MyLinkedList buildList(int[] initial) {
        MyLinkedList l = new MyLinkedList();
        for (int v : initial) l.add(v);
        l.metrics.reset();
        return l;
    }

    private static void runWorkload1RandomAccess(String path) throws IOException {
        try (PrintWriter w = csv(path, "structure,n,avg_time_ns,total_accesses,num_queries")) {
            for (int n : SIZES) {
                Random dataRnd = new Random(SEED);
                int[] initial = randomInts(n, dataRnd, 1_000_000);
                Random queryRnd = new Random(SEED + 1);
                int[] indices = new int[10_000];
                for (int i = 0; i < indices.length; i++) indices[i] = queryRnd.nextInt(n);

                DynamicArray arr = buildArray(initial);
                long totalNs = 0;
                for (int r = 0; r < REPEATS; r++) {
                    arr.metrics.reset();
                    long start = System.nanoTime();
                    for (int idx : indices) arr.get(idx);
                    totalNs += System.nanoTime() - start;
                }
                w.printf(Locale.US, "Array,%d,%.1f,%d,%d%n", n, totalNs / (double) REPEATS,
                        arr.metrics.accesses, indices.length);

                MyLinkedList list = buildList(initial);
                totalNs = 0;
                for (int r = 0; r < REPEATS; r++) {
                    list.metrics.reset();
                    long start = System.nanoTime();
                    for (int idx : indices) list.get(idx);
                    totalNs += System.nanoTime() - start;
                }
                w.printf(Locale.US, "List,%d,%.1f,%d,%d%n", n, totalNs / (double) REPEATS,
                        list.metrics.accesses + list.metrics.moves, indices.length);

                System.out.println("  n=" + n + " done");
            }
        }
    }

    private static void runWorkload2Search(String path) throws IOException {
        try (PrintWriter w = csv(path, "structure,n,avg_time_ns,total_comparisons,num_queries")) {
            for (int n : SIZES) {
                Random dataRnd = new Random(SEED);
                int[] initial = randomInts(n, dataRnd, 1_000_000);
                Random queryRnd = new Random(SEED + 2);
                int[] queries = new int[1_000];
                for (int i = 0; i < queries.length; i++) {
                    if (i % 2 == 0) {
                        queries[i] = initial[queryRnd.nextInt(n)];
                    } else {
                        queries[i] = queryRnd.nextInt(1_000_000);
                    }
                }

                DynamicArray arr = buildArray(initial);
                long totalNs = 0;
                for (int r = 0; r < REPEATS; r++) {
                    arr.metrics.reset();
                    long start = System.nanoTime();
                    for (int q : queries) arr.contains(q);
                    totalNs += System.nanoTime() - start;
                }
                w.printf(Locale.US, "Array,%d,%.1f,%d,%d%n", n, totalNs / (double) REPEATS,
                        arr.metrics.comparisons, queries.length);

                MyLinkedList list = buildList(initial);
                totalNs = 0;
                for (int r = 0; r < REPEATS; r++) {
                    list.metrics.reset();
                    long start = System.nanoTime();
                    for (int q : queries) list.contains(q);
                    totalNs += System.nanoTime() - start;
                }
                w.printf(Locale.US, "List,%d,%.1f,%d,%d%n", n, totalNs / (double) REPEATS,
                        list.metrics.comparisons, queries.length);

                System.out.println("  n=" + n + " done");
            }
        }
    }

    private static void runWorkload3InsertRemove(String path) throws IOException {
        try (PrintWriter w = csv(path,
                "structure,n,operation,position,avg_time_ns,total_moves,num_ops")) {
            for (int n : SIZES) {
                Random dataRnd = new Random(SEED);
                int[] initial = randomInts(n, dataRnd, 1_000_000);
                Random valRnd = new Random(SEED + 3);
                int[] insertValues = randomInts(1_000, valRnd, 1_000_000);

                for (String structure : new String[]{"Array", "List"}) {
                    for (String position : new String[]{"front", "middle"}) {
                        int insertIndex = position.equals("front") ? 0 : n / 2;

                        long insTotalNs = 0;
                        long insMoves = 0;
                        for (int r = 0; r < REPEATS; r++) {
                            if (structure.equals("Array")) {
                                DynamicArray a = buildArray(initial);
                                int idx = insertIndex;
                                long start = System.nanoTime();
                                for (int v : insertValues) {
                                    a.add(Math.min(idx, a.size()), v);
                                }
                                insTotalNs += System.nanoTime() - start;
                                insMoves = a.metrics.moves;
                            } else {
                                MyLinkedList l = buildList(initial);
                                int idx = insertIndex;
                                long start = System.nanoTime();
                                for (int v : insertValues) {
                                    l.add(Math.min(idx, l.size()), v);
                                }
                                insTotalNs += System.nanoTime() - start;
                                insMoves = l.metrics.moves;
                            }
                        }
                        w.printf(Locale.US, "%s,%d,insert,%s,%.1f,%d,%d%n", structure, n, position,
                                insTotalNs / (double) REPEATS, insMoves, insertValues.length);

                        long remTotalNs = 0;
                        long remMoves = 0;
                        int removals = Math.min(1_000, n);
                        for (int r = 0; r < REPEATS; r++) {
                            if (structure.equals("Array")) {
                                DynamicArray a = buildArray(initial);
                                int idx = insertIndex;
                                long start = System.nanoTime();
                                for (int i = 0; i < removals; i++) {
                                    int removeAt = Math.min(idx, a.size() - 1);
                                    a.remove(removeAt);
                                }
                                remTotalNs += System.nanoTime() - start;
                                remMoves = a.metrics.moves;
                            } else {
                                MyLinkedList l = buildList(initial);
                                int idx = insertIndex;
                                long start = System.nanoTime();
                                for (int i = 0; i < removals; i++) {
                                    int removeAt = Math.min(idx, l.size() - 1);
                                    l.remove(removeAt);
                                }
                                remTotalNs += System.nanoTime() - start;
                                remMoves = l.metrics.moves;
                            }
                        }
                        w.printf(Locale.US, "%s,%d,remove,%s,%.1f,%d,%d%n", structure, n, position,
                                remTotalNs / (double) REPEATS, remMoves, removals);
                    }
                }
                System.out.println("  n=" + n + " done");
            }
        }
    }

    private static void runWorkload4Heap(String path) throws IOException {
        try (PrintWriter w = csv(path, "n,phase,avg_time_ns,total_comparisons,non_decreasing_verified")) {
            for (int n : SIZES) {
                Random dataRnd = new Random(SEED);
                int[] values = randomInts(n, dataRnd, 1_000_000);

                long insertTotalNs = 0;
                long insertComparisons = 0;
                long extractTotalNs = 0;
                long extractComparisons = 0;
                boolean nonDecreasing = true;

                for (int r = 0; r < REPEATS; r++) {
                    MinHeap heap = new MinHeap(Math.max(16, n));
                    long start = System.nanoTime();
                    for (int v : values) heap.insert(v);
                    insertTotalNs += System.nanoTime() - start;
                    insertComparisons = heap.metrics.comparisons;

                    heap.metrics.reset();
                    int prev = Integer.MIN_VALUE;
                    boolean ok = true;
                    start = System.nanoTime();
                    for (int i = 0; i < n; i++) {
                        int m = heap.extractMin();
                        if (m < prev) ok = false;
                        prev = m;
                    }
                    extractTotalNs += System.nanoTime() - start;
                    extractComparisons = heap.metrics.comparisons;
                    if (!ok) nonDecreasing = false;
                }

                w.printf(Locale.US, "%d,insert,%.1f,%d,%s%n", n, insertTotalNs / (double) REPEATS,
                        insertComparisons, "n/a");
                w.printf(Locale.US, "%d,extractMin,%.1f,%d,%s%n", n, extractTotalNs / (double) REPEATS,
                        extractComparisons, nonDecreasing);

                System.out.println("  n=" + n + " done");
            }
        }
    }

    private static PrintWriter csv(String path, String header) throws IOException {
        PrintWriter w = new PrintWriter(new FileWriter(path));
        w.println(header);
        return w;
    }
}