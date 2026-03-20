# Phase 01 — Type system and object model

Physical layout, modern types (records/sealed/patterns), generics internals, contracts, and boxing footprint.

| Order | Concept | Summary |
|------|---------|---------|
| 00 | [00-records-sealed-pattern-matching](00-records-sealed-pattern-matching/notes.md) | Records, sealed hierarchies, pattern `switch` |
| 01 | [01-object-layout-header-alignment-dispatch](01-object-layout-header-alignment-dispatch/notes.md) | Header, alignment, dispatch, constructors, immutability |
| 02 | [02-generics-erasure-bridge-methods](02-generics-erasure-bridge-methods/notes.md) | PECS, erasure, bridges, heap pollution, reflection |
| 03 | [03-value-entity-immutability-equals](03-value-entity-immutability-equals/notes.md) | equals/hashCode, value vs entity, builders |
| 04 | [04-autoboxing-primitive-arrays](04-autoboxing-primitive-arrays/notes.md) | `int[]` vs `List<Integer>`, streams |

**Priority:** must know.

**Legacy mapping:** former `phase-1-type-system-object-model`.
