import java.util.ArrayList;
import java.util.List;

/**
 * Exercise: this code mutates shared state from a stream pipeline.
 * 1) Predict output.
 * 2) Explain why stream "functional style" did not save you from bugs.
 * 3) Rewrite with a clear for-loop or a proper collector.
 */
public class StreamDebuggingExercise {

    public static void main(String[] args) {
        List<Integer> acc = new ArrayList<>();
        List<String> items = List.of("a", "bb", "ccc");

        items.stream()
            .map(String::length)
            .forEach(acc::add); // side effect + shared mutation

        System.out.println("acc size: " + acc.size());
        System.out.println("OK for sequential stream — but fragile if parallelized or refactored.");
    }
}
