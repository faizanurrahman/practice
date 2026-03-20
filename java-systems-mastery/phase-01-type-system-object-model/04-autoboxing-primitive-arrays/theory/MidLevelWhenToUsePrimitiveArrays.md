## Mid-level: when to use primitive arrays and streams

- **`int[]`, `long[]`** — dense numeric data, minimal overhead per element.
- **`List<Integer>`** — flexible API, but each element may be a boxed `Integer` object.
- Prefer **`IntStream`** / **`LongStream`** over `Stream<Integer>` when doing numeric pipelines.

**Caches:** `Integer.valueOf` caches small values (-128..127 by default) — do not rely on reference equality for boxed integers.
