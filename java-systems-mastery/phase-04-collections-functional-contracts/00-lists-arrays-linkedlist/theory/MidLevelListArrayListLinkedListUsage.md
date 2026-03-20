## Mid-level: `List`, `ArrayList`, `LinkedList`

- **`ArrayList`** — dynamic array; **random access O(1)**; amortized **append O(1)**; **insert/remove in middle O(n)** due to shifting.
- **`LinkedList`** — node chain; **no** cheap random access; insert/remove **at known node** O(1) but finding index is O(n). Rarely wins vs `ArrayList` in real JVM code due to pointer chasing and cache misses.

**Default choice:** `ArrayList` unless you have a deque-specific API need (`Deque` implementations often preferable to `LinkedList`).
