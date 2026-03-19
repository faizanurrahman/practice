/**
 * Bridge method demonstration (type erasure + polymorphism preservation).
 *
 * Compile and inspect:
 *   javac BridgeMethodExample.java
 *   javap -c -v BridgeMethodExample$StringNode
 *
 * You should see a synthetic "bridge" method in StringNode that delegates to the
 * String-specific override and performs casts.
 */
public class BridgeMethodExample {
    static class Node<T> {
        private T data;

        public void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    static class StringNode extends Node<String> {
        @Override
        public void setData(String data) {
            super.setData(data);
        }
    }

    public static void main(String[] args) {
        Node<String> n = new StringNode();
        n.setData("hello");
        assert "hello".equals(n.getData()) : "Expected StringNode to behave correctly through Node<String>";

        System.out.println("OK: Bridge/polymorphism demo passed");
    }
}

