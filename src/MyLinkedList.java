public class MyLinkedList {

    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    public final Metrics metrics = new Metrics();

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(int x) {
        Node node = new Node(x);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
            metrics.moves++;
        }

        size++;
    }

    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }
        if (index == 0) {
            Node node = new Node(x);
            node.next = head;
            head = node;
            if (tail == null) {
                tail = node;
            }

            size++;
            return;
        }

        Node prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
            metrics.moves++;
        }

        Node node = new Node(x);
        node.next = prev.next;
        prev.next = node;
        if (node.next == null) {
            tail = node;
        }
        size++;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }

        int removed;
        if (index == 0) {
            removed = head.value;
            head = head.next;
            if (head == null) {
                tail = null;
            }
        } else {
            Node prev = head;

            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
                metrics.moves++;
            }
            removed = prev.next.value;
            prev.next = prev.next.next;

            if (prev.next == null) {
                tail = prev;
            }
        }
        size--;
        return removed;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + " size=" + size);
        }

        Node cur = head;

        for (int i = 0; i < index; i++) {
            cur = cur.next;
            metrics.moves++;
        }
        metrics.accesses++;
        return cur.value;
    }

    public boolean contains(int x) {
        Node cur = head;

        while (cur != null) {
            metrics.comparisons++;

            if (cur.value == x) {
                return true;
            }
            cur = cur.next;
        }
        return false;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node cur = head;
        while (cur != null) {
            sb.append(cur.value);
            if (cur.next != null) {
                sb.append(", ");
            }
            cur = cur.next;
        }

        return sb.append("]").toString();
    }
}
