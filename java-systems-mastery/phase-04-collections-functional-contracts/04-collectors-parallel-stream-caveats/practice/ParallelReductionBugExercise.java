import java.util.List;

public class ParallelReductionBugExercise {
    public static void main(String[] args) {
        List<Double> values = List.of(100.0, 10.0, 2.0);
        double r = values.parallelStream().reduce(0.0, (a, b) -> a - b);
        System.out.println("Result: " + r);
        System.out.println("Exercise: explain why subtraction breaks in parallel reduction.");
    }
}
