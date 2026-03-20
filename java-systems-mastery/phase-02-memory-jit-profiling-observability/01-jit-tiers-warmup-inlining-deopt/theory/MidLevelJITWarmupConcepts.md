## Mid-level: JIT warmup (intuition)

- First runs of a method may be **interpreted** or **C1-compiled**; hot code gets **C2** optimizations.
- After warmup, throughput often improves; **latency** of first requests can still be high (“cold start”).
- **Microbenchmarks** without warmup lie — always warm the code path you measure.
