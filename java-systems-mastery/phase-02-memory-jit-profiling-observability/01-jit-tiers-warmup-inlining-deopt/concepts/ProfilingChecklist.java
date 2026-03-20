import java.util.List;

public final class ProfilingChecklist {
    private ProfilingChecklist() {}

    public static List<String> steps() {
        return List.of(
            "Define SLA and regression symptom",
            "Capture JFR under representative load",
            "Identify top CPU + allocation + lock hotspots",
            "Apply minimal fix and compare recordings"
        );
    }
}
