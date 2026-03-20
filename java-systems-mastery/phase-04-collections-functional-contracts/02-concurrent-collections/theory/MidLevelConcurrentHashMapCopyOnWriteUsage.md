## Mid-level: `ConcurrentHashMap` and `CopyOnWriteArrayList`

- **`ConcurrentHashMap`** — concurrent reads/writes with **segmented/bucket** locking (JDK-version-specific details); **do not** use `null` keys/values.
- **Atomic composite operations** like `compute`, `merge`, `putIfAbsent` — understand **retry** behavior under contention.
- **`CopyOnWriteArrayList`** — **copy-on-write** on mutation; **read-heavy, rare writes**; iterators are **snapshot** (no CME, weak consistency).

**Wrong tool:** COW for write-heavy lists — quadratic copying.
