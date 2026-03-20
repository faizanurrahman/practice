## Concept: Architecture trade-offs and observability

### Mid-level — daily use

- Tie every major decision to **operability**: deploy, rollback, metrics, logs, traces.

### Senior — system shape

- Boundaries that limit blast radius; clear ownership of retries and timeouts.

### Staff — interviews and production

- Rubric for comparing architectures; align JVM profiling (JFR) with service-level SLOs.

### Checklist

- Can you write an ADR with alternatives and consequences in 10 minutes?
- Where do observability hooks live vs business logic?

### Theory index

- [StaffConcurrencyModelSelectionAtBoundaries.md](theory/StaffConcurrencyModelSelectionAtBoundaries.md)
- [StaffObservabilityAndJfrBoundaries.md](theory/StaffObservabilityAndJfrBoundaries.md)
- [StaffSystemDesignTradeoffRubric.md](theory/StaffSystemDesignTradeoffRubric.md)

### Artifacts

- [ArchitectureDecisionRecordTemplate.java](concepts/ArchitectureDecisionRecordTemplate.java)
- [ArchitectureInterviewPromptExercise.java](practice/ArchitectureInterviewPromptExercise.java)
