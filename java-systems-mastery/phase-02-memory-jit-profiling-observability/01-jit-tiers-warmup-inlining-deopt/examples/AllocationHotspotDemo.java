import java.util.ArrayList;
import java.util.List;

public class AllocationHotspotDemo {
    public static void main(String[] args) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50_000; i++) {
            data.add("v-" + i); // deliberate allocation churn
        }
        System.out.println(data.size());
    }
}
