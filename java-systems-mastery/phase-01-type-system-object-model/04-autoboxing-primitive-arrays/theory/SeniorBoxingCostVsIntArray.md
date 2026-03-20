## Senior: boxing cost vs `int[]`

- Each `Integer` is an object: header + field + alignment vs 4 bytes in `int[]`.
- Autoboxing in loops (`sum += i` on `Integer`) allocates unless eliminated by escape analysis (do not rely on it in hot paths).
- **Collections:** `ArrayList<Integer>` holds references; GC scans more pointers; worse cache locality than primitive arrays.

**Measure:** allocation rate in JFR when debating `List<Integer>` vs `int[]` for inner loops.
