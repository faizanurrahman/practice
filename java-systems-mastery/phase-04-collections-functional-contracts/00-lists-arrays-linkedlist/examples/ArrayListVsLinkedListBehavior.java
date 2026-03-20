import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Example: demonstrate observable behavior differences when inserting/iterating.
 *
 * This is not a benchmark. It is a deterministic, small-size smoke test.
 *
 * Run:
 *   javac ArrayListVsLinkedListBehavior.java
 *   java -ea ArrayListVsLinkedListBehavior
 */
public class ArrayListVsLinkedListBehavior {
    private static void exercise(String name, List<Integer> list) {
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        // Insert near the middle to force traversal work in linked lists.
        list.add(5, 999);

        assert list.get(5) == 999 : name + ": expected inserted value at index 5";
    }

    public static void main(String[] args) {
        exercise("ArrayList", new ArrayList<>());
        exercise("LinkedList", new LinkedList<>());

        System.out.println("OK: ArrayList/LinkedList behavior checks passed");
    }
}

