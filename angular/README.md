# Phases 2–7: RxJS → Angular Internals → Architecture → Performance → Testing → A11y

**Goal:** From reactive programming (RxJS) through Angular runtime internals, architecture and state (including **real @ngrx/store**), performance, testing, and accessibility. At senior level you justify choices and debug framework behavior.

## Prerequisites

Complete **Phase 1** ([practice/javascript/](../javascript/)) first — execution context, closures, event loop, async patterns. You should be comfortable with the event loop and lexical scope.

## Phase table

| Phase | Focus | Concept | Practice |
|-------|--------|---------|----------|
| **2** | RxJS | [02-rxjs-deep-dive.md](02-rxjs-deep-dive.md) | [practice/02-rxjs-operators/](practice/02-rxjs-operators/) — operators + **subjects-demo.js** (install RxJS, run with Node) |
| **3** | Angular internals | [01-angular-core-ivy.md](01-angular-core-ivy.md) | [practice/01-signals-vs-zone/](practice/01-signals-vs-zone/), [practice/02-trackby-virtual/](practice/02-trackby-virtual/) |
| **4** | Architecture & state | [04-architecture-and-state.md](04-architecture-and-state.md) | [practice/02-ngrx-minimal/](practice/02-ngrx-minimal/) (vanilla pattern), [practice/02-ngrx-store/](practice/02-ngrx-store/) (**real @ngrx/store**: `npm install`, `ng serve`) |
| **5** | Performance | [05-performance.md](05-performance.md) | 01-signals-vs-zone, 02-trackby-virtual; optional bundle analysis |
| **6** | Testing | [03-testing-and-accessibility.md](03-testing-and-accessibility.md) | [practice/03-component-harness/](practice/03-component-harness/) |
| **7** | A11y | 03 (same doc) | [practice/03-a11y-focus-trap/](practice/03-a11y-focus-trap/) |

**Install and run:** RxJS demos use `npm install rxjs` in `practice/02-rxjs-operators/` and `node operators-demo.js` / `node subjects-demo.js`. NgRx uses the real library in `practice/02-ngrx-store/`: run `npm install` and `ng serve` in that folder to see actions, reducer, selector, and Store in a minimal Angular app.

## Recommended order

1. **02-rxjs-deep-dive.md** — Observable contract, Subjects, operators, error handling, schedulers. Then run `practice/02-rxjs-operators/` (operators-demo.js, subjects-demo.js).
2. **01-angular-core-ivy.md** — Ivy, LView/TView, change detection, DI, ViewContainerRef, Zone.js. Then practice/01-signals-vs-zone and 02-trackby-virtual.
3. **02-rxjs-ngrx-performance.md** — Four flattening operators, NgRx pattern, trackBy, virtualization (quick reference).
4. **04-architecture-and-state.md** — Component design, state (NgRx, ComponentStore, Signals), lazy load, DI, side effects. Then practice/02-ngrx-minimal (vanilla) and **02-ngrx-store** (real @ngrx/store).
5. **05-performance.md** — CD optimisations, trackBy/virtual, bundle, memory leaks.
6. **03-testing-and-accessibility.md** — Harnesses, A11y (ARIA, FocusTrap). Then practice/03-component-harness and 03-a11y-focus-trap.

**Time:** ~45–60 min per concept (read + practice); Phases 2–7 span several weeks if done in depth.

## Concept → practice map

| Concept file | Practice | What to do |
|--------------|----------|------------|
| 01-angular-core-ivy.md | practice/01-signals-vs-zone/, 02-trackby-virtual/ | Signal vs zone counter; *ngFor + trackBy, virtual scroll |
| 02-rxjs-deep-dive.md | practice/02-rxjs-operators/ | Run operators-demo.js and subjects-demo.js (Node + RxJS) |
| 02-rxjs-ngrx-performance.md | practice/02-rxjs-operators/, 02-ngrx-minimal/, 02-trackby-virtual/ | Same RxJS; vanilla store pattern; trackBy |
| 04-architecture-and-state.md | practice/02-ngrx-minimal/, **practice/02-ngrx-store/** | Vanilla store; **real NgRx app** (npm install, ng serve) |
| 05-performance.md | 01-signals-vs-zone, 02-trackby-virtual | CD and list optimisations; optional bundle analysis |
| 03-testing-and-accessibility.md | practice/03-component-harness/, practice/03-a11y-focus-trap/ | Harness spec; modal with FocusTrap and ARIA |

See each `practice/*/README.md` for how to run (e.g. `ng serve`, `ng test`, `node *.js`).
