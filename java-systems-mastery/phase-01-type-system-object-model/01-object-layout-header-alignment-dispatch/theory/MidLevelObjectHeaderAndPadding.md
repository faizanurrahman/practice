## Mid-level: object header and padding (intuition)

- Every object has **overhead** beyond your fields: a **header** (mark word + class pointer on HotSpot) and **alignment** to 8-byte boundaries.
- **Arrays** add a length field; primitive arrays pack elements tightly; reference arrays store references.

**Why care:** collections of boxed `Integer` explode memory vs `int[]`.

**Measure:** JOL (Java Object Layout) on your JDK flags.
