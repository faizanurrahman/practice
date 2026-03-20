## Staff: safe publication patterns

- **Single-writer + volatile publish:** write all shared state, then set `volatile ready = true`; readers check `ready` before using state.
- **Initialization-on-demand holder** idiom for lazy singletons (class init is JVM-synchronized).
- **Avoid leaking `this`** from constructors to other threads — publication races break `final` guarantees.

**Diagnostics:** intermittent `NullPointerException` / half-initialized reads under load → suspect publication, not “random JVM bug”.
