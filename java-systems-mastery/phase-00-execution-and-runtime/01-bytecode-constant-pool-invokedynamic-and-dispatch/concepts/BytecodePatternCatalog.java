/**
 * Concept: a small bytecode "pattern catalog" you can inspect with javap.
 *
 * Compile and inspect:
 *   javac BytecodePatternCatalog.java
 *   javap -c -v BytecodePatternCatalog
 *
 * The class contains methods that map to common bytecode sequences:
 * - new/dup/invokespecial (object construction)
 * - getfield/putfield (instance fields)
 * - invokevirtual (virtual dispatch)
 * - invokespecial (constructors/private/super)
 * - checkcast/instanceof (type checks)
 */
public class BytecodePatternCatalog {
    static abstract class Base {
        abstract int value();

        int plus(int x) {
            return value() + x; // invokevirtual on value()
        }
    }

    static final class Impl extends Base {
        private final int v;

        Impl(int v) {
            this.v = v; // putfield
        }

        @Override
        int value() {
            return v; // getfield
        }
    }

    public static int constructAndDispatch(int x) {
        Base b = new Impl(x); // new/dup/invokespecial
        return b.plus(1);     // invokevirtual chain
    }

    public static String castAndCheck(Object o) {
        if (o instanceof String) { // instanceof + conditional
            // checkcast after type check
            return ((String) o).toUpperCase();
        }
        return "NOT_STRING";
    }

    public static void main(String[] args) {
        int r = constructAndDispatch(10);
        assert r == 11 : "Expected 10 + 1";

        assert "HELLO".equals(castAndCheck("hello"));
        assert "NOT_STRING".equals(castAndCheck(123));

        System.out.println("OK: BytecodePatternCatalog executed correctly");
    }
}

