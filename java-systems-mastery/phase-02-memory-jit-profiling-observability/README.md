# Phase 02 — Memory, JIT, profiling, observability

Heap behavior, escape/TLAB, tiered JIT, **JFR/JMC**, GC trade-offs, leak diagnostics. **JMM** lives in Phase 03 (`00-jmm`).

| Order | Concept | Summary |
|------|---------|---------|
| 00 | [00-heap-metaspace-stack-tlabs-escape](00-heap-metaspace-stack-tlabs-escape/notes.md) | Stack vs heap, TLABs, escape analysis, promotion |
| 01 | [01-jit-tiers-warmup-inlining-deopt](01-jit-tiers-warmup-inlining-deopt/notes.md) | Tiered compilation, inlining, deopt, OSR |
| 02 | [02-jfr-jmc-flame-graphs](02-jfr-jmc-flame-graphs/notes.md) | JFR recording, JMC, flame-style diagnosis |
| 03 | [03-gc-g1-zgc-symptom-driven](03-gc-g1-zgc-symptom-driven/notes.md) | Generations, G1 vs low-latency collectors |
| 04 | [04-allocation-retention-leak-diagnostics](04-allocation-retention-leak-diagnostics/notes.md) | Leak patterns, heap dumps, dominators |

**Priority:** must know (GC flag depth optional by role).

**Legacy mapping:** former `phase-3-memory-jmm-heap-metaspace-gc` + `phase-9-performance-profiling`.
