## Concept: Executors, ForkJoin, CompletableFuture

### Theory index

- [MidLevelThreadPoolSizing.md](theory/MidLevelThreadPoolSizing.md)
- [SeniorRejectionPoliciesWorkStealing.md](theory/SeniorRejectionPoliciesWorkStealing.md)
- [StaffCompletableFutureComposition.md](theory/StaffCompletableFutureComposition.md)

### Related

- **Virtual threads / ScopedValue:** [03-virtual-threads-scopedvalue-structured-concurrency](../03-virtual-threads-scopedvalue-structured-concurrency/notes.md)

### 1) Learning objectives

- Size pools and choose queue + rejection policy for overload.
- Explain work-stealing and common-pool hazards with `parallelStream`.
- Compose `CompletableFuture` without losing failures.

### 2) Artifacts

- [PoolSelectionMatrix.java](concepts/PoolSelectionMatrix.java)
- [CompletableFutureFailurePropagation.java](examples/CompletableFutureFailurePropagation.java)
- [ThreadPoolOverloadExercise.java](practice/ThreadPoolOverloadExercise.java)

### 3) Summary

Execution policy is a reliability knob — unbounded queues are a latent incident.
