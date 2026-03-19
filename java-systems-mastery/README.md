## Java Systems Mastery Roadmap

Goal: move beyond \"how to use Java\" into **why it works this way, when it breaks, and how to optimize it in production**, analogous to your JavaScript internals work.

### Phases
- **Phase 0 – Java execution mental model**: source -> bytecode -> class loading/linking -> execution -> heap/metaspace -> threads/stack -> JMM -> GC -> profiling workflow.
- **Phase 1 – Type system and object model**: primitives vs references, generics (erasure/bridges/wildcards), records/sealed classes, immutability, equals/hashCode/toString, builders, value vs entity.
- **Phase 2 – Bytecode, classloading, and runtime architecture**: class file structure, dispatch modes, invokedynamic/lambdas, reflection/proxies, metaspace/hidden classes (conceptual).
- **Phase 3 – Memory model, heap, metaspace, GC**: happens-before, safe publication, allocation paths, escape analysis, generations, safepoints, latency vs retention (conceptual).
- **Phase 4 – Collections and data-structure trade-offs**: List/Set/Map internals, HashMap/ConcurrentHashMap/ArrayList/LinkedList behavior, iteration guarantees, hashing/resize trade-offs.
- **Phase 5 – Concurrency primitives**: threads, monitors/synchronized, volatile, CAS/atomics (conceptually), contention, deadlocks and ordering.
- **Phase 6 – Task execution models**: ExecutorService, thread pools and queues, CompletableFuture, ForkJoin work stealing, virtual threads, cancellation/coordination.
- **Phase 7 – Functional Java and streams**: lambdas/capture, stream pipeline anatomy, collectors, laziness, boxing costs, parallel stream caveats.
- **Phase 8 – Robustness and contracts**: exceptions and cause preservation, null-handling strategy, API contracts/invariants, retryability/idempotency, testing strategy.
- **Phase 9 – Performance and profiling**: JIT warmup, inlining, deoptimization, allocation hotspots, lock contention, flame graphs, JFR/JMC workflows.
- **Phase 10 – Applied design and architecture**: stable public APIs, module boundaries (JPMS mental model), layering/dependency direction, error strategy across boundaries, observability integration.

Each `week-XX-topic/` contains:

```text
week-XX-topic/
├── notes.md     # mental models, war stories, gotchas
├── theory/      # deeper write-ups (JMM, GC, JIT, etc.)
├── examples/    # small experiments (javap, JFR, async-profiler output)
├── concepts/    # clean reference implementations
└── practice/    # exercises, often modeled on real incidents
```

