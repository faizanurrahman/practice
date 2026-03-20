## Senior: `ArrayList` growth and memory

- **Growth strategy:** when full, capacity increases (typically ~1.5×); elements copied to new backing array — **occasional large copy** on growth.
- **Memory:** `capacity >= size`; trimming with `trimToSize()` after bulk load saves footprint.
- **vs array:** `ArrayList` adds indirection + `size` field; still far better locality than `LinkedList`.

**Interview:** why repeated `add(0, x)` on `ArrayList` is pathological.
