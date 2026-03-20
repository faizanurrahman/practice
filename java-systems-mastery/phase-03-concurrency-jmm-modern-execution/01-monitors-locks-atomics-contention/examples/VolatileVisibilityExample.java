public class VolatileVisibilityExample {
    private static volatile boolean stop;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            while (!stop) {
                // spin
            }
            System.out.println("Worker observed stop=true");
        });
        worker.start();
        Thread.sleep(100);
        stop = true;
        worker.join();
    }
}
