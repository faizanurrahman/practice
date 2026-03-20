public class ClassInitOrderDemo {
    static class Parent {
        static {
            System.out.println("Parent.<clinit>");
        }
    }

    static class Child extends Parent {
        static {
            System.out.println("Child.<clinit>");
        }
    }

    public static void main(String[] args) {
        new Child();
    }
}
