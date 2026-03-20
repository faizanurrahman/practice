## Senior: biased, thin, inflated locks; CAS and atomics

- HotSpot **biased locking** (where enabled) optimizes uncontended locks by pinning the lock to one thread; **revocation** has cost when contention appears.
- **Lightweight** (thin) locks use CAS in the mark word; **heavyweight** inflates to full monitor with OS assistance under contention.
- **`java.util.concurrent.atomic`** and **`VarHandle`** provide explicit CAS/read-modify-write without full monitors for suitable algorithms.

**Interview:** why `x++` is not safe even with `volatile` on `x` (read-modify-write).
