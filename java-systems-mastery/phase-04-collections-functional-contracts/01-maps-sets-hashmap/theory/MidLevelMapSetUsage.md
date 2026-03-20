## Mid-level: `Map`, `Set`, `HashMap`, `HashSet`

- **`HashMap`** — key → value, **average O(1)** get/put; **no iteration order** guarantee (until linked hash variants).
- **`HashSet`** — backed by `HashMap`; uniqueness by `hashCode` + `equals`.
- **Fail-fast iterators:** if structurally modified (except via iterator’s own `remove`), **ConcurrentModificationException** — not thread safety, just early detection.

**Contracts:** `equals`/`hashCode` consistent for keys; **mutable keys** are dangerous.
