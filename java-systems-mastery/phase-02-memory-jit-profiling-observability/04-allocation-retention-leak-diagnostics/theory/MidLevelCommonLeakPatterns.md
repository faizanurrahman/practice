## Mid-level: common “leak” patterns in Java

- **Static collections** that grow unbounded (cache without eviction).
- **ThreadLocal** values not removed in pooled threads (especially app servers).
- **Listeners** registered but never removed.
- **Class loader** retention (frameworks, OSGi-style plugins) holding metadata and classes.

Symptom: heap grows until **OutOfMemoryError** or chronic GC thrash.
