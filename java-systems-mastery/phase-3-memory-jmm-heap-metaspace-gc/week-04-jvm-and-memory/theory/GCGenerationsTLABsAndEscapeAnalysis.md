## GC Generations, TLABs, and Escape Analysis (Why Latency Moves)

Staff-level takeaway: object allocation and GC are tightly coupled to latency.

### 1. Generations: young is cheap, old is expensive

Generational collectors assume most objects die young.
Young collections are typically more frequent and cheaper.
Pressure in the old generation can trigger longer pauses.

### 2. TLABs: thread-local allocation buffers

TLABs make short-lived allocations cheap by avoiding synchronization.
Allocation failure and TLAB refill events are where allocation cost becomes visible.

### 3. Escape analysis: removing allocations when possible

Escape analysis determines whether an object escapes a method.
If it does not escape, the JIT can scalar-replace the object and avoid heap allocation.

This often explains "why allocations disappeared" in profiles even though your source code allocates.

