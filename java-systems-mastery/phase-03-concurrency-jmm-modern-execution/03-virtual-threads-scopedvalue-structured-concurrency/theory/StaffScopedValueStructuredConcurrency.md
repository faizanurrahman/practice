## Staff: ScopedValue and structured concurrency

- **ScopedValue** (final in modern JDKs): immutable, **lexically scoped** implicit context — safer than `ThreadLocal` for many propagation patterns; check your target JDK.
- **Structured concurrency** (preview status varies by JDK): treat a tree of tasks as one unit for **cancellation** and **failure propagation** — aligns with virtual-thread style “many tasks, clear boundaries.”
- **Staff decision:** virtual threads + ScopedValue vs thread pools vs reactive stack — justify with **blocking ratio**, **pinning risk**, **observability**, and **team familiarity**.

Always confirm **preview/final** status for the JDK you ship on.
