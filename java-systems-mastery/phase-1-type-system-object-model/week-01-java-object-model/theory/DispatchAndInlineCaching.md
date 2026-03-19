## Dispatch and Inline Caching (Polymorphism Shape Matters)

Staff-level takeaway: the JVM's ability to optimize depends heavily on whether your call sites stay stable.

### 1. Invocation bytecodes imply different lookup paths

- `invokestatic`: resolved directly
- `invokespecial`: constructors/private/super calls
- `invokevirtual`: virtual dispatch through class hierarchy
- `invokeinterface`: dispatch through interface tables (itable)

### 2. Inline caching and call-site stability

The JIT tracks receiver types at each call site:

- **monomorphic** (one receiver type): easiest to inline/devirtualize
- **polymorphic** (few types): may inline with guards
- **megamorphic** (many types): falls back to slower dispatch paths

In CPU profiles, megamorphic call sites often appear as:

- increased time in runtime dispatch machinery
- fewer inlining frames in JITWatch/async-profiler

### 3. Design implications

If a call is in a hot loop:

- consider sealing/final classes to stabilize receiver types
- keep polymorphism at system boundaries instead of per-element loops
- avoid unnecessary interface layers in critical inner loops

### 4. Debugging approach

When performance regresses:

- confirm polymorphism increased (e.g., new implementations introduced)
- inspect call-site stability with profiling data
- correlate with inlining decisions (JITWatch / `-XX:+PrintCompilation` for deeper work)

