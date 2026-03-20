import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Concept: a correct PECS utility.
 *
 * It demonstrates:
 * - why `List<? extends T>` is good for reading (producer)
 * - why `List<? super T>` is good for writing (consumer)
 *
 * Run:
 *   javac PeCSCopyUtil.java
 *   java -ea PeCSCopyUtil
 */
public class PeCSCopyUtil {
    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        Objects.requireNonNull(src, "src must not be null");
        Objects.requireNonNull(dest, "dest must not be null");
        for (T item : src) {
            dest.add(item);
        }
    }

    static class NumberBox {
        private final List<Number> list;

        NumberBox(List<Number> list) {
            this.list = list;
        }

        List<Number> list() {
            return list;
        }
    }

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);

        List<Number> numbers = new ArrayList<>();
        copy(ints, numbers);

        assert numbers.size() == 2 : "Expected two copied values";
        assert numbers.get(0).equals(1) : "Expected first item to be 1";

        System.out.println("OK: PECS copy utility passed");
    }
}

