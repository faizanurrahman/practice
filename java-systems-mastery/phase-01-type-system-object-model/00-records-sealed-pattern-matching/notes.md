## Concept: Records, sealed types, and pattern matching

### Mid-level

- Use **records** for immutable data carriers; validate in **compact constructors**.
- Use **`sealed`** + **`permits`** to model closed hierarchies; combine with exhaustive **`switch`**.

### Senior

- Pattern matching and exhaustiveness are compile-time proofs over sealed types.

### Staff

- Evolution of sealed APIs, framework compatibility (reflection, serialization), and allocation in hot paths.

### Theory index

- [MidLevelRecordsAndCompactConstructors.md](theory/MidLevelRecordsAndCompactConstructors.md)
- [SeniorSealedTypesAndPatternMatching.md](theory/SeniorSealedTypesAndPatternMatching.md)
- [StaffPerformanceWithEscapeAnalysis.md](theory/StaffPerformanceWithEscapeAnalysis.md)

### Checklist

- [ ] Write a sealed interface + records + exhaustive `switch`.
- [ ] Contrast with visitor pattern for closed hierarchies.

**Next:** [01-object-layout-header-alignment-dispatch](../01-object-layout-header-alignment-dispatch/notes.md)
