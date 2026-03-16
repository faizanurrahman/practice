# Senior Angular Engineer Mastery

A progressive curriculum from **JavaScript engine** to **engineering leadership**. Each layer builds on the one below so you understand *why* Angular and the runtime behave the way they do — not just syntax.

## The pyramid

```
         +---------------------------------------------+
         |  7. Engineering Leadership                  |
         |     Mentoring, code reviews, ADRs, A11y    |
         +---------------------------------------------+
         |  6. Testing & Quality                       |
         |     Harnesses, e2e, testable design         |
         +---------------------------------------------+
         |  5. Performance                            |
         |     OnPush, trackBy, virtual scroll, CD    |
         +---------------------------------------------+
         |  4. Angular Architecture & State           |
         |     Components, NgRx, lazy load, DI        |
         +---------------------------------------------+
         |  3. Angular Runtime Internals              |
         |     Ivy, LView/TView, CD, Zone, DI tree    |
         +---------------------------------------------+
         |  2. Reactive Programming (RxJS)           |
         |     Observables, Subjects, operators        |
         +---------------------------------------------+
         |  1. JavaScript Engine                       |
         |     Call stack, memory, event loop, async  |
         +---------------------------------------------+
```

Study **from the bottom up**: Phase 1 first, then 2, then 3, and so on.

## Phase overview

| Phase | Focus | Where | Duration |
|-------|--------|-------|----------|
| **1** | JavaScript Engine — execution model, memory, event loop, prototypes, async patterns, performance fundamentals | [javascript/](javascript/) | 2–3 weeks |
| **2** | RxJS — Observable contract, Subjects, operators, error handling, schedulers | [angular/](angular/) (concept + practice) | 2 weeks |
| **3** | Angular internals — Ivy, TView/LView, change detection, DI, ViewContainerRef, Zone.js | [angular/](angular/) | 3 weeks |
| **4** | Architecture & state — component design, NgRx (real library), lazy loading, DI patterns | [angular/](angular/) | 2 weeks |
| **5** | Performance — OnPush, trackBy, virtual scroll, bundle, memory leaks | [angular/](angular/) | 1–2 weeks |
| **6** | Testing — Component Harnesses, e2e, testable design | [angular/](angular/) | 1 week |
| **7** | A11y & leadership — ARIA, focus, mentoring, ADRs | [angular/](angular/) | Ongoing |

## Recommended order

1. **Phase 1** — [javascript/README.md](javascript/README.md): run all concept modules (01–08) and practice files. You should be able to predict the output of any JS snippet and explain the event loop.
2. **Phase 2** — [angular/02-rxjs-deep-dive.md](angular/02-rxjs-deep-dive.md) and [angular/practice/02-rxjs-operators/](angular/practice/02-rxjs-operators/): install RxJS, run operators and Subjects demos.
3. **Phase 3** — [angular/01-angular-core-ivy.md](angular/01-angular-core-ivy.md) and practice (signals-vs-zone, trackby-virtual): understand Ivy, CD, and DI.
4. **Phase 4** — [angular/04-architecture-and-state.md](angular/04-architecture-and-state.md) and [angular/practice/02-ngrx-store/](angular/practice/02-ngrx-store/): install @ngrx/store, build a minimal app with actions, reducer, selector.
5. **Phase 5** — [angular/05-performance.md](angular/05-performance.md): optimise CD, lists, and bundles.
6. **Phase 6** — [angular/03-testing-and-accessibility.md](angular/03-testing-and-accessibility.md) and practice (component-harness, a11y-focus-trap).
7. **Phase 7** — A11y (in 03) and leadership (code review, ADRs) as you work.

## Phase 0 — Workspace and study system

Before starting Phase 1, set up your environment and study habit: [workspace/](workspace/). One Angular workspace, one JS playground, one TS playground, plus **mental-models.md** and **debug-log.md**. Rule for every topic: define in one line, draw the flow, run one tiny example, break it intentionally, explain why it broke.

## Quick links

- **Phase 0 (setup):** [workspace/](workspace/)
- **Phase 1 (JS):** [javascript/](javascript/)
- **Phases 2–7 (Angular):** [angular/](angular/)
