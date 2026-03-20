## Staff: observability boundaries (JFR and production)

- **What to record:** allocation, lock contention, I/O wait — align events with **symptoms** (p99, GC pauses).
- **Overhead vs signal:** continuous vs on-demand flight recordings; **retention** and **PII** in events.
- **Boundary ownership:** service mesh metrics vs JVM events — who acts on which signal?

Cross-link: [Phase 02 — JFR / JMC](../../../phase-02-memory-jit-profiling-observability/02-jfr-jmc-flame-graphs/notes.md).
