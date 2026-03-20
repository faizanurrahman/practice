import java.util.List;
import java.util.stream.IntStream;

public class BoxingCostExample {
    public static void main(String[] args) {
        List<Integer> boxed = IntStream.range(0, 1_000).boxed().toList();
        int sum = boxed.stream().mapToInt(Integer::intValue).sum();
        System.out.println(sum);
    }
}
