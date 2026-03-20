## Staff: why reflection (and proxies) fight the JIT

- The JIT excels when **targets are known** and **call sites stabilize**.
- Reflection **`Method.invoke`** is inherently polymorphic from the JVM’s perspective: the callee may change, access checks may apply, and intrinsics are limited.
- Dynamic proxies add **megomorphic interface dispatch** + handler indirection in hot paths.

**Staff mitigation**

- Cache `Method`, `MethodHandle`, or generated accessors; avoid per-invocation lookup.
- Move hot paths off reflection in inner loops; keep reflection at boundaries (startup, config).
- Prefer **method handles** or compile-time codegen where throughput matters.

**Observability:** CPU samples often show `Method.invoke`, `GeneratedMethodAccessor`, or proxy classes dominating when reflection is abused.
