## Staff: when (if ever) `LinkedList`

- Most “I need inserts in the middle” workloads still profile better with **`ArrayList`** + batching or different structures (`TreeSet`, gap buffers, etc.).
- **`LinkedList`** can be appropriate for **deque** operations at ends with stable iterators — but **`ArrayDeque`** is often faster for queue/deque patterns.

**Staff answer:** choose by **measured** cache behavior and allocation, not Big-O alone.
