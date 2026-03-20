## Concept: Heap, metaspace, stack, TLABs, escape

### Theory index

- [MidLevelStackVsHeap.md](theory/MidLevelStackVsHeap.md)
- [SeniorEscapeAnalysisScalarReplacement.md](theory/SeniorEscapeAnalysisScalarReplacement.md)
- [StaffTLABAndPromotionPressure.md](theory/StaffTLABAndPromotionPressure.md)

This concept teaches you to reason about:

- what gets allocated (and where: heap vs stack frames vs metaspace metadata)
- why GC pauses happen (safepoints, generations, allocation failure)
- how TLABs and escape analysis affect real allocation cost

**Canonical JMM** (happens-before, `volatile`, safe publication, `final`):

- [Phase 03 — `00-jmm-happens-before-safe-publication`](../../phase-03-concurrency-jmm-modern-execution/00-jmm-happens-before-safe-publication/notes.md)

Staff mental model:

When a system slows down, you often misdiagnose "CPU" vs "allocation pressure" vs "stop-the-world pauses".
This concept builds vocabulary to pick the right root cause quickly.

---

## 1. Learning Objectives

You should be able to:

- describe object lifetime and the typical allocation path
- understand GC generation behavior and what causes safepoints
- explain TLABs and why short-lived allocations can be cheap until they escape
- connect escape analysis to observable allocation/throughput changes
- point to the dedicated JMM concept for visibility and publication rules

---

## 2. Object Lifetime and Allocation Paths

### 2.1 What is "allocation cost" in Java?

Allocation cost is not only "create an object":

- the JVM must reserve space in the heap
- the allocator can be fast when it can use thread-local allocation buffers (TLABs)
- allocations can become expensive when objects escape or the thread-local path can't be used

### 2.2 TLABs - why many short-lived objects are cheap

TLABs reduce synchronization and contention:

- each thread allocates from its own buffer
- allocation is typically just pointer bumping

But:

- if objects escape, escape analysis may be unable to scalar replace
- TLAB refill and allocation failure events become points of observable latency

---

## 3. Garbage Collection, Generations, and Safepoints

### 3.1 Generational behavior (intuition)

Most garbage collection implementations treat objects as:

- young generation: many objects die young; cheap collections
- old generation: objects surviving longer are promoted

When old generation pressure grows, the cost increases and you can observe longer pauses.

### 3.2 Safepoints and STW pauses

Stop-the-world pauses typically occur at safepoints:

- the JVM brings threads into a safe state
- then performs GC phases

In production:

- tail latency spikes often correlate with safepoints and allocation failure

Senior insight: if the system pauses but CPU is low, GC/safepoints are often the culprit.

Deeper GC trade-offs (G1 vs ZGC symptom-level): see concept `03-gc-g1-zgc-symptom-driven` in this phase.

---

## 4. Escape Analysis and Scalar Replacement

Escape analysis answers:
"Does this object escape the method?"

If it does not escape:

- the JIT can avoid allocating the object on the heap
- it may scalar-replace the object into individual fields stored in registers/stack

How you can think about it:

- create temporary objects freely when they do not escape
- avoid returning them, storing them into shared structures, or capturing them in lambdas if you want stack-like behavior

---

## 5. Senior/Staff Interview Questions

### Q1: Why can allocation appear "free" until it suddenly isn't?

Because fast allocation relies on JVM/GC heuristics and fast paths like TLABs.
Once allocation pressure increases, objects escape more often, TLABs refill more frequently, and GC runs more.
At that point you observe latency spikes due to safepoints and collector work.

### Q2: What is the role of safepoints in diagnosing latency?

Safepoints are the points where threads are brought to a safe state for JVM operations such as GC.
If you see tail latency spikes that correlate with low CPU, safepoints and GC are prime suspects.

### Q3: Where do I learn happens-before and safe publication?

In [Phase 03 — JMM concept](../../phase-03-concurrency-jmm-modern-execution/00-jmm-happens-before-safe-publication/notes.md).

---

## 6. Summary

This concept connects:

- allocation behavior (TLABs) to throughput
- GC/safepoints to tail latency
- escape analysis to "why allocations sometimes disappear in profiles"

Correctness under concurrency (JMM) is covered next door in **Phase 03**.

**Next:** [Phase 03 — concurrency primitives and execution models](../../phase-03-concurrency-jmm-modern-execution/README.md).
