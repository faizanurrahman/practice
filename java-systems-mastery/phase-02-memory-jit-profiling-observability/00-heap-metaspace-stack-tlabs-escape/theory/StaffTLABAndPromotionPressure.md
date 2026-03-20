## Staff: TLAB refill, generations, promotion pressure

- **Young collections** are frequent and usually cheaper; **promotion** to old generation raises pause risk when old fills.
- **TLAB refill** and allocation failure synchronize more; tail latency can jump when allocation rate spikes.
- **Safepoints** coordinate GC and other VM work — correlate STW pauses with allocation / GC logs and JFR.

**JMM:** visibility is **not** defined here — use [Phase 03 / JMM](../../phase-03-concurrency-jmm-modern-execution/00-jmm-happens-before-safe-publication/notes.md).
