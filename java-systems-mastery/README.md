# Java Systems Mastery

**Audience:** senior → lead / staff engineers preparing for depth interviews and production JVM reasoning.

**Design:** **runtime-first** curriculum. You navigate **`java-systems-mastery/` → phase → concept slug** (there are **no** `week-*` folders).

**Philosophy (three layers):**

1. **Language** — what source code expresses (types, generics, records, exceptions).
2. **VM / runtime** — bytecode, class loading, dispatch, JIT, heap, GC, JMM, threads.
3. **Design** — boundaries, modules, APIs, observability, trade-offs.

---

## Repository layout (top level)

Everything lives under this folder:

```text
java-systems-mastery/
├── README.md                    ← you are here: map + conventions + full file index
├── phase-00-execution-and-runtime/
│   └── README.md                ← ordered list of concepts in this phase + links
├── phase-01-type-system-object-model/
│   └── README.md
├── phase-02-memory-jit-profiling-observability/
│   └── README.md
├── phase-03-concurrency-jmm-modern-execution/
│   └── README.md
├── phase-04-collections-functional-contracts/
│   └── README.md
└── phase-05-applied-design-architecture/
    └── README.md
```

Each **`phase-NN-.../`** directory contains:

- **`README.md`** — teaching order, one line per concept, links to `notes.md`.
- **One folder per concept** — name pattern `NN-short-slug` (e.g. `01-object-layout-header-alignment-dispatch`). The numeric prefix is **teaching order within that phase**, not a calendar week.

---

## Standard concept folder (what every concept should contain)

Each **`<concept-slug>/`** is a self-contained mini-module:

| Path | Role | Typical contents |
|------|------|-------------------|
| **`notes.md`** | Main narrative | Learning objectives, mental models, mechanisms, pitfalls, interview Q&A, summary. **Start here.** |
| **`theory/`** | Focused deep dives | One topic per `.md` file (e.g. `TypeErasureAndSyntheticCasts.md`). ASCII-friendly; link to JVM spec / JEPs in text. |
| **`concepts/`** | Reference-style Java | Small, clean types or utilities meant to be read as “the right shape” (e.g. immutable value object, PECS helper). |
| **`examples/`** | Runnable demos | `main` or obvious entry points; illustrate one idea (dispatch, lambda bytecode, volatile visibility). |
| **`practice/`** | Exercises | Pitfalls, fixes, or “explain the output” drills; often interview-shaped. |
| **`supplementary/README.md`** | Resource index | Curated links, reading order, and pointers to local media. |
| **`supplementary/assets/`** | Binary media (optional) | `.mp4`, `.pdf` — keep **out of Git** or use Git LFS if the repo policy requires it. |

**Naming conventions:**

- **Theory:** `PascalCaseOrDescriptiveTopic.md` under `theory/`.
- **Java:** one public top-level class per file, file name matches class name (`FooBar.java`).
- **Stubs:** some concepts use `theory/Overview.md` plus a short `notes.md` until content is expanded.

**Gaps are OK temporarily:** a concept might have empty `examples/` or only `theory/Overview.md` while stubs are filled in. Prefer adding at least `notes.md` + `supplementary/README.md` everywhere.

---

## Phase map (six merged tracks)

| Phase | Directory | Focus | Priority |
|-------|-----------|--------|----------|
| 00 | [phase-00-execution-and-runtime](phase-00-execution-and-runtime/README.md) | Source → bytecode → class loading → dispatch → reflection / JIT cost | Must know |
| 01 | [phase-01-type-system-object-model](phase-01-type-system-object-model/README.md) | Layout, generics, modern types, contracts, footprint | Must know |
| 02 | [phase-02-memory-jit-profiling-observability](phase-02-memory-jit-profiling-observability/README.md) | Heap, TLABs, escape, GC intuition, JIT, JFR/JMC | Must know |
| 03 | [phase-03-concurrency-jmm-modern-execution](phase-03-concurrency-jmm-modern-execution/README.md) | **Canonical JMM** first, locks/atomics, executors/ForkJoin/CF, virtual threads / ScopedValue | Must know |
| 04 | [phase-04-collections-functional-contracts](phase-04-collections-functional-contracts/README.md) | Collections internals, streams, exceptions, contracts | Must / should |
| 05 | [phase-05-applied-design-architecture](phase-05-applied-design-architecture/README.md) | JPMS, API evolution, architecture trade-offs, observability boundaries | Must know (staff) |

**Legacy folder numbering** (if you see old notes or bookmarks):  
`phase-0` + `phase-2` → **00** · `phase-1` → **01** · `phase-3` + `phase-9` → **02** · `phase-5` + `phase-6` → **03** · `phase-4` + `phase-7` + `phase-8` → **04** · `phase-10` → **05**.

---

## JMM single source of truth

**Happens-before, safe publication, `final` fields:** canonical material lives in  
**[phase-03-concurrency-jmm-modern-execution / 00-jmm-happens-before-safe-publication](phase-03-concurrency-jmm-modern-execution/00-jmm-happens-before-safe-publication/notes.md)**.

Phase **02** (heap / GC / JIT) **links** there instead of duplicating full JMM narratives.

---

## Full structure and file names (inventory)

Below is the **current** tree of markdown + Java (+ listed media). Use it to jump directly to a file.

### `phase-00-execution-and-runtime/`

```text
phase-00-execution-and-runtime/
├── README.md
├── 00-pipeline-source-to-bytecode-and-class-loading/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelCompilationAndRunning.md
│   │   ├── SeniorClassLoadingLinkingInitialization.md
│   │   └── StaffMetaspaceVsHeapImplications.md
│   ├── examples/ClassInitOrderDemo.java
│   ├── practice/StaticLeakExercise.java
│   └── supplementary/
│       ├── README.md
│       └── assets/                    # optional local media
│           ├── Java_Execution_Model.mp4
│           ├── Java_s_Hidden_Machinery.mp4
│           ├── The_JIT_Compiler.mp4
│           └── The_JVM_Matrix.pdf
├── 01-bytecode-constant-pool-invokedynamic-and-dispatch/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelBytecodeReadingWithJavap.md
│   │   ├── SeniorInvokeVirtualInterfaceAndVtable.md
│   │   └── StaffInvokedynamicLambdaCost.md
│   ├── concepts/BytecodePatternCatalog.java
│   ├── examples/LambdaInvokeDynamicExample.java
│   ├── practice/InspectAndPredictDispatch.java
│   └── supplementary/README.md
├── 02-metaclass-heap-statics-and-metaspace/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelStaticFieldsAndClassObjects.md
│   │   └── SeniorMetaspaceLifecycleAndOOM.md
│   ├── concepts/RuntimeMemoryMapHelper.java
│   └── supplementary/README.md
└── 03-reflection-proxies-and-jit-interaction/
    ├── notes.md
    ├── theory/
    │   ├── MidLevelReflectionUsage.md
    │   ├── SeniorProxyBytecodeAndCost.md
    │   └── StaffWhyReflectionBreaksInlining.md
    ├── examples/DynamicProxyDemo.java
    └── supplementary/README.md
```

### `phase-01-type-system-object-model/`

```text
phase-01-type-system-object-model/
├── README.md
├── 00-records-sealed-pattern-matching/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelRecordsAndCompactConstructors.md
│   │   ├── SeniorSealedTypesAndPatternMatching.md
│   │   └── StaffPerformanceWithEscapeAnalysis.md
│   └── supplementary/README.md
├── 01-object-layout-header-alignment-dispatch/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelObjectHeaderAndPadding.md
│   │   ├── SeniorMarkWordKlassPointerAndLocking.md
│   │   └── StaffFieldReorderingAndAlignment.md
│   ├── concepts/SafePublishedImmutableValueObject.java
│   ├── examples/ObjectLayoutAndDispatchExample.java
│   ├── practice/ConstructorOverridableLeakPitfall.java
│   └── supplementary/README.md
├── 02-generics-erasure-bridge-methods/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelGenericsAndPECS.md
│   │   ├── SeniorTypeErasureBridgeMethods.md
│   │   └── StaffHeapPollutionAndReflection.md
│   ├── concepts/PeCSCopyUtil.java
│   ├── examples/BridgeMethodExample.java
│   ├── practice/HeapPollutionPitfall.java
│   └── supplementary/README.md
├── 03-value-entity-immutability-equals/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelEqualsHashCodeContracts.md
│   │   ├── SeniorValueVsEntityAndIdentity.md
│   │   └── StaffBuildersImmutabilityAndAPICost.md
│   ├── concepts/ValueObjectEqualsHashCode.java
│   ├── examples/BuilderVsConstructorExample.java
│   ├── practice/EqualsHashCodePitfallFix.java
│   └── supplementary/README.md
└── 04-autoboxing-primitive-arrays/
    ├── notes.md
    ├── theory/
    │   ├── MidLevelWhenToUsePrimitiveArrays.md
    │   └── SeniorBoxingCostVsIntArray.md
    └── supplementary/README.md
```

### `phase-02-memory-jit-profiling-observability/`

```text
phase-02-memory-jit-profiling-observability/
├── README.md
├── 00-heap-metaspace-stack-tlabs-escape/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelStackVsHeap.md
│   │   ├── SeniorEscapeAnalysisScalarReplacement.md
│   │   └── StaffTLABAndPromotionPressure.md
│   ├── examples/AllocationAndEscapeSmokeTest.java
│   └── supplementary/README.md
├── 01-jit-tiers-warmup-inlining-deopt/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelJITWarmupConcepts.md
│   │   ├── SeniorC1C2InliningDeoptimisation.md
│   │   └── StaffOSRAndAOTProfiling.md
│   ├── concepts/ProfilingChecklist.java
│   ├── examples/AllocationHotspotDemo.java
│   ├── practice/LatencyRegressionTriageExercise.java
│   └── supplementary/README.md
├── 02-jfr-jmc-flame-graphs/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelJFRRecording.md
│   │   ├── SeniorJMCFlameGraphDiagnosis.md
│   │   └── StaffRecordingOverheadAndRetention.md
│   └── supplementary/README.md
├── 03-gc-g1-zgc-symptom-driven/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelGCGenerations.md
│   │   └── SeniorG1VsZGCTradeoffs.md
│   └── supplementary/README.md
└── 04-allocation-retention-leak-diagnostics/
    ├── notes.md
    ├── theory/
    │   ├── MidLevelCommonLeakPatterns.md
    │   └── SeniorHeapDumpToClassObjectPath.md
    └── supplementary/README.md
```

### `phase-03-concurrency-jmm-modern-execution/`

```text
phase-03-concurrency-jmm-modern-execution/
├── README.md
├── 00-jmm-happens-before-safe-publication/    ← canonical JMM
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelVolatileAndFinal.md
│   │   ├── SeniorHappensBeforeRules.md
│   │   └── StaffSafePublicationPatterns.md
│   ├── concepts/SafePublicationFinalFields.java
│   ├── examples/README.md
│   ├── practice/SafeToShareImmutabilityExercise.java
│   └── supplementary/README.md
├── 01-monitors-locks-atomics-contention/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelSynchronizedUsage.md
│   │   ├── SeniorBiasedLightHeavyLocksAndCAS.md
│   │   └── StaffFalseSharingAndContention.md
│   ├── concepts/CounterWithAtomicAndLock.java
│   ├── examples/VolatileVisibilityExample.java
│   ├── practice/DeadlockDiagnosisExercise.java
│   └── supplementary/README.md
├── 02-executors-forkjoin-completablefuture/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelThreadPoolSizing.md
│   │   ├── SeniorRejectionPoliciesWorkStealing.md
│   │   └── StaffCompletableFutureComposition.md
│   ├── concepts/PoolSelectionMatrix.java
│   ├── examples/CompletableFutureFailurePropagation.java
│   ├── practice/ThreadPoolOverloadExercise.java
│   └── supplementary/README.md
└── 03-virtual-threads-scopedvalue-structured-concurrency/
    ├── notes.md
    ├── theory/
    │   ├── MidLevelVirtualThreadsUsage.md
    │   ├── SeniorPinningAndCarrierThreads.md
    │   └── StaffScopedValueStructuredConcurrency.md
    └── supplementary/README.md
```

### `phase-04-collections-functional-contracts/`

```text
phase-04-collections-functional-contracts/
├── README.md
├── 00-lists-arrays-linkedlist/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelListArrayListLinkedListUsage.md
│   │   ├── SeniorArrayListGrowthAndMemory.md
│   │   └── StaffWhenToChooseLinkedList.md
│   ├── examples/ArrayListVsLinkedListBehavior.java
│   └── supplementary/README.md
├── 01-maps-sets-hashmap/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelMapSetUsage.md
│   │   ├── SeniorHashMapResizeTreeificationCollision.md
│   │   ├── SeniorFailFastIteratorsAndMutationSafety.md
│   │   └── StaffHashMapMemoryOverhead.md
│   ├── concepts/MiniHashMapResizeSimulator.java
│   ├── practice/AdversarialHashMapCollisionTreeificationDemo.java
│   └── supplementary/
│       ├── README.md
│       └── assets/Java_Collections__Senior_Guide.mp4
├── 02-concurrent-collections/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelConcurrentHashMapCopyOnWriteUsage.md
│   │   ├── SeniorConcurrentHashMapSegmentsBuckets.md
│   │   └── StaffCopyOnWriteVsCHMTradeoffs.md
│   └── supplementary/README.md
├── 03-streams-lambdas-functional-interfaces/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelStreamPipelineAndLambdas.md
│   │   ├── SeniorLambdaCaptureBytecodeAndInvokedynamic.md
│   │   ├── StaffLazinessAndShortCircuiting.md
│   │   └── StaffWhenForLoopBeatsStream.md
│   ├── concepts/LoopVsStreamHeuristic.java
│   ├── examples/BoxingCostExample.java
│   ├── practice/StreamDebuggingExercise.java
│   └── supplementary/README.md
├── 04-collectors-parallel-stream-caveats/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelCollectorsGroupingPartitioning.md
│   │   ├── SeniorCollectorDesignAndCustom.md
│   │   ├── StaffParallelStreamThreadPoolAndBackpressure.md
│   │   └── StaffBoxingAndMemoryCost.md
│   ├── practice/ParallelReductionBugExercise.java
│   └── supplementary/README.md
└── 05-exceptions-contracts-idempotency-testing/
    ├── notes.md
    ├── theory/
    │   ├── MidLevelCheckedUncheckedAndOptional.md
    │   ├── SeniorDomainExceptionDesignAndCause.md
    │   └── StaffIdempotencyRetryAndContractTesting.md
    ├── concepts/ValidationBoundaryExample.java
    ├── examples/CausePreservationExample.java
    ├── practice/RetryClassificationExercise.java
    └── supplementary/
        ├── README.md
        └── property-based-testing.md    ← optional / bonus only
```

### `phase-05-applied-design-architecture/`

```text
phase-05-applied-design-architecture/
├── README.md
├── 00-jpms-modules-encapsulation/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelModuleRequiresExports.md
│   │   ├── SeniorJpmsLayeringAndCycles.md
│   │   └── StaffJpmsEncapsulationAndDependencyDirection.md
│   └── supplementary/README.md
├── 01-api-evolution-compatibility/
│   ├── notes.md
│   ├── theory/
│   │   ├── MidLevelAdditiveApiChanges.md
│   │   ├── SeniorApiEvolutionAndCompatibility.md
│   │   └── StaffApiCompatibilityGovernance.md
│   ├── examples/BackwardCompatibleApiExample.java
│   └── supplementary/README.md
└── 02-architecture-tradeoffs-observability/
    ├── notes.md
    ├── theory/
    │   ├── StaffConcurrencyModelSelectionAtBoundaries.md
    │   ├── StaffObservabilityAndJfrBoundaries.md
    │   └── StaffSystemDesignTradeoffRubric.md
    ├── concepts/ArchitectureDecisionRecordTemplate.java
    ├── practice/ArchitectureInterviewPromptExercise.java
    └── supplementary/README.md
```

---

## Study flow (interview speed)

1. **Phase 00 → 01** — execution pipeline + type/object model.
2. **Phase 02** — allocation, GC symptoms, profiling; use **Phase 03 / 00-jmm** when the bug is visibility/publication.
3. **Phase 03** — **JMM first**, then locks/atomics, executors, virtual threads / ScopedValue (confirm JDK maturity for previews).
4. **Phase 04** — lists/maps, concurrent collections, streams, collectors, robust contracts.
5. **Phase 05** — JPMS, API evolution, architecture + observability.

**Optional / role-dependent:** reactive stacks (not tracked as core here); [property-based testing](phase-04-collections-functional-contracts/05-exceptions-contracts-idempotency-testing/supplementary/property-based-testing.md); deep GC tuning only for JVM-heavy roles.

---

## Running the Java snippets

Examples and practice classes are **standalone** `*.java` files (default package) unless a file declares a package (most do not).

From the directory that contains the file:

```bash
javac YourFile.java && java YourClassName
```

If you use an IDE, open the repo root or the specific `concepts/` / `examples/` / `practice/` folder as a source root and run the `main` method.

---

## Maintaining this README

When you add a concept or rename files:

1. Update the **phase `README.md`** for that phase.
2. Update the **inventory section** above (or regenerate from `find` / a small script).
3. Keep **binary assets** under `supplementary/assets/` and document them in `supplementary/README.md`.
