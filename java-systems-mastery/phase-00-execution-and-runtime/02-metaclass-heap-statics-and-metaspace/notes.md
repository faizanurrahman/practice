## Concept: Metaclass, heap, statics, and metaspace

### Mid-level

- `static` fields are per-class, not per-instance; accessed via the type name.
- `SomeClass.class` is the runtime mirror (`java.lang.Class`).

### Senior

- **Metaspace** holds metadata for classes loaded by each loader; **heap** holds instances and `Class` mirrors.
- Loader lifetime determines when metadata can be reclaimed.

### Staff

- Static collections and caches on long-lived classes → **GC root** behavior and memory leaks.
- Frameworks generating many classes → metaspace and native memory pressure.

### Checklist

- [ ] Explain where static field storage “lives” in the HotSpot mental model.
- [ ] Tie a metaspace leak to a class-loader retention story.

### Theory index

- [MidLevelStaticFieldsAndClassObjects.md](theory/MidLevelStaticFieldsAndClassObjects.md)
- [SeniorMetaspaceLifecycleAndOOM.md](theory/SeniorMetaspaceLifecycleAndOOM.md)

### Artifacts

- [RuntimeMemoryMapHelper.java](concepts/RuntimeMemoryMapHelper.java)

**Next:** [03-reflection-proxies-and-jit-interaction](../03-reflection-proxies-and-jit-interaction/notes.md)
