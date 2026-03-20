## Mid-level: GC generations (intuition)

- Most collectors are **generational**: objects that die young are collected cheaply in the **young** generation.
- Objects that live longer are **promoted** to the **old** generation; old-gen collections are typically more expensive.
- **Pause** length often rises when old generation fills or when allocation rate exceeds what the young collector can keep up with.

**You are not tuning yet** — you are learning vocabulary for logs and JFR GC views.
