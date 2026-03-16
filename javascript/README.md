# Phase 1: JavaScript Engine Mastery

**Goal:** Move from "knowing syntax" to understanding how the engine works — the Call Stack, Memory Heap, Execution Contexts, Lexical Environment, event loop, and async patterns. At senior level you explain *why* code behaves the way it does and when to use which async pattern.

## Recommended order

1. **01-execution-context-and-memory.md** — EC, creation/execution phase, hoisting, TDZ  
2. **02-closures-and-scope.md** — Closures, encapsulation, memoization, currying  
3. **03-event-loop-and-async.md** — Call stack, task queue, microtasks, execution order  
4. **04-types-equality-and-this.md** (optional) — `typeof`, `==` vs `===`, `this` binding  
5. **05-memory-model.md** — Stack vs heap, reference vs value, closures and GC  
6. **06-prototypes-and-inheritance.md** — `[[Prototype]]`, `this` rules, `class` sugar  
7. **07-async-patterns.md** — Callbacks, Promises, async/await, Observables preview, when to use each  
8. **08-performance-fundamentals.md** — Event delegation, debounce/throttle, reflow/repaint  

**Prerequisites:** Basic JavaScript syntax (variables, functions, conditionals).  
**Time:** ~2–3 weeks total (30–45 min per module read + practice).

## Module → main idea → interview angle

| Module | Main idea | Interview angle |
|--------|-----------|-----------------|
| 01 | Execution context, creation vs execution, lexical environment, hoisting | "Describe the two phases"; "Why does this throw?" (TDZ) |
| 02 | Closure = function + lexical environment; encapsulation, memoize, curry | "Implement a private counter"; "Implement memoize" |
| 03 | Event loop: stack → microtasks → one macrotask → repeat | "What is the output?" (setTimeout + Promise order) |
| 04 | `this` binding rules; `==` vs `===` | "What is `this` here?"; "When to use strict equality?" |
| 05 | Stack vs heap, reference vs value, closure retention, GC | "Where do objects live?"; "Can closures leak?" |
| 06 | [[Prototype]] chain, four rules of `this`, class sugar | "How does property lookup work?"; "What is class?" |
| 07 | Promises, async/await, Observables preview, when to use each | "Promise vs Observable?"; "When async/await vs RxJS?" |
| 08 | Event delegation, debounce/throttle, reflow/repaint | "What is event delegation?"; "Debounce vs throttle?" |

## Concept → practice map

| Concept file | Practice files | What to do |
|--------------|----------------|------------|
| 01-execution-context-and-memory.md | `practice/01-hoisting-and-tdz.js` | Predict output, then run; explain creation vs execution |
| 02-closures-and-scope.md | `practice/02-closure-private-counter.js`, `02-closure-memoize.js`, `02-closure-curry.js` | Implement from scratch; run and extend |
| 03-event-loop-and-async.md | `practice/03-event-loop-order.js`, `practice/03-event-loop-interview.js` | Predict order of logs; run to verify |
| 04-types-equality-and-this.md | `practice/04-this-binding.js`, `practice/04-equality-gotchas.js` | Run demos; explain each binding and gotcha |
| 05-memory-model.md | `practice/05-memory-reference-leak.js` | Run; explain reference vs value and closure retention |
| 06-prototypes-and-inheritance.md | `practice/04-this-binding.js`, `practice/06-prototype-chain.js` | Run this binding; run prototype/class demo |
| 07-async-patterns.md | `practice/07-promises-async-await.js`, `practice/07-observable-preview.js` | Run Promise/async and Observable preview |
| 08-performance-fundamentals.md | `practice/08-debounce-throttle.js` | Run; observe debounce vs throttle timing |

Run any practice file with: `node practice/<filename>.js` (from this directory).
