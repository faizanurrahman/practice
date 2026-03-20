## Staff: metaspace vs heap implications

**Where metadata lives**

- **Metaspace** (native, since Java 8): class metadata, method metadata, runtime structures tied to class loaders. Grows with loaded classes; can **Metaspace OOM** if loaders leak or code generates endless classes.
- **Heap**: object instances, arrays, and **`java.lang.Class` mirror objects** (and static field storage is tied to those mirrors in the HotSpot mental model).

**Design consequences**

- Frameworks that generate proxies, bytecode, or many class loaders → **metaspace pressure** and harder-to-reason class identity.
- **Static** collections on a class → GC roots via the `Class` object → **pinning** large graphs until the loader is collectible (often never in app loaders).

**Diagnostics**

- Correlate class-loading spikes with **native memory** / metaspace metrics and **heap** dominators separately.
- Class-init deadlocks show up as threads blocked in `<clinit>` — thread dumps + static init discipline.

**Next concepts:** bytecode/dispatch ([01-bytecode-constant-pool-invokedynamic-and-dispatch](../01-bytecode-constant-pool-invokedynamic-and-dispatch/notes.md)), then [02-metaclass-heap-statics-and-metaspace](../02-metaclass-heap-statics-and-metaspace/notes.md).
