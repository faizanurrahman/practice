## Concept: Monitors, locks, atomics, contention

**Prerequisite:** [00-jmm](../00-jmm-happens-before-safe-publication/notes.md) (visibility / `volatile` / hb graph).

### Theory index

- [MidLevelSynchronizedUsage.md](theory/MidLevelSynchronizedUsage.md)
- [SeniorBiasedLightHeavyLocksAndCAS.md](theory/SeniorBiasedLightHeavyLocksAndCAS.md)
- [StaffFalseSharingAndContention.md](theory/StaffFalseSharingAndContention.md)

### 1) Learning objectives
- Separate **mutual exclusion** from **visibility** (JMM vs monitors).
- Choose `synchronized`, explicit locks, and atomics intentionally.
- Diagnose deadlock, livelock, starvation, and contention.

### 2) Mental models
- **Critical section model**: protect invariants, not single lines of code.
- **Happens-before graph**: correctness is graph edges, not CPU speed.
- **Contention budget**: locks serialize throughput when contention rises.

### 3) Internal mechanisms
- `synchronized` maps to monitor enter/exit with memory barriers.
- `volatile` gives visibility + ordering, but not compound atomicity.
- CAS atomics avoid blocking but can spin and burn CPU under contention.

### 4) Performance implications
- Biased/lightweight lock optimizations help uncontended paths.
- High contention increases context switches and tail latency.
- False sharing can hurt throughput even without lock contention.

### 5) Failure modes / diagnostics
- Deadlock from inconsistent lock ordering.
- Livelock from overly cooperative retry loops.
- Starvation from unfair lock acquisition patterns.

### 6) Interview Q&A
- When is `volatile` enough?
- Why can lock-free be slower than lock-based?
- How do you prove a deadlock from thread dumps?

### 7) Summary
Use primitives as tools for invariants and service-level objectives, not as syntax features.

