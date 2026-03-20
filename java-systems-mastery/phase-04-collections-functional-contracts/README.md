# Phase 04 — Collections, functional style, and robustness

**Merged from former Phase 4 + Phase 7 + Phase 8.** Lists/maps, concurrent collections, streams, collectors & parallel pitfalls, exceptions, contracts, and testing strategy.

| Order | Concept | Summary |
|------|---------|---------|
| 00 | [00-lists-arrays-linkedlist](00-lists-arrays-linkedlist/notes.md) | `ArrayList` vs `LinkedList`, growth, when to choose |
| 01 | [01-maps-sets-hashmap](01-maps-sets-hashmap/notes.md) | `HashMap`/`HashSet`, resize, fail-fast, memory |
| 02 | [02-concurrent-collections](02-concurrent-collections/notes.md) | `ConcurrentHashMap`, COW, iteration semantics |
| 03 | [03-streams-lambdas-functional-interfaces](03-streams-lambdas-functional-interfaces/notes.md) | Pipelines, lambdas, laziness, loop vs stream |
| 04 | [04-collectors-parallel-stream-caveats](04-collectors-parallel-stream-caveats/notes.md) | Collectors, custom collectors, parallel pool caveats |
| 05 | [05-exceptions-contracts-idempotency-testing](05-exceptions-contracts-idempotency-testing/notes.md) | Exceptions, retries, validation, contract tests |

**Media:** [Java_Collections__Senior_Guide.mp4](01-maps-sets-hashmap/supplementary/assets/Java_Collections__Senior_Guide.mp4) (local asset under `01-maps-sets-hashmap`).

**Optional / bonus:** [property-based testing](05-exceptions-contracts-idempotency-testing/supplementary/property-based-testing.md).

**Priority:** collections + contracts must know; streams should know.

**Legacy mapping:** `phase-4-collections` + `phase-7-functional-and-streams` + `phase-8-robustness-and-contracts`.
