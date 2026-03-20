## Concept: Virtual threads, ScopedValue, structured concurrency

### Theory index

- [MidLevelVirtualThreadsUsage.md](theory/MidLevelVirtualThreadsUsage.md)
- [SeniorPinningAndCarrierThreads.md](theory/SeniorPinningAndCarrierThreads.md)
- [StaffScopedValueStructuredConcurrency.md](theory/StaffScopedValueStructuredConcurrency.md)

### Prerequisites

- [00-jmm](../00-jmm-happens-before-safe-publication/notes.md), [02-executors](../02-executors-forkjoin-completablefuture/notes.md)

### Checklist

- [ ] Explain pinning and why `synchronized` can hurt under virtual threads.
- [ ] Contrast ScopedValue with ThreadLocal for request context.
