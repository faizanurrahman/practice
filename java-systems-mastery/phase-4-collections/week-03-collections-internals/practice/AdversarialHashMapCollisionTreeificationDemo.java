import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Practice: a runnable demo that creates adversarial hash collisions.
 *
 * Why: HashMap behaves well on average, but poor hashing patterns can create
 * long collision chains and invoke defensive mechanisms (treeification).
 *
 * Run:
 *   javac AdversarialHashMapCollisionTreeificationDemo.java
 *   java -ea AdversarialHashMapCollisionTreeificationDemo
 */
public class AdversarialHashMapCollisionTreeificationDemo {
    static final class BadKey {
        private final int id;

        BadKey(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            // Force collisions by returning the same hash for all keys.
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BadKey)) return false;
            return id == ((BadKey) o).id;
        }
    }

    private static Object[] extractTable(HashMap<?, ?> map) {
        try {
            Field tableField = HashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(map);
            return table;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to access HashMap internal table", e);
        }
    }

    public static void main(String[] args) {
        HashMap<BadKey, Integer> map = new HashMap<>(64, 0.75f);
        BadKey[] keys = new BadKey[40];

        for (int i = 0; i < keys.length; i++) {
            keys[i] = new BadKey(i);
            map.put(keys[i], i);
        }

        // Verify correctness.
        for (int i = 0; i < keys.length; i++) {
            assert map.get(keys[i]) == i : "Expected value " + i;
        }

        // Best-effort check: whether any bucket has been treeified.
        boolean foundTreeBucket = false;
        try {
            Class<?> treeNodeClass = Class.forName("java.util.HashMap$TreeNode");
            Object[] table = extractTable(map);
            for (Object bucket : table) {
                if (bucket != null && treeNodeClass.isInstance(bucket)) {
                    foundTreeBucket = true;
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            // Different JDK implementations could rename internals; ignore.
        }

        // Not guaranteed across all JDK versions/configs, so we assert only "not crash".
        System.out.println("Collision demo complete. treeified=" + foundTreeBucket);
        System.out.println("OK: AdversarialHashMapCollisionTreeificationDemo passed");
    }
}

