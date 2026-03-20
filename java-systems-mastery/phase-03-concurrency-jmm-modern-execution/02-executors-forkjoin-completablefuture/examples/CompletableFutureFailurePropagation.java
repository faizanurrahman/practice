import java.util.concurrent.CompletableFuture;

public class CompletableFutureFailurePropagation {
    public static void main(String[] args) {
        CompletableFuture<Integer> chain = CompletableFuture.supplyAsync(() -> 42)
            .thenApply(v -> v / 0)
            .exceptionally(ex -> -1);
        System.out.println(chain.join()); // -1
    }
}
