## Mid-level: `volatile` and `final` (daily use)

- **`volatile`** on a field ensures writes are visible to other threads **in a defined order** relative to that variable — use for one-shot flags and simple publication points.
- **`final` fields** (when the object is **safely published**) are visible as fully constructed to other threads — foundation for immutable snapshots.
- **`volatile` does not fix** compound actions: `count++` still needs atomics or locks.

**Next:** formal happens-before in [SeniorHappensBeforeRules.md](SeniorHappensBeforeRules.md).
