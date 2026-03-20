public class RetryClassificationExercise {
    static boolean shouldRetry(int statusCode) {
        return statusCode == 408 || statusCode == 429 || statusCode >= 500;
    }

    public static void main(String[] args) {
        System.out.println("Retry 503? " + shouldRetry(503));
        System.out.println("Retry 400? " + shouldRetry(400));
        System.out.println("Exercise: add idempotency-key gate before retrying writes.");
    }
}
