import java.util.function.Function;

/**
 * Example for Week 3: lambda + method reference + a captured lambda.
 *
 * Compile:
 *   javac LambdaInvokeDynamicExample.java
 *
 * Inspect:
 *   javap -c -v LambdaInvokeDynamicExample | rg invokedynamic
 *
 * You should see invokedynamic call sites related to lambda linkage.
 */
public class LambdaInvokeDynamicExample {
    static int add1(int x) {
        return x + 1;
    }

    static int applyTwice(int x, Function<Integer, Integer> f) {
        return f.apply(f.apply(x));
    }

    public static void main(String[] args) {
        // Non-capturing lambda becomes simpler and can be optimized/reused.
        Function<Integer, Integer> f1 = (Integer x) -> x + 1;

        // Method reference typically compiles to invokedynamic as well.
        Function<Integer, Integer> f2 = LambdaInvokeDynamicExample::add1;

        // Capturing lambda: captures base, typically forces a different lambda instance per base.
        int base = 10;
        Function<Integer, Integer> f3 = (Integer x) -> base + x;

        int r1 = applyTwice(1, f1);
        int r2 = applyTwice(1, f2);
        int r3 = applyTwice(1, f3);

        assert r1 == 3 : "Expected (1+1)+1 = 3";
        assert r2 == 3 : "Expected (1+1)+1 = 3";
        assert r3 == (base + (base + 1)) : "Expected base + (base + x)";

        System.out.println("OK: lambda invoke/dynamic demo passed");
    }
}

