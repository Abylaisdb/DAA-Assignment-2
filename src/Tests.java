import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Tests {

    static int passed = 0;

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAILED: " + message);
        }
        passed++;
    }

    public static void main(String[] args) {
        testDynamicArrayEmptyAndSingle();
        testDynamicArrayMultipleAndDuplicates();
        testDynamicArrayBoundaryAndInvalidIndices();
        testDynamicArrayLarge();

        testLinkedListEmptyAndSingle();
        testLinkedListMultipleAndDuplicates();
        testLinkedListBoundaryAndInvalidIndices();
        testLinkedListLarge();

        testMinHeapEmptyAndSingle();
        testMinHeapMultipleAndDuplicates();
        testMinHeapPropertyAfterOps();
        testMinHeapLargeAgainstPriorityQueue();

        crossCheckAgainstJavaCollections();

        System.out.println("All " + passed + " checks passed.");
    }

    static void testDynamicArrayEmptyAndSingle() {
        DynamicArray a = new DynamicArray();
        check(a.isEmpty(), "new DynamicArray should be empty");
        check(a.size() == 0, "size should be 0");
        check(!a.contains(5), "empty array should not contain anything");

        a.add(42);
        check(a.size() == 1, "size should be 1 after one add");
        check(a.get(0) == 42, "get(0) should return the single element");
        check(a.contains(42), "contains should find the single element");
        check(!a.contains(1), "contains should not find missing element");
    }

    static void testDynamicArrayMultipleAndDuplicates() {
        DynamicArray a = new DynamicArray();
        int[] values = {5, 3, 5, 8, 3, 3, 9};
        for (int v : values) a.add(v);
        check(a.size() == values.length, "size should match number of adds");
        for (int i = 0; i < values.length; i++) {
            check(a.get(i) == values[i], "get(" + i + ") should match inserted order");
        }
        check(a.contains(3), "should find duplicate value 3");
        int removed = a.remove(2);
        check(removed == 5, "removed value should be the duplicate 5");
        check(a.get(2) == 8, "elements after removal index should shift left");
    }

    static void testDynamicArrayBoundaryAndInvalidIndices() {
        DynamicArray a = new DynamicArray();
        for (int i = 0; i < 5; i++) a.add(i);
        a.add(0, -1);
        check(a.get(0) == -1, "insert at index 0 should place at front");
        a.add(a.size(), 100);
        check(a.get(a.size() - 1) == 100, "insert at index==size should append");

        boolean threw = false;
        try { a.get(-1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check(threw, "get(-1) should throw");

        threw = false;
        try { a.get(a.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check(threw, "get(size) should throw");

        threw = false;
        try { a.add(-1, 0); } catch (IndexOutOfBoundsException e) { threw = true; }
        check(threw, "add(-1, x) should throw");

        threw = false;
        try { a.remove(a.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check(threw, "remove(size) should throw");
    }

    static void testDynamicArrayLarge() {
        DynamicArray a = new DynamicArray();
        int n = 50_000;
        for (int i = 0; i < n; i++) a.add(i);
        check(a.size() == n, "large array should hold n elements");
        check(a.get(n - 1) == n - 1, "last element should be correct after many appends");
        check(a.contains(n / 2), "large array should find a middle value");
    }

    static void testLinkedListEmptyAndSingle() {
        MyLinkedList l = new MyLinkedList();
        check(l.isEmpty(), "new list should be empty");
        check(!l.contains(1), "empty list should not contain anything");

        l.add(7);
        check(l.size() == 1, "size should be 1");
        check(l.get(0) == 7, "get(0) should return single element");
    }

    static void testLinkedListMultipleAndDuplicates() {
        MyLinkedList l = new MyLinkedList();
        int[] values = {1, 2, 2, 3, 2};
        for (int v : values) l.add(v);
        for (int i = 0; i < values.length; i++) {
            check(l.get(i) == values[i], "get(" + i + ") should match insertion order");
        }
        check(l.contains(2), "should find duplicate value 2");
        int removed = l.remove(1);
        check(removed == 2, "removed element should be first duplicate 2");
        check(l.get(1) == 2, "shifted element should be the next duplicate 2");
    }

    static void testLinkedListBoundaryAndInvalidIndices() {
        MyLinkedList l = new MyLinkedList();
        for (int i = 0; i < 5; i++) l.add(i);
        l.add(0, -1);
        check(l.get(0) == -1, "insert at index 0 should place at head");
        l.add(l.size(), 100);
        check(l.get(l.size() - 1) == 100, "insert at index==size should append at tail");

        boolean threw = false;
        try { l.get(-1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check(threw, "get(-1) should throw");

        threw = false;
        try { l.get(l.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check(threw, "get(size) should throw");

        threw = false;
        try { l.remove(l.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check(threw, "remove(size) should throw");
    }

    static void testLinkedListLarge() {
        MyLinkedList l = new MyLinkedList();
        int n = 50_000;
        for (int i = 0; i < n; i++) l.add(i);
        check(l.size() == n, "large list should hold n elements");
        check(l.get(n - 1) == n - 1, "last element should be correct after many appends");
        check(l.contains(n / 2), "large list should find a middle value");
    }

    static void testMinHeapEmptyAndSingle() {
        MinHeap h = new MinHeap();
        check(h.isEmpty(), "new heap should be empty");
        boolean threw = false;
        try { h.peekMin(); } catch (java.util.NoSuchElementException e) { threw = true; }
        check(threw, "peekMin on empty heap should throw");

        h.insert(10);
        check(h.peekMin() == 10, "single-element heap min should be that element");
        check(h.isValidHeap(), "heap property should hold with one element");
        int min = h.extractMin();
        check(min == 10, "extractMin should return the only element");
        check(h.isEmpty(), "heap should be empty after extracting the only element");
    }

    static void testMinHeapMultipleAndDuplicates() {
        MinHeap h = new MinHeap();
        int[] values = {5, 3, 8, 3, 1, 9, 1};
        for (int v : values) h.insert(v);
        check(h.isValidHeap(), "heap property should hold after inserts with duplicates");

        int[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        for (int expected : sorted) {
            int got = h.extractMin();
            check(got == expected, "extractMin order should be non-decreasing, expected " + expected + " got " + got);
            check(h.isValidHeap(), "heap property should hold after each extraction");
        }
    }

    static void testMinHeapPropertyAfterOps() {
        Random rnd = new Random(42);
        MinHeap h = new MinHeap();
        for (int i = 0; i < 1000; i++) {
            h.insert(rnd.nextInt(10000));
            check(h.isValidHeap(), "heap property should hold after every insertion");
        }
        int prev = Integer.MIN_VALUE;
        while (!h.isEmpty()) {
            int cur = h.extractMin();
            check(cur >= prev, "extractMin sequence should be non-decreasing");
            check(h.isValidHeap(), "heap property should hold after every extraction");
            prev = cur;
        }
    }

    static void testMinHeapLargeAgainstPriorityQueue() {
        Random rnd = new Random(42);
        int n = 20_000;
        MinHeap h = new MinHeap();
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int i = 0; i < n; i++) {
            int v = rnd.nextInt(1_000_000);
            h.insert(v);
            pq.add(v);
        }
        while (!pq.isEmpty()) {
            check(h.extractMin() == pq.poll(), "MinHeap and PriorityQueue should agree on extraction order");
        }
        check(h.isEmpty(), "MinHeap should be empty when PriorityQueue is empty");
    }

    static void crossCheckAgainstJavaCollections() {
        Random rnd = new Random(42);
        DynamicArray a = new DynamicArray();
        MyLinkedList l = new MyLinkedList();
        List<Integer> reference = new ArrayList<>();

        for (int op = 0; op < 5000; op++) {
            int choice = rnd.nextInt(4);
            if (choice == 0 || reference.isEmpty()) {
                int v = rnd.nextInt(100000);
                int idx = reference.isEmpty() ? 0 : rnd.nextInt(reference.size() + 1);
                a.add(idx, v);
                l.add(idx, v);
                reference.add(idx, v);
            } else if (choice == 1) {
                int idx = rnd.nextInt(reference.size());
                int ra = a.remove(idx);
                int rl = l.remove(idx);
                int rr = reference.remove(idx);
                check(ra == rr && rl == rr, "remove(idx) should agree with reference list");
            } else if (choice == 2) {
                int idx = rnd.nextInt(reference.size());
                check(a.get(idx) == reference.get(idx), "DynamicArray.get should agree with reference");
                check(l.get(idx) == reference.get(idx), "MyLinkedList.get should agree with reference");
            } else {
                int v = reference.get(rnd.nextInt(reference.size()));
                check(a.contains(v), "DynamicArray.contains should find a value known to be present");
                check(l.contains(v), "MyLinkedList.contains should find a value known to be present");
            }
        }
        check(a.size() == reference.size(), "final DynamicArray size should match reference");
        check(l.size() == reference.size(), "final MyLinkedList size should match reference");
        for (int i = 0; i < reference.size(); i++) {
            check(a.get(i) == reference.get(i), "final DynamicArray contents should match reference at " + i);
            check(l.get(i) == reference.get(i), "final MyLinkedList contents should match reference at " + i);
        }
    }
}