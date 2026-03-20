## Senior: `ConcurrentHashMap` internals (conceptual)

- Modern CHM uses **bin-level** locking / CAS rather than one global lock; **size()** may be **approximate** under contention.
- **Iteration** is **weakly consistent** — may reflect some concurrent updates, not a point-in-time snapshot.
- Contrast with **`Collections.synchronizedMap`** — single lock, simpler semantics, worse scalability.

**Interview:** why CHM’s `size()` is not a strict transactional counter.
