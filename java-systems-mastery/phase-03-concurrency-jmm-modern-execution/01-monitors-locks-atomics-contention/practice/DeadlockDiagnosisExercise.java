public class DeadlockDiagnosisExercise {
    private static final Object A = new Object();
    private static final Object B = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (A) {
                sleepQuietly();
                synchronized (B) {
                    System.out.println("t1 done");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (B) {
                sleepQuietly();
                synchronized (A) {
                    System.out.println("t2 done");
                }
            }
        });

        t1.start();
        t2.start();
        t1.join(300);
        t2.join(300);
        System.out.println("Exercise: detect and explain the deadlock condition.");
    }

    private static void sleepQuietly() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
