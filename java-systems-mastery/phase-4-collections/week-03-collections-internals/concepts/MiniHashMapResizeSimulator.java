/**
 * Concept: a tiny separate-chaining map to make resize logic concrete.
 *
 * This is intentionally not a full HashMap clone.
 * It is designed so you can reason about:
 * - bucket array size
 * - load-factor resize thresholds
 * - collision chains
 *
 * Run:
 *   javac MiniHashMapResizeSimulator.java
 *   java -ea MiniHashMapResizeSimulator
 */
public class MiniHashMapResizeSimulator {
    private static final class Node {
        final int key;
        int value;
        Node next;

        Node(int key, int value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node[] table;
    private int size;
    private final float loadFactor;

    public MiniHashMapResizeSimulator(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be > 0");
        }
        if (!(loadFactor > 0.0f)) {
            throw new IllegalArgumentException("loadFactor must be > 0");
        }
        this.table = new Node[initialCapacity];
        this.loadFactor = loadFactor;
    }

    private int indexFor(int key, int capacity) {
        // Very small hash mixing, just for demonstrative purposes.
        int h = key ^ (key >>> 16);
        return (capacity - 1) & h;
    }

    public void put(int key, int value) {
        if ((size + 1) > table.length * loadFactor) {
            resize(table.length * 2);
        }

        int idx = indexFor(key, table.length);
        Node cur = table[idx];
        while (cur != null) {
            if (cur.key == key) {
                cur.value = value;
                return;
            }
            cur = cur.next;
        }

        table[idx] = new Node(key, value, table[idx]);
        size++;
    }

    public int getOrThrow(int key) {
        int idx = indexFor(key, table.length);
        Node cur = table[idx];
        while (cur != null) {
            if (cur.key == key) {
                return cur.value;
            }
            cur = cur.next;
        }
        throw new IllegalStateException("Key not found: " + key);
    }

    private void resize(int newCapacity) {
        Node[] old = table;
        table = new Node[newCapacity];

        for (Node head : old) {
            Node cur = head;
            while (cur != null) {
                Node next = cur.next;
                int idx = indexFor(cur.key, newCapacity);
                cur.next = table[idx];
                table[idx] = cur;
                cur = next;
            }
        }
    }

    public static void main(String[] args) {
        // Choose a power-of-two capacity so indexFor works with the bitmask.
        MiniHashMapResizeSimulator m = new MiniHashMapResizeSimulator(4, 0.75f);

        // Put enough keys to force multiple resizes.
        m.put(1, 10);
        m.put(2, 20);
        m.put(3, 30);
        m.put(4, 40); // likely triggers a resize
        m.put(5, 50);

        assert m.getOrThrow(1) == 10;
        assert m.getOrThrow(5) == 50;

        System.out.println("OK: MiniHashMapResizeSimulator passed");
    }
}

