import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolOverloadExercise {
    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.AbortPolicy()
        );

        pool.execute(() -> sleep(200));
        pool.execute(() -> sleep(200));
        try {
            pool.execute(() -> System.out.println("rejected"));
            throw new IllegalStateException("Expected rejection");
        } catch (RuntimeException expected) {
            System.out.println("Exercise: explain queue saturation and rejection policy.");
        } finally {
            pool.shutdownNow();
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
