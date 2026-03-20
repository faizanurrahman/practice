## Staff: `HashMap` memory overhead

- **Buckets:** array of bins; each entry is a node (key, value, hash, next) — **pointer overhead** vs raw arrays.
- **Load factor / resize:** resizing rehashes all entries; **right-size** initial capacity when known.
- **Treeification:** extreme collisions degrade bin to tree — protects against worst-case **O(n)** on attacks or bad `hashCode`.

**Trade-off:** open-addressing (not in `HashMap`) vs chaining — interview: why JDK uses linked nodes + treeification threshold.
