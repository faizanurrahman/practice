## Concept: Autoboxing and primitive arrays

### Mid-level

- Prefer **`int[]` / `long[]`** for dense numeric storage; use **`List<Integer>`** when you need collection API semantics.
- Use **`IntStream`** instead of `Stream<Integer>` for primitive pipelines when possible.

### Senior

- Boxing allocates; autoboxing in tight loops dominates allocation profiles.

### Theory index

- [MidLevelWhenToUsePrimitiveArrays.md](theory/MidLevelWhenToUsePrimitiveArrays.md)
- [SeniorBoxingCostVsIntArray.md](theory/SeniorBoxingCostVsIntArray.md)

### Checklist

- [ ] Explain why `List<Integer>` uses more memory than `int[]` for the same logical length.

**Next phase:** [Phase 02 — memory and profiling](../../phase-02-memory-jit-profiling-observability/README.md)
