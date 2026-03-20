## Staff: choosing a concurrency model at system boundaries

- **Thread-per-request vs pools vs virtual threads** — match **latency SLO**, **blocking I/O**, and **pinning** risk (see Phase 03 virtual threads).
- **Message-driven / async** — great for fan-out; harder **debuggability** and **backpressure** unless explicit.
- **Staff:** document **failure domains**: what blocks, what times out, what retries, and under which pool.

Cross-link: [Phase 03 — executors & virtual threads](../../../phase-03-concurrency-jmm-modern-execution/README.md).
