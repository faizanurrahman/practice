## Staff: field reordering, alignment, false sharing

- JVM may **reorder fields** to reduce padding; declared order is not guaranteed layout order.
- **False sharing:** independent fields on the same cache line mutated by different threads → coherence traffic; `@Contended` / padding are mitigations (expert use).
- **Staff sizing:** estimate collection memory with header + refs + alignment; justify `int[]` vs `List<Integer>` for throughput services.

Always validate on **production JDK + flags** (compressed OOPs, heap size).
