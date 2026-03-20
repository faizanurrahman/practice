## Staff: COW vs CHM vs synchronized collections

- **Latency vs throughput:** COW optimizes **read** latency at **write** cost; CHM spreads **write** contention; synchronized map is simplest but often bottlenecks.
- **Memory:** COW duplicates backing array on each write — **memory spikes** on bulk updates.
- **Observability:** measure **contention** (JFR lock events) and **allocation** when choosing.

**Staff:** “We use COW for config snapshot read by 10k threads/sec and update once per minute” — defensible.
