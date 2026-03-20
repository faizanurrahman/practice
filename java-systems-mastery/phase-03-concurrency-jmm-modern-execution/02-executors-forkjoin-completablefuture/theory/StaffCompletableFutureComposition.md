## Staff: `CompletableFuture` composition and failure propagation

- Async stages form a **DAG**; failures can **short-circuit** or be **handled** per stage — silent drops are a production hazard.
- **Executor choice per stage** changes threading and ordering; default `ForkJoinPool.commonPool()` may be wrong for blocking callbacks.
- **Backpressure:** thread pools are not reactive backpressure — when queues grow, you are **delaying** work or **rejecting**; align with API SLAs and bulkheads.

**Operational:** enforce timeouts, explicit `exceptionally`/`handle`, and logging on unexpected completion states.
