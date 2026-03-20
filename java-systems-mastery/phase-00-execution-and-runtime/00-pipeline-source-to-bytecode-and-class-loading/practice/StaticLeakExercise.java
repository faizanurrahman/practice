import java.util.HashMap;
import java.util.Map;

public class StaticLeakExercise {
    private static final Map<String, byte[]> CACHE = new HashMap<>();

    static void load(String key, int mb) {
        CACHE.put(key, new byte[mb * 1024 * 1024]);
    }

    public static void main(String[] args) {
        load("a", 2);
        load("b", 2);
        System.out.println("Cache entries: " + CACHE.size());
        System.out.println("Exercise: explain why GC cannot reclaim these arrays.");
    }
}
