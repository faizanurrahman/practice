## Java Systems Mastery Roadmap

Goal: move beyond \"how to use Java\" into **why it works this way, when it breaks, and how to optimize it in production**, analogous to your JavaScript internals work.

### Phases

- **Phase 1 – Language & OO internals**: object model, value vs entity, immutability, equals/hashCode, records/sealed classes.
- **Phase 2 – Collections & JVM**: collections internals, Big-O, JMM basics, GC, TLABs.
- **Phase 3 – Concurrency & parallelism**: threads, locks, JMM, executors, virtual threads, work stealing.
- **Phase 4 – Functional & streams**: lambdas, functional interfaces, streams, collectors, performance trade-offs.
- **Phase 5 – Robustness & testing**: exceptions, contracts, null-handling, property-based tests.
- **Phase 6 – Performance & profiling**: JIT, escape analysis, profiling, flame graphs, GC tuning stories.
- **Phase 7 – Design & architecture**: API evolution, modules, observability, library design.

Each `week-XX-topic/` contains:

```text
week-XX-topic/
├── notes.md     # mental models, war stories, gotchas
├── theory/      # deeper write-ups (JMM, GC, JIT, etc.)
├── examples/    # small experiments (javap, JFR, async-profiler output)
├── concepts/    # clean reference implementations
└── practice/    # exercises, often modeled on real incidents
```

