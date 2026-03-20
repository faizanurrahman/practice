# Phase 00 — Execution and runtime architecture

**Merged from former Phase 0 + Phase 2.** Runtime-first backbone: source → bytecode → class loading → dispatch → metaspace/heap → reflection/JIT boundaries.

| Order | Concept | Summary |
|------|---------|---------|
| 00 | [00-pipeline-source-to-bytecode-and-class-loading](00-pipeline-source-to-bytecode-and-class-loading/notes.md) | `javac` / `java`, class loading, linking, init, metaspace vs heap |
| 01 | [01-bytecode-constant-pool-invokedynamic-and-dispatch](01-bytecode-constant-pool-invokedynamic-and-dispatch/notes.md) | `javap`, constant pool, invocation bytecodes, lambdas |
| 02 | [02-metaclass-heap-statics-and-metaspace](02-metaclass-heap-statics-and-metaspace/notes.md) | Statics, `Class` mirrors, metaspace lifecycle |
| 03 | [03-reflection-proxies-and-jit-interaction](03-reflection-proxies-and-jit-interaction/notes.md) | Reflection, dynamic proxies, JIT/inlining cost |

**Priority:** must know.

**Legacy mapping:** `phase-0-java-execution-mental-model` + `phase-2-bytecode-classloading-runtime`.
