## Mid-level: `Collectors.groupingBy`, `partitioningBy`

- **`groupingBy(classifier)`** → `Map<K, List<T>>`; overloads with **downstream collector** (e.g. counting, summing).
- **`partitioningBy(predicate)`** → `Map<Boolean, List<T>>` — two buckets only.
- **Mind the map value type:** downstream collector determines **mutability** and **merge** behavior.

**Common bug:** wrong downstream collector causing **unexpected map value type** or **mutable shared** structures.
