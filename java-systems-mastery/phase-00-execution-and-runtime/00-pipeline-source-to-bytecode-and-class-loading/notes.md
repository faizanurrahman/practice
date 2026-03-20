## Concept: Pipeline — source to bytecode and class loading

### 1) Mid-level (daily coding confidence)

- Compile with `javac`, run with `java`; use `-cp` for dependencies.
- Know that **`.class`** files hold bytecode and a **constant pool** of symbolic references.
- Understand that classes are **loaded** before use and **initialized** (static blocks) once.

### 2) Senior (internals)

- **Linking:** verify → prepare → resolve; **initialization** runs `<clinit>`.
- **Delegation model** for class loaders; bootstrap vs application loader.
- Execution: interpreter → tiered JIT; cold vs warm behavior.

### 3) Staff (trade-offs and diagnostics)

- **Metaspace vs heap:** metadata growth, loader lifetimes, static roots pinning graphs.
- Correlate startup latency with class loading, init, and first JIT tiers.
- Prove bottlenecks with logs and profiles, not assumptions.

### 4) Checklist

- [ ] Trace `java MyApp` from process start to `main` entry.
- [ ] Explain why symbolic constant-pool references matter for linking.
- [ ] Name failure modes: class-init deadlock, metaspace pressure, static leaks.

### 5) Theory index

- [MidLevelCompilationAndRunning.md](theory/MidLevelCompilationAndRunning.md)
- [SeniorClassLoadingLinkingInitialization.md](theory/SeniorClassLoadingLinkingInitialization.md)
- [StaffMetaspaceVsHeapImplications.md](theory/StaffMetaspaceVsHeapImplications.md)

### 6) Artifacts

- `examples/ClassInitOrderDemo.java`
- `practice/StaticLeakExercise.java`

**Next:** [01-bytecode-constant-pool-invokedynamic-and-dispatch](../01-bytecode-constant-pool-invokedynamic-and-dispatch/notes.md)
