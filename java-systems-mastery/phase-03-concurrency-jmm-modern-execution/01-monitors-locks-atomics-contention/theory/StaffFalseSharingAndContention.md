## Staff: false sharing, contention, liveness

- **False sharing:** independent fields on the same cache line; writes invalidate peers → throughput collapse. Mitigate with padding / `@Contended` / layout (expert).
- **Contention:** many threads fight for one lock — tail latency rises; consider sharding, lock-free structures, or redesign.
- **Liveness:** **deadlock** (circular wait), **livelock**, **starvation** — global lock ordering and bounded critical sections prevent most deadlocks.

**Diagnostics:** thread dumps for lock cycles; JFR lock events; CPU profiles showing monitor enter storms.
