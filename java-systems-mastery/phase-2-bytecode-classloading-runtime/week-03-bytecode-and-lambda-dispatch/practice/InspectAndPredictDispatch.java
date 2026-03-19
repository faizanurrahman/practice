import java.util.function.IntUnaryOperator;

/**
 * Practice: predict which invocation form is used and validate behavior.
 *
 * This is not a "javap autograder". Instead, it gives you a deterministic
 * behavior check plus bytecode inspection instructions.
 *
 * Steps:
 *   1) javac InspectAndPredictDispatch.java
 *   2) javap -c -v InspectAndPredictDispatch | rg -n "invoke|new|invokedynamic|checkcast"
 *
 * What to look for:
 * - virtual calls: invokevirtual (method dispatch via receiver type)
 * - interface/lambda calls: invokedynamic / invokeinterface depending on shapes
 * - allocation sites: new (where objects are actually created)
 */
public class InspectAndPredictDispatch {
    static class Animal {
        int speakBase() {
            return 1;
        }

        int speak() {
            return speakBase(); // invokevirtual in bytecode (virtual dispatch on Animal)
        }
    }

    static final class Dog extends Animal {
        @Override
        int speakBase() {
            return 42;
        }
    }

    static int useVirtualDispatch(Animal a) {
        return a.speak(); // invokevirtual call site; JIT may inline if stable
    }

    static int useLambda(IntUnaryOperator op) {
        // The call through the functional interface is an interface invocation path.
        return op.applyAsInt(10);
    }

    public static void main(String[] args) {
        int v = useVirtualDispatch(new Dog());
        assert v == 42 : "Expected overridden speakBase() to drive speak() result";

        // Non-capturing lambda should be representable efficiently.
        int lambdaR = useLambda(x -> x + 7);
        assert lambdaR == 17 : "Expected lambda 10 + 7 = 17";

        System.out.println("OK: InspectAndPredictDispatch behavioral checks passed");
    }
}

