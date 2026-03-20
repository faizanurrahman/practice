## Concept: JIT tiers, warmup, inlining, deoptimization

### Theory index

- [MidLevelJITWarmupConcepts.md](theory/MidLevelJITWarmupConcepts.md)
- [SeniorC1C2InliningDeoptimisation.md](theory/SeniorC1C2InliningDeoptimisation.md)
- [StaffOSRAndAOTProfiling.md](theory/StaffOSRAndAOTProfiling.md)

**JFR / JMC workflow:** [02-jfr-jmc-flame-graphs](../02-jfr-jmc-flame-graphs/notes.md)

### 1) Learning objectives
- Explain warmup, inlining, deoptimization, and allocation pressure.
- Correlate cold vs steady-state latency with tiered compilation.

### 2) Mental models
- Performance is a system property, not a method property.
- Always separate cold-start and steady-state behavior.
- Tail latency often points to contention, safepoints, or GC pauses.

### 3) Internal mechanisms
- Tiered compilation moves methods across optimization levels.
- Inlining and escape analysis can remove call/alloc overhead.
- Deoptimization invalidates assumptions and falls back to interpreter.

### 4) Performance implications
- Microbenchmarks lie without warmup and proper harnessing.
- Allocation rate drives GC frequency and pause distribution.
- Lock contention can dominate CPU without obvious business code hotspots.

### 5) Failure modes / diagnostics
- Premature tuning without profiler evidence.
- Chasing average latency while p99 is collapsing.
- Interpreting flame graphs without thread-state context.

### 6) Interview Q&A
- How do you profile a latency regression in production?
- What signs indicate deoptimization churn?
- When do you tune GC vs fix allocation behavior?

### 7) Summary
Measure first, then optimize with explicit hypotheses and validation loops.

**Related:** JMM in [Phase 03 — 00-jmm](../../phase-03-concurrency-jmm-modern-execution/00-jmm-happens-before-safe-publication/notes.md).

