## Senior: heap dump → dominators → `Class` / static root

- **Heap dump** in MAT / JMC / VisualVM: find **dominators** — objects that retain large subgraphs.
- **GC roots** include static fields (via `Class` objects), threads, JNI, etc.
- **Staff narrative:** “This `ConcurrentHashMap` is retained by a static on `Foo.class` loaded by `AppClassLoader`” — connect dominator path to **ownership** and **fix** (eviction, scope, loader lifecycle).

Practice: take a small repro with a static `Map`, capture dump, walk path to root.
