## Staff: OSR, CompileCommand, AOT profiling (Java 25 context)

- **On-Stack Replacement (OSR):** enter a method interpreted but exit the hot loop in optimized code — matters for long warm loops.
- **CompileCommand** / diagnostic flags (`PrintCompilation`, `PrintInlining`) are for controlled environments — avoid in production unless you accept overhead.
- **AOT / profiling data** (e.g. JEP 514 area in modern JDKs): reduces cold-start cost for some deployments — confirm applicability to your runtime (Graal/native vs HotSpot server).

**Staff:** tie startup SLOs to class-init + JIT + I/O; choose evidence (JFR + compilation logs) before tuning flags.
