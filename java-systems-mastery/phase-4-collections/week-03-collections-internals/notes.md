## Week 03 - Collections Internals and Trade-offs (Senior/Staff Depth)

Senior goal: when you choose `List`, `Set`, or `Map`, you should be able to explain:

- the complexity profile *and* why the constant factors matter
- how hashing and resizing behavior shape latency under load
- what "fail-fast" really means for iterators (and what it does not guarantee)
- what concurrent collections trade away (e.g., approximate size)

This week focuses on *predictive selection*.
You will learn to anticipate performance degradation caused by:

- poor hashing / adversarial keys
- mutation during iteration
- load-factor and resize cascades
- contention patterns in concurrent maps

---

## 1. Learning Objectives

By the end you should be able to:

- compare `ArrayList` vs `LinkedList` beyond Big-O (allocation/locality/iterator costs)
- explain HashMap internals: buckets, collision strategies, load factor, resize rules
- understand treeification threshold behavior under heavy collisions
- reason about iterator fail-fast semantics and why "fail-fast" is not "thread-safe"
- understand `ConcurrentHashMap` behavior differences, especially `size()` and iteration semantics

---

## 2. Collection Families - Complexity is only half the story

### 2.1 Lists: why `ArrayList` usually wins

`ArrayList` provides:

- O(1) indexed access
- contiguous storage and good cache locality
- lower per-element overhead than node-based lists

`LinkedList` provides:

- O(1) insert/delete *when you already have the node reference*
- poor locality and typically more pointer chasing

In real systems, "mid-list inserts" can still be slower with linked lists because you pay traversal costs and suffer locality penalties.

Senior heuristic:

- prefer array-backed collections unless you have a clear reason for frequent middle insert/remove *and* you can avoid repeated traversal

### 2.2 Sets and Maps - equality and hashing cost

Hash-based `Set`/`Map` correctness depends on:

- stable `equals` semantics
- stable `hashCode` semantics
- immutability of key fields used by equality/hash

Performance depends on:

- distribution quality (how many collisions happen)
- collision handling cost (chain length / tree height)
- resize frequency (how often the table expands and rehashes)

---

## 3. HashMap Internals - Load Factor, Resizing, and Collision Handling

### 3.1 Buckets and nodes

A `HashMap` stores entries in an array of buckets.
Each bucket holds a chain of nodes (linked list style) at first.

### 3.2 Load factor and resize behavior

Load factor determines the threshold for resizing:

- when size grows beyond `capacity * loadFactor`, the table resizes
- resizing allocates a new bucket array and rehashes entries

Why it matters:

- resize causes large, correlated latency spikes
- the rehash process can amplify GC pressure due to allocation of new internal structures

Senior insight: if your workload is bursty, you often feel resize as a tail-latency incident.

### 3.3 Treeification under adversarial collision patterns

In modern Java, buckets can convert from a list of nodes into a tree representation under heavy collisions.

Staff-level message:

- treeification is a defense against collision attacks
- it is still more expensive than well-distributed hashes
- adversarial keys remain an anti-pattern; you want to reject or salt at boundaries when possible

### 3.4 Failure mode: mutable keys

If fields used by `equals` or `hashCode` change after insertion:

- lookups can fail because the bucket changes
- `HashMap` invariants break from your perspective

This manifests as "missing entries" that are logically present.

---

## 4. Iterators - Fail-fast is a warning, not a guarantee

Iterators in `ArrayList`/`HashMap` are often "fail-fast":

- they may detect concurrent modification by checking a modification counter
- if modification is detected, they throw `ConcurrentModificationException`

What fail-fast does NOT do:

- it does not make the collection thread-safe
- it does not guarantee detection of all races

In multi-threaded systems, prefer:

- external synchronization
- concurrent collections
- snapshot/Copy-on-Write patterns (when appropriate)

---

## 5. Concurrency Angle - ConcurrentHashMap and approximate size

`ConcurrentHashMap` supports concurrent updates without global locking.

Differences vs HashMap:

- internal segmentation and/or lock-free structures for many operations
- weakly consistent iterators
- `size()` is not a stable atomic snapshot of the map at one point in time

Senior insight: monitoring and alerting must understand these semantics.
If you build SLOs on `size()` exactness, you will create false alarms.

---

## 6. Senior/Staff Interview Questions

### Q1: When does HashMap resize, and why does that affect tail latency?

Resize occurs when the map's size exceeds `capacity * loadFactor`.
Resizing allocates a new bucket array and rehashes entries, which costs CPU and can create GC pressure.
Because it is correlated across many entries, it often shows up as tail-latency spikes.

### Q2: Explain the performance impact of poor hash functions.

Poor hashing increases bucket collisions.
More collisions increase lookup time due to:

- longer chains (more node comparisons)
- potential treeification (higher per-node cost but improved worst-case)

Even with defenses, poor hash distributions still waste CPU and can degrade throughput.

### Q3: What does ConcurrentHashMap `size()` returning an approximate value mean for monitoring?

It means `size()` is not guaranteed to reflect the exact number of entries at a single instant.
If monitoring assumes strict correctness, alerts can flap.
Use better instrumentation (gauges based on events or stronger counters) when exactness is required.

---

## 7. Summary

Week 03 taught:

- collection selection beyond Big-O (locality and allocation cost)
- HashMap behavior: load factor, resize, collision handling, treeification
- iterator fail-fast semantics and why they are not thread safety
- concurrent map semantics: weak iteration and approximate size

Next: Week 04 transitions from collections to the JVM itself: JMM, allocation paths, and GC behavior.

