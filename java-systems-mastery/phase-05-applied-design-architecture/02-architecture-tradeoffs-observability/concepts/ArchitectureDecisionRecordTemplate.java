import java.util.List;

public record ArchitectureDecisionRecordTemplate(
    String context,
    String decision,
    List<String> alternatives,
    List<String> consequences
) {}
