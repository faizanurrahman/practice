/**
 * A tiny "source-to-execution" example for Week 1.
 *
 * Run:
 *   javac ObjectLayoutAndDispatchExample.java
 *   javap -c ObjectLayoutAndDispatchExample
 *
 * Then compare how overridden calls compile (virtual dispatch) versus static calls.
 */
public class ObjectLayoutAndDispatchExample {
    interface Speaker {
        String speak();
    }

    static class Animal implements Speaker {
        @Override
        public String speak() {
            return "???";
        }

        public static String species() {
            return "Animal";
        }
    }

    static final class Dog extends Animal {
        @Override
        public String speak() {
            return "woof";
        }
    }

    public static void main(String[] args) {
        Animal a = new Dog();
        Speaker s = new Dog();

        // Overridden method: virtual dispatch.
        assert a.speak().equals("woof") : "Expected overridden speak()";
        assert s.speak().equals("woof") : "Expected interface dispatch to Dog.speak()";

        // Static method: no receiver polymorphism.
        assert Animal.species().equals("Animal") : "Expected Animal.species()";

        System.out.println("OK: dispatch checks passed");
    }
}

