## Concept: Reflection, proxies, and JIT interaction

### Mid-level

- Use reflection to inspect types and invoke methods by name; common in frameworks and tests.
- Dynamic proxies implement interfaces at runtime via `InvocationHandler`.

### Senior

- Proxy classes and `Method.invoke` paths add indirection and complicate dispatch.
- Method handles and hidden classes are alternatives for generated access.

### Staff

- Reflection-heavy hot paths resist inlining; mitigate with caching, codegen, or boundary-only use.
- Observability: watch for `Method.invoke` and generated accessors in profiles.

### Checklist

- [ ] Explain why `Proxy` + handler adds cost vs direct calls.
- [ ] List mitigations for reflection in performance-critical code.

### Theory index

- [MidLevelReflectionUsage.md](theory/MidLevelReflectionUsage.md)
- [SeniorProxyBytecodeAndCost.md](theory/SeniorProxyBytecodeAndCost.md)
- [StaffWhyReflectionBreaksInlining.md](theory/StaffWhyReflectionBreaksInlining.md)

### Artifacts

- [DynamicProxyDemo.java](examples/DynamicProxyDemo.java)

**Next phase:** [Phase 01 — type system](../../phase-01-type-system-object-model/README.md)
