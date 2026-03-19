/**
 * Example: shape the allocation/escape behavior to help you reason about
 * escape analysis and allocation hot spots.
 *
 * Run:
 *   javac AllocationAndEscapeSmokeTest.java
 *   java -ea AllocationAndEscapeSmokeTest
 *
 * Notes:
 * - `noEscapeSum` creates temporary objects that never escape the method.
 * - `escapeObject` returns an object, forcing escape.
 *
 * You can inspect bytecode with javap and compare allocation patterns
 * when you run with profilers.
 */
public class AllocationAndEscapeSmokeTest {
    static final class Pair {
        final int a;
        final int b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    static int noEscapeSum(int n) {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            Pair p = new Pair(i, i + 1);
            sum += p.a + p.b;
        }
        return sum;
    }

    static Pair escapeObject(int n) {
        return new Pair(n, n + 1);
    }

    public static void main(String[] args) {
        int n = 1000;
        int sum = noEscapeSum(n);
        // Sum of (i + (i+1)) for i in [0..n-1] = sum(2i+1) = n*(n-1) + n = n*n
        assert sum == n * n : "Expected sum to equal n*n";

        Pair p = escapeObject(10);
        assert p.a == 10 && p.b == 11;

        System.out.println("OK: AllocationAndEscapeSmokeTest passed");
    }
}

