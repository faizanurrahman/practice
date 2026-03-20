## Mid-level: `synchronized` and intrinsic locks

- **`synchronized`** on a method or block uses the object’s **monitor** (or `Class` for static methods).
- One thread in the monitor ⇒ others **block**; exiting the monitor **releases** the lock.
- **`volatile` / happens-before:** see [00-jmm](../00-jmm-happens-before-safe-publication/theory/MidLevelVolatileAndFinal.md) first — this concept focuses on **mutual exclusion** and **atomic compound actions**.

**Common APIs:** `java.util.concurrent.locks` for try-lock, fairness, interruptible lock acquisition.
