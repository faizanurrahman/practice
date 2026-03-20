# Phase 03 — Concurrency, JMM, modern execution

**JMM first**, then locks/atomics, executors/ForkJoin/`CompletableFuture`, then virtual threads / ScopedValue / structured concurrency.

| Order | Concept | Summary |
|------|---------|---------|
| 00 | [00-jmm-happens-before-safe-publication](00-jmm-happens-before-safe-publication/notes.md) | Happens-before, `volatile`, `final`, safe publication |
| 01 | [01-monitors-locks-atomics-contention](01-monitors-locks-atomics-contention/notes.md) | `synchronized`, lock states, CAS, contention, liveness |
| 02 | [02-executors-forkjoin-completablefuture](02-executors-forkjoin-completablefuture/notes.md) | Thread pools, rejection, ForkJoin, async composition |
| 03 | [03-virtual-threads-scopedvalue-structured-concurrency](03-virtual-threads-scopedvalue-structured-concurrency/notes.md) | Virtual threads, pinning, ScopedValue, structured concurrency |

**Priority:** must know.

**Legacy mapping:** former `phase-5-concurrency-primitives` + `phase-6-task-execution-models`.
