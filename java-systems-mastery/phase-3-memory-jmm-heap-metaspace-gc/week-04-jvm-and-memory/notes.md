## Week 04 - JVM and Memory (JMM, Allocation Paths, GC, and Escape Analysis)

This week teaches you to reason about:

- what gets allocated (and where)
- how the JVM establishes visibility guarantees (JMM happens-before)
- why GC pauses happen (safepoints, generations, allocation failure)
- how TLABs and escape analysis affect real allocation cost

Staff mental model:

When a system slows down, you often misdiagnose "CPU" vs "allocation pressure" vs "stop-the-world pauses".
This week builds the vocabulary to pick the right root cause quickly.

---

## 1. Learning Objectives

You should be able to:

- explain happens-before edges at a conceptual level (`volatile`, locks, thread start/join, `final`)
- describe object lifetime and the typical allocation path
- understand GC generation behavior and what causes safepoints
- explain TLABs and why short-lived allocations can be cheap until they escape
- connect escape analysis to observable allocation/throughput changes

---

## 2. The Java Memory Model (JMM) in Practice

### 2.1 Mental model - visibility is an ordering relation

Java does not guarantee that reads/writes happen in program order unless you establish happens-before.

Happens-before can come from:

- `volatile` writes/reads
- entering/leaving `synchronized` blocks
- lock acquisition/release (and other explicit concurrency primitives)
- thread start and thread join
- `final` field semantics for properly constructed and safely published objects

### 2.2 Why `volatile` is not "just visibility"

`volatile` also provides ordering constraints:

- writes to `volatile` happen-before subsequent reads of that same variable

But if you publish multiple variables, you still need consistent publication discipline:

- use `final` fields for immutable snapshots
- or use synchronization/volatile as a proper publication mechanism

Failure mode: reading a flag that is `volatile`, but still observing stale related data because the publication pattern was incomplete.

---

## 3. Object Lifetime and Allocation Paths

### 3.1 What is "allocation cost" in Java?

Allocation cost is not only "create an object":

- the JVM must reserve space in the heap
- the allocator can be fast when it can use thread-local allocation buffers (TLABs)
- allocations can become expensive when objects escape or the thread-local path can't be used

### 3.2 TLABs - why many short-lived objects are cheap

TLABs reduce synchronization and contention:

- each thread allocates from its own buffer
- allocation is typically just pointer bumping

But:

- if objects escape, escape analysis may be unable to scalar replace
- TLAB refill and allocation failure events become points of observable latency

---

## 4. Garbage Collection, Generations, and Safepoints

### 4.1 Generational behavior (intuition)

Most garbage collection implementations treat objects as:

- young generation: many objects die young; cheap collections
- old generation: objects surviving longer are promoted

When old generation pressure grows, the cost increases and you can observe longer pauses.

### 4.2 Safepoints and STW pauses

Stop-the-world pauses typically occur at safepoints:

- the JVM brings threads into a safe state
- then performs GC phases

In production:

- tail latency spikes often correlate with safepoints and allocation failure

Senior insight: if the system pauses but CPU is low, GC/safepoints are often the culprit.

---

## 5. Escape Analysis and Scalar Replacement

Escape analysis answers:
"Does this object escape the method?"

If it does not escape:

- the JIT can avoid allocating the object on the heap
- it may scalar-replace the object into individual fields stored in registers/stack

How you can think about it:

- create temporary objects freely when they do not escape
- avoid returning them, storing them into shared structures, or capturing them in lambdas if you want stack-like behavior

---

## 6. Senior/Staff Interview Questions

### Q1: What establishes a happens-before edge for `final` fields?

If an object is properly constructed and safely published, the JMM provides special visibility guarantees for `final` fields.
Safe publication usually comes from proper synchronization, volatile publication, thread start/join, or other happens-before mechanisms.

### Q2: Why can allocation appear "free" until it suddenly isn't?

Because fast allocation relies on JVM/GC heuristics and fast paths like TLABs.
Once allocation pressure increases, objects escape more often, TLABs refill more frequently, and GC runs more.
At that point you observe latency spikes due to safepoints and collector work.

### Q3: What is the role of safepoints in diagnosing latency?

Safepoints are the points where threads are brought to a safe state for JVM operations such as GC.
If you see tail latency spikes that correlate with low CPU, safepoints and GC are prime suspects.

---

## 7. Summary

Week 04 gave you the mental model to connect:

- visibility (JMM) to correctness under concurrency
- allocation behavior (TLABs) to throughput
- GC/safepoints to tail latency
- escape analysis to "why allocations sometimes disappear in profiles"

Next: Week 05 will focus on concurrency primitives and ordering guarantees you pull daily in high-throughput systems.

