import java.util.concurrent.CountDownLatch;

/**
 * Concept: safe publication of an immutable snapshot.
 *
 * Demonstrates a typical senior pattern:
 * - immutable object with final fields
 * - published via a volatile reference (establishes happens-before)
 *
 * Run:
 *   javac SafePublicationFinalFields.java
 *   java -ea SafePublicationFinalFields
 */
public class SafePublicationFinalFields {
    static final class Snapshot {
        final int a;
        final int b;

        Snapshot(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    static volatile Snapshot SNAP;

    public static void main(String[] args) throws InterruptedException {
        int expectedA = 123;
        int expectedB = 456;

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch readerDone = new CountDownLatch(1);

        Thread writer = new Thread(() -> {
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SNAP = new Snapshot(expectedA, expectedB); // volatile write
        }, "writer");

        Thread reader = new Thread(() -> {
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Spin until published; volatile read provides visibility.
            Snapshot s;
            do {
                s = SNAP;
            } while (s == null);

            assert s.a == expectedA : "Expected a=" + expectedA + " but got " + s.a;
            assert s.b == expectedB : "Expected b=" + expectedB + " but got " + s.b;
            readerDone.countDown();
        }, "reader");

        writer.start();
        reader.start();

        start.countDown();
        readerDone.await();

        System.out.println("OK: SafePublicationFinalFields passed");
    }
}

