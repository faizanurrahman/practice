## HashMap Internals: Resize and Treeification (What Changes the Curve)

Staff-level takeaway: correctness is stable even with collisions; performance is what breaks.

### 1. Buckets, chains, and conversion under pressure

HashMap stores entries in buckets indexed by hash.
When collisions occur, entries share a bucket via a chain.

Under heavy collisions, a bucket can convert to a tree representation.
This is a worst-case defense, not a performance plan.

### 2. Load factor as the resize trigger

Resize occurs when:

`size > capacity * loadFactor`

After resize:

- bucket count increases
- entries are rehashed to new buckets

That rehash is where latency spikes and GC pressure can come from.

### 3. Practical implication for system design

If you expect bursty traffic or adversarial key patterns, plan:

- choose an initial capacity close to steady-state
- avoid mutable keys
- consider key normalization or hashing strategies at boundaries

