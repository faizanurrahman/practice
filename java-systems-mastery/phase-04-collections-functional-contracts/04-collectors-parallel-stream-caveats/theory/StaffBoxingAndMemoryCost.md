## Staff: `collect()` boxing and memory cost

- **`Collectors.toList()`** returns **mutable `ArrayList`** (implementation detail — do not rely on mutability across JDKs for public APIs).
- **`groupingBy`** with large groups holds **full lists** in memory — consider **downstream reducing** collectors for scale.
- **Boxing:** `Stream<Integer>` + `summingInt` via wrong path may still autobox — prefer **`mapToInt`** pipelines.

**Staff:** for multi-GB aggregations, streams in memory may be wrong tool — consider DB or map-reduce.
