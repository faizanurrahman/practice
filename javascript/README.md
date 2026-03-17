# Phase 1: JavaScript Engine Mastery

**Goal:** Move from "knowing syntax" to understanding how the engine works — the Call Stack, Memory Heap, Execution Contexts, Lexical Environment, event loop, and async patterns. At senior level you explain *why* code behaves the way it does and when to use which async pattern.

## Recommended order

1. **01-execution-context-and-memory.md** — EC, creation/execution phase, LexicalEnvironment/VariableEnvironment, `this` binding  
2. **02-closures-and-scope.md** — Scope chain, lexical environment, closures, encapsulation, memoization, currying  
3. **03-event-loop-and-async.md** — Intro to event loop: call stack, basic tasks vs microtasks, simple output prediction  
4. **04-hoisting-and-tdz.md** — Hoisting, TDZ, `var` vs `let`/`const`, function declarations vs expressions  
5. **04-types-equality-and-this.md** (optional) — `typeof`, `==` vs `===`, `this` binding basics  
6. **05-memory-model.md** — Stack vs heap, reference vs value, closures and GC  
7. **06-event-loop-tasks-microtasks.md** — Deep dive into tasks vs microtasks and precise ordering  
8. **07-ui-rendering-and-event-loop.md** — Where rendering fits in the loop; how scheduling affects UX  
9. **06-prototypes-and-inheritance.md** — `[[Prototype]]`, `this` rules, `class` sugar  
10. **07-async-patterns.md** — Promises and Async/Await: states, microtasks, chaining, async/await, sequential vs parallel (Promise.all/allSettled), pitfalls, debugging; Observables preview and when to use each  
11. **08-performance-fundamentals.md** — Event delegation, debounce/throttle, reflow/repaint  

**Prerequisites:** Basic JavaScript syntax (variables, functions, conditionals).  
**Time:** ~2–3 weeks total (30–45 min per module read + practice).

## Module → main idea → interview angle

| Module | Main idea | Interview angle |
|--------|-----------|-----------------|
| 01 | Execution context, creation vs execution, lexical environment, `this` binding | "Describe the two phases"; "What are LexicalEnvironment vs VariableEnvironment?" |
| 02 | Scope chain, lexical environment, closures, encapsulation, memoize, curry | "Implement a private counter"; "Implement memoize"; "Explain loop-with-var bug" |
| 03 | Event loop intro: stack → microtasks → one macrotask → repeat | "What is the output?" (setTimeout + Promise order) |
| 04 | Hoisting and TDZ: `var` vs `let`/`const`, function declarations vs expressions | "Why `var` is undefined but `let` throws?"; "Explain TDZ" |
| 04 (types) | `this` binding basics; `==` vs `===`; `typeof` edge cases | "What is `this` here?"; "When to use strict equality?" |
| 05 | Stack vs heap, reference vs value, closure retention, GC | "Where do objects live?"; "Can closures leak?" |
| 06 | Event loop deep dive: tasks vs microtasks, ordering, starvation | "Explain why Promise.then runs before setTimeout(0)"; "Diagnose microtask starvation" |
| 07 | Rendering and event loop: frames, long tasks, microtasks vs paint | "Why is the UI janky?"; "How to break work into chunks to keep 60fps?" |
| 06 (prototypes) | [[Prototype]] chain, four rules of `this`, class sugar | "How does property lookup work?"; "What is class?" |
| 07 (async patterns) | Promises and Async/Await: states, microtasks, chaining, sequential vs parallel, pitfalls, debugging; Observables preview | "Promise vs Observable?"; "When async/await vs RxJS?"; "Promise.all vs allSettled?" |
| 08 | Event delegation, debounce/throttle, reflow/repaint | "What is event delegation?"; "Debounce vs throttle?" |

## Concept → practice map

| Concept file | Practice files | What to do |
|--------------|----------------|------------|
| 01-execution-context-and-memory.md | `practice/01-hoisting-and-tdz.js` | Predict output, then run; explain creation vs execution, LexicalEnvironment vs VariableEnvironment |
| 02-closures-and-scope.md | `practice/02-closure-private-counter.js`, `02-closure-memoize.js`, `02-closure-curry.js`, `02-closure-loop-var.js` | Implement from scratch; run and extend; show loop-with-var vs let behaviour |
| 03-event-loop-and-async.md | `practice/03-event-loop-order.js`, `practice/03-event-loop-interview.js` | Predict order of logs; run to verify (intro level) |
| 04-hoisting-and-tdz.md | `practice/01-hoisting-and-tdz.js` | Focus on hoisting/TDZ examples; add your own function-expression cases |
| 04-types-equality-and-this.md | `practice/04-this-binding.js`, `practice/04-equality-gotchas.js` | Run demos; explain each binding and gotcha |
| 05-memory-model.md | `practice/05-memory-reference-leak.js` | Run; explain reference vs value and closure retention |
| 06-event-loop-tasks-microtasks.md | `practice/03-event-loop-order.js`, `practice/03-event-loop-interview.js` | Use the same scripts for deeper reasoning about tasks vs microtasks and starvation |
| 07-ui-rendering-and-event-loop.md | (browser DevTools) | Record performance traces; reason about long tasks and microtask impact on frames |
| 06-prototypes-and-inheritance.md | `practice/04-this-binding.js`, `practice/06-prototype-chain.js` | Run this binding; run prototype/class demo |
| 07-async-patterns.md | `practice/07-promises-async-await.js`, `practice/07-observable-preview.js` | Run Promise/async (includes microtask order, parallel vs sequential, Observables teaser) and full Observable preview |
| 08-performance-fundamentals.md | `practice/08-debounce-throttle.js` | Run; observe debounce vs throttle timing |

Run any practice file with: `node practice/<filename>.js` (from this directory).
