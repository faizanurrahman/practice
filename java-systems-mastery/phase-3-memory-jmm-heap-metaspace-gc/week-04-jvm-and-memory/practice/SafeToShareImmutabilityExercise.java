import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Practice: "Is this safe to share?"
 *
 * The goal is to test the immutability/defensive-copy discipline that underpins
 * both correctness and concurrency safety.
 *
 * Run:
 *   javac SafeToShareImmutabilityExercise.java
 *   java -ea SafeToShareImmutabilityExercise
 */
public class SafeToShareImmutabilityExercise {
    static final class UnsafeSharedSnapshot {
        private final List<Integer> backing; // aliasing bug

        UnsafeSharedSnapshot(List<Integer> backing) {
            this.backing = backing; // no defensive copy
        }

        List<Integer> values() {
            return backing; // exposes mutable list
        }
    }

    static final class SafeSharedSnapshot {
        private final List<Integer> backing; // defensive copy + unmodifiable view

        SafeSharedSnapshot(List<Integer> backing) {
            this.backing = Collections.unmodifiableList(new ArrayList<>(backing));
        }

        List<Integer> values() {
            return backing;
        }
    }

    public static void main(String[] args) {
        List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);

        UnsafeSharedSnapshot unsafe = new UnsafeSharedSnapshot(input);
        SafeSharedSnapshot safe = new SafeSharedSnapshot(input);

        // Mutate the original after construction.
        input.set(0, 999);

        assert unsafe.values().get(0) == 999 : "Unsafe snapshot must reflect aliasing mutation";
        assert safe.values().get(0) == 1 : "Safe snapshot must not reflect aliasing mutation";

        System.out.println("OK: SafeToShareImmutabilityExercise passed");
    }
}

