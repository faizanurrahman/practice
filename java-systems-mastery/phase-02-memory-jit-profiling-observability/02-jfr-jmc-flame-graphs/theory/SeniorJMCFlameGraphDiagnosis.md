## Senior: JMC, flame graphs, and diagnosis workflow

**Suggested order**

1. Capture a **baseline** recording under representative load.
2. Compare against a **regression** window (same load shape).
3. Inspect **hot methods**, **allocation** hotspots, **lock** instances, **GC** pauses.
4. Apply **one** change at a time and re-measure.

**Flame graphs / CPU:** distinguish **CPU-bound** vs **allocation-bound** vs **blocked** threads (parking, I/O, locks).

**Keep GC tuning conceptual** unless the role is JVM/platform-heavy — first prove allocation and retention stories.
