## Concept: API evolution and compatibility

### Mid-level — daily use

- Default to **additive** changes; deprecate with a clear story; avoid silent semantic tightening.

### Senior — contracts

- Distinguish binary vs source compatibility for libraries; version DTOs and public types deliberately.

### Staff — governance

- CI checks (japicmp / Revapi), consumer communication, and measured removal.

### Checklist

- What is your deprecation and removal policy?
- How do you detect accidental breaking changes before release?

### Theory index

- [MidLevelAdditiveApiChanges.md](theory/MidLevelAdditiveApiChanges.md)
- [SeniorApiEvolutionAndCompatibility.md](theory/SeniorApiEvolutionAndCompatibility.md)
- [StaffApiCompatibilityGovernance.md](theory/StaffApiCompatibilityGovernance.md)

### Artifacts

- [BackwardCompatibleApiExample.java](examples/BackwardCompatibleApiExample.java)

**Next:** [02-architecture-tradeoffs-observability](../02-architecture-tradeoffs-observability/notes.md)
