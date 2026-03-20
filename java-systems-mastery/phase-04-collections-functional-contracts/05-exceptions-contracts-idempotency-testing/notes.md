## Concept: Exceptions, contracts, idempotency, testing

### Mid-level — daily use

- Map exceptions to **HTTP / RPC** codes; preserve **cause** chains.
- Classify errors for **retry** vs **fail fast**; use `Optional` and validation at **trust boundaries**.

### Senior — design

- Domain exception hierarchies; when checked vs unchecked; cause preservation patterns.

### Staff — production

- Idempotency keys, retry storms, contract tests across services.

### Checklist

- Can you draw your error taxonomy and retry policy on one whiteboard?
- Where is validation enforced vs asserted?

### Theory index

- [MidLevelCheckedUncheckedAndOptional.md](theory/MidLevelCheckedUncheckedAndOptional.md)
- [SeniorDomainExceptionDesignAndCause.md](theory/SeniorDomainExceptionDesignAndCause.md)
- [StaffIdempotencyRetryAndContractTesting.md](theory/StaffIdempotencyRetryAndContractTesting.md)

### Artifacts

- [ValidationBoundaryExample.java](concepts/ValidationBoundaryExample.java)
- [CausePreservationExample.java](examples/CausePreservationExample.java)
- [RetryClassificationExercise.java](practice/RetryClassificationExercise.java)

**Optional:** [property-based testing](supplementary/property-based-testing.md) (bonus).

**Phase complete:** return to [phase README](../README.md) or continue to [Phase 05 — design](../../phase-05-applied-design-architecture/README.md).
