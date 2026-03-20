import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Concept: an immutable value object that demonstrates:
 * - final fields
 * - defensive copies on input
 * - defensive copies on output via unmodifiable views
 *
 * Run:
 *   javac SafePublishedImmutableValueObject.java
 *   java -ea SafePublishedImmutableValueObject
 *
 * (Then optionally inspect bytecode with javap.)
 */
public final class SafePublishedImmutableValueObject {
    private final String name;
    private final List<Integer> values; // stored as immutable copy

    public SafePublishedImmutableValueObject(String name, List<Integer> values) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(values, "values must not be null");
        // Defensive copy to avoid aliasing through the caller's list reference.
        this.values = Collections.unmodifiableList(new ArrayList<>(values));
    }

    public String name() {
        return name;
    }

    public List<Integer> values() {
        // The returned list is unmodifiable, but still safe because we never expose the backing list.
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SafePublishedImmutableValueObject that = (SafePublishedImmutableValueObject) o;
        return name.equals(that.name) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + values.hashCode();
    }

    @Override
    public String toString() {
        return "SafePublishedImmutableValueObject{name='" + name + "', values=" + values + "}";
    }

    public static void main(String[] args) {
        List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);

        SafePublishedImmutableValueObject v = new SafePublishedImmutableValueObject("x", input);

        // Mutate the original list after construction. The object must not change.
        input.set(0, 999);

        assert v.values().get(0) == 1 : "Expected defensive copy to prevent aliasing mutation";

        System.out.println("OK: immutable value object defensive-copy concept passed");
    }
}

