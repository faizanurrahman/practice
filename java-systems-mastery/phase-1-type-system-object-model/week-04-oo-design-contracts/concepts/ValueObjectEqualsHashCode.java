/**
 * Reference implementation: a value object with correct equals/hashCode contracts.
 *
 * Design goals:
 * - final class (prevents equality surprises via inheritance)
 * - final fields + defensive checks
 * - equals compares by concrete type (getClass)
 * - hashCode is computed from exactly the fields used by equals
 */
public final class ValueObjectEqualsHashCode {
    private final String name;
    private final int zip;

    public ValueObjectEqualsHashCode(String name, int zip) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        this.name = name;
        this.zip = zip;
    }

    public String getName() {
        return name;
    }

    public int getZip() {
        return zip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValueObjectEqualsHashCode other = (ValueObjectEqualsHashCode) o;
        return zip == other.zip && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + zip;
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append("{name='")
                .append(name)
                .append('\'')
                .append(", zip=")
                .append(zip)
                .append('}')
                .toString();
    }

    public static void main(String[] args) {
        ValueObjectEqualsHashCode a = new ValueObjectEqualsHashCode("A", 1);
        ValueObjectEqualsHashCode b = new ValueObjectEqualsHashCode("A", 1);
        ValueObjectEqualsHashCode c = new ValueObjectEqualsHashCode("C", 2);

        if (!a.equals(b) || a.hashCode() != b.hashCode()) {
            throw new IllegalStateException("equals/hashCode contract violated for equal values");
        }
        if (a.equals(c)) {
            throw new IllegalStateException("equals returned true for different values");
        }
        System.out.println("OK: ValueObjectEqualsHashCode reference checks passed");
    }
}

