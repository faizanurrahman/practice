## Concept: JMM — happens-before and safe publication

**Canonical** JMM material for this curriculum.

### Theory index

- [MidLevelVolatileAndFinal.md](theory/MidLevelVolatileAndFinal.md)
- [SeniorHappensBeforeRules.md](theory/SeniorHappensBeforeRules.md)
- [StaffSafePublicationPatterns.md](theory/StaffSafePublicationPatterns.md)

### Pair with

- Heap / allocation: [Phase 02 — 00-heap](../../phase-02-memory-jit-profiling-observability/00-heap-metaspace-stack-tlabs-escape/notes.md)
- Locks and atomics (after JMM): [01-monitors-locks-atomics-contention](../01-monitors-locks-atomics-contention/notes.md)

---

## 1. Learning objectives

- Explain happens-before edges (`volatile`, locks, thread start/join, `final`).
- Design safe publication for immutable snapshots.
- Explain why `volatile` alone may be insufficient for multi-field publication.

---

## 2. Artifacts

- [SafePublicationFinalFields.java](concepts/SafePublicationFinalFields.java)
- [SafeToShareImmutabilityExercise.java](practice/SafeToShareImmutabilityExercise.java)
- [examples/README.md](examples/README.md)

---

## 3. Interview essentials

**Q:** What establishes visibility for `final` fields?  
**A:** Correct construction + **safe publication** (happens-before edge to readers).

**Q:** Data race vs bad publication?  
**A:** You can avoid races on individual fields yet publish a partially visible aggregate without proper hb ordering.

---

## 4. Summary

Master happens-before **before** virtual threads or lock tuning — visibility bugs do not disappear with more threads.

**Next:** [01-monitors-locks-atomics-contention](../01-monitors-locks-atomics-contention/notes.md)
