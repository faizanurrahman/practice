## Senior: rejection policies, `ForkJoinPool`, work stealing

- **`ThreadPoolExecutor`:** core/max pool, **work queue**, **rejection policy** (`Abort`, `CallerRuns`, `Discard` variants) define behavior under saturation.
- **Unbounded queues** hide overload until latency explodes — **bounded queue + explicit rejection** is usually safer for APIs you own.
- **`ForkJoinPool`:** work-stealing for **many small** tasks; `parallelStream()` uses the **common pool** — watch for **blocking tasks** stealing carriers from CPU work.

**Interview:** why `CallerRuns` acts as a throttle and changes caller thread latency.
