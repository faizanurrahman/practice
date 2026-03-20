## Concept: JPMS, modules, encapsulation

### Mid-level — daily use

- Read and write `module-info.java`: `requires`, `exports`, when to use `opens`.
- Keep **dependency direction** pointing inward: domain stable, adapters replaceable.

### Senior — internals / build graph

- Avoid module cycles; understand `requires transitive` and migration from classpath.

### Staff — trade-offs

- Encapsulation as **organizational** boundary: who may depend on what, and how to evolve modules without breaking teams.

### Checklist

- Can you draw your service’s module/layer graph and justify each `exports`?
- Where would you use `opens` vs splitting a module?

### Theory index

- [MidLevelModuleRequiresExports.md](theory/MidLevelModuleRequiresExports.md)
- [SeniorJpmsLayeringAndCycles.md](theory/SeniorJpmsLayeringAndCycles.md)
- [StaffJpmsEncapsulationAndDependencyDirection.md](theory/StaffJpmsEncapsulationAndDependencyDirection.md)

**Next:** [01-api-evolution-compatibility](../01-api-evolution-compatibility/notes.md)
