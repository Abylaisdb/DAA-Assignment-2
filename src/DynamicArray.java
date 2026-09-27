import java.util.NoSuchElementException;

public class DynamicArray {

    private int[] data;
    private int size;
    public final Metrics metrics = new Metrics();

    public DynamicArray() {
        this(8);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        data = new int[initialCapacity];
        size = 0;
    }

    public int size() {
        return size;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) return;
        int newCapacity = data.length;
        while (newCapacity < minCapacity) newCapacity *= 2;
        int[] newData = new int[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
            metrics.moves++;
        }
        data = newData;
    }

    public void add(int x) {
        ensureCapacity(size + 1);
        data[size] = x;
        metrics.accesses++;
        size++;
    }

    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }
        ensureCapacity(size + 1);

        for (int i = size - 1; i >= index; i--) {
            data[i + 1] = data[i];
            metrics.moves++;
            metrics.accesses += 2;
        }

        data[index] = x;
        metrics.accesses++;
        size++;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }

        int removed = data[index];
        metrics.accesses++;

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            metrics.moves++;
            metrics.accesses += 2;
        }

        size--;
        return removed;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }

        metrics.accesses++;
        return data[index];
    }

    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.comparisons++;
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    public int peekFirstOrThrow() {
        if (size == 0) throw new NoSuchElementException("empty");
        return data[0];
    }
}