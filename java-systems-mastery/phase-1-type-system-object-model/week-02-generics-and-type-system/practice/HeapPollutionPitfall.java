/**
 * Practice: trigger a deterministic ClassCastException via heap pollution.
 *
 * Run:
 *   javac HeapPollutionPitfall.java
 *   java HeapPollutionPitfall
 *
 * This is intentionally unsafe code to demonstrate why unchecked/raw operations
 * can break runtime type safety.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HeapPollutionPitfall {
    public static void main(String[] args) {
        // Raw type bypasses generic checks.
        java.util.List raw = new java.util.ArrayList();
        raw.add(123); // insert Integer into what we will pretend is List<String>

        java.util.List<String> strings;
        // Unchecked cast: compiler warns, runtime cast happens at use site.
        strings = raw;

        try {
            String s = strings.get(0);
            // If we get here, the cast didn't fail (unexpected for the demo).
            throw new IllegalStateException("Expected ClassCastException, but got: " + s);
        } catch (ClassCastException expected) {
            System.out.println("OK: ClassCastException triggered as expected: " + expected);
        }
    }
}

