## Staff: records, patterns, and performance

- Small **records** used locally may benefit from **escape analysis** and scalarization like ordinary objects (implementation-dependent).
- Pattern matching reduces boilerplate; staff concern is **allocation in hot paths** (e.g. creating many short-lived record instances vs primitives).
- Sealed hierarchies help the JIT reason about **receiver types** at `switch` sites — fewer impossible branches.

**Trade-off:** binary/API evolution with sealed types and records requires planning (serialization, JDBC, frameworks).
