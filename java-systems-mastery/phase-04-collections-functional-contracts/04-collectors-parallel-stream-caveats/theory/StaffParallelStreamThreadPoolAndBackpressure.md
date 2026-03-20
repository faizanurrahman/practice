## Parallel stream caveats

Parallel stream helps when:
- workload per element is meaningful,
- source splits efficiently,
- operation is CPU-bound and side-effect free.

It hurts when tasks are tiny, blocking, or synchronization-heavy.
