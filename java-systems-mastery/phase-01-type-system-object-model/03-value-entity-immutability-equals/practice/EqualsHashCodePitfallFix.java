import java.util.HashSet;
import java.util.Set;

/**
 * Practice: demonstrate the "mutable key" pitfall for equals/hashCode,
 * then show a fixed immutable value object.
 */
public class EqualsHashCodePitfallFix {
    static class MutableKey {
        private int x;

        MutableKey(int x) {
            this.x = x;
        }

        public void setX(int x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MutableKey)) {
                return false;
            }
            MutableKey other = (MutableKey) o;
            return x == other.x;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(x);
        }
    }

    static final class ImmutableKey {
        private final int x;

        ImmutableKey(int x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ImmutableKey other = (ImmutableKey) o;
            return x == other.x;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(x);
        }
    }

    public static void main(String[] args) {
        // Bad case: using a mutable object as a key.
        MutableKey mk = new MutableKey(1);
        Set<MutableKey> bad = new HashSet<>();
        bad.add(mk);

        mk.setX(2);

        // HashSet can't find the key again because the hash bucket changed.
        if (bad.contains(mk)) {
            throw new IllegalStateException("Expected HashSet to lose track of the mutated key");
        }

        // Fixed case: immutable key works as expected.
        ImmutableKey ik = new ImmutableKey(1);
        Set<ImmutableKey> good = new HashSet<>();
        good.add(ik);

        if (!good.contains(ik)) {
            throw new IllegalStateException("Expected HashSet to find the immutable key");
        }

        System.out.println("OK: mutable-key pitfall reproduced and immutable fix validated");
    }
}

