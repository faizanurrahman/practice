## Mid-level: virtual threads usage

- **`Thread.startVirtualThread(...)`** or **`Executors.newVirtualThreadPerTaskExecutor()`** — cheap threads for **blocking** work (I/O, JDBC, `Thread.sleep`).
- Use the same structured `try/finally` and interruption discipline as platform threads.
- **Do not** treat virtual threads as “free CPU parallelism” — CPU-bound work still needs pool sizing / `ForkJoinPool`.
