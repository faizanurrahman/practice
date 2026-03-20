/**
 * Practice exercise: reproduce (deterministically) the initialization-order pitfall
 * of calling an overridable method from a constructor.
 *
 * The subclass field is set AFTER the superclass constructor finishes.
 * If the superclass calls an overridable method during construction, the override
 * can observe default values for subclass fields.
 */
public class ConstructorOverridableLeakPitfall {
    static class Parent {
        Parent() {
            // Calling an overridable method during construction is a correctness bug.
            onInit();
        }

        void onInit() {
            // default no-op
        }
    }

    static class Child extends Parent {
        private final String msg = "child-msg";

        @Override
        void onInit() {
            // When this method runs, 'msg' is still the default value.
            // For a final field, that default might be null depending on when the JVM makes
            // it visible, but in this construction pattern the overridden method is still
            // invoked before 'msg' is assigned in the subclass constructor/field init.
            if (msg != null) {
                throw new IllegalStateException("Expected msg to be null during constructor leak");
            }
        }
    }

    public static void main(String[] args) {
        new Child();
        System.out.println("OK: constructor init leak reproduced");
    }
}

