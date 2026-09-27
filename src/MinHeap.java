import java.util.NoSuchElementException;

public class MinHeap {

    private int[] data;
    private int size;
    public final Metrics metrics = new Metrics();

    public MinHeap() {
        this(16);
    }

    public MinHeap(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        data = new int[initialCapacity];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) return;
        int newCapacity = data.length;
        while (newCapacity < minCapacity) {
            newCapacity *= 2;
        }
        int[] newData = new int[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private void swap(int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;

        metrics.swaps++;
        metrics.accesses += 4;
    }

    public void insert(int x) {
        ensureCapacity(size + 1);
        int i = size;
        data[i] = x;
        metrics.accesses++;
        size++;
        while (i > 0) {
            int p = parent(i);
            metrics.comparisons++;

            if (data[p] <= data[i]) {
                break;
            }
            swap(i, p);
            i = p;
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new NoSuchElementException("empty heap");
        }
        metrics.accesses++;
        return data[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new NoSuchElementException("empty heap");
        }
        int min = data[0];
        metrics.accesses++;

        size--;

        data[0] = data[size];
        metrics.accesses++;
        int i = 0;
        while (true) {
            int l = left(i);
            int r = right(i);
            int smallest = i;
            if (l < size) {
                metrics.comparisons++;
                if (data[l] < data[smallest]) {
                    smallest = l;
                }
            }
            if (r < size) {
                metrics.comparisons++;
                if (data[r] < data[smallest]) {
                    smallest = r;
                }
            }
            if (smallest == i) {
                break;
            }
            swap(i, smallest);
            i = smallest;
        }
        return min;
    }

    public boolean isValidHeap() {
        for (int i = 1; i < size; i++) {
            if (data[parent(i)] > data[i]) {
                return false;
            }
        }

        return true;
    }
}