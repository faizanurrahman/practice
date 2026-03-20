## Staff: architecture trade-off rubric (interview)

Score options on:

1. **Correctness** — invariants, failure modes, idempotency at boundaries (Phase 04 contracts).
2. **Operability** — deployability, rollbacks, observability, blast radius.
3. **Performance** — tail latency, resource ceilings, GC / allocation story.
4. **Team velocity** — coupling, testability, onboarding cost.

**Deliverable:** 1-page ADR using [ArchitectureDecisionRecordTemplate.java](../concepts/ArchitectureDecisionRecordTemplate.java) + explicit **revisit** date.
