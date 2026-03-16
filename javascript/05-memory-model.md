# 05 — Memory Model: Stack, Heap, and Garbage Collection

## Learning objectives

- Explain where **primitives** vs **objects** live (stack vs heap).
- Describe **reference vs value** — what happens when you assign an object.
- Explain how **closures** retain memory and can cause leaks.
- Summarise **mark-and-sweep** garbage collection and when to avoid pitfalls.

---

## Stack vs heap

- **Stack:** Used for **primitive values** (numbers, strings, booleans, null, undefined, symbols, bigints) and **references** (pointers) to objects. Also holds **Execution Context** frames (local variables, return address). Fixed size per thread; fast allocation and deallocation (LIFO).
- **Heap:** Used for **objects** (including arrays, functions, and boxed primitives). Dynamically sized; the engine allocates space and **Garbage Collection (GC)** reclaims it when no references remain.

**Memory model:** When you declare `let x = 42`, the value `42` is stored (e.g. in the stack or an optimized slot). When you declare `let o = { a: 1 }`, the object `{ a: 1 }` lives in the heap; the variable `o` holds a **reference** (pointer) to that object.

---

## Reference vs value

- **Copy by value:** Primitives are copied. `let b = a` (where `a` is a number) gives `b` its own copy.
- **Copy by reference:** Assigning an object does **not** copy the object; it copies the reference. `let o2 = o1` means both `o1` and `o2` point to the same heap object. Mutating `o2.x = 2` is visible via `o1.x`.

**Performance implication:** Passing large objects to functions is cheap (only a reference is passed). Mutations inside the function affect the original; for immutability you copy (e.g. spread, `Object.assign`).

---

## Closures and memory retention

A **closure** keeps a reference to its **outer Lexical Environment**. As long as the closure is reachable (e.g. stored in a variable or attached to the DOM), that environment is **not** garbage-collected. So:

- **Intended:** Private state, memoization caches, event handlers that need outer scope.
- **Leak risk:** If you attach a closure to a long-lived object (e.g. DOM node, global store) and the closure holds a reference to a large object or another DOM node, that memory stays alive. Detach listeners and clear references when components are destroyed.

**Senior scenario:** A single-page app keeps a closure over a large cache in a service. The service is never torn down, so the cache is fine. But if the closure is attached to a DOM element that gets removed without removing the listener, the closure (and thus the cache) can outlive the view — or the other way around, the DOM can be retained by the closure.

---

## Garbage collection (brief)

- **Mark-and-sweep:** The GC traverses from **roots** (global object, current stack, etc.) and marks every reachable object. Then it sweeps (frees) everything that was not marked.
- **When to be careful:** Avoid holding unnecessary references to large structures (e.g. caches, DOM nodes) in closures that outlive their need. Use **weak references** (e.g. `WeakMap`, `WeakRef`) when you want the GC to be able to collect the key or value if nothing else references it.

---

## Interview focus

- "Where do primitives vs objects live?" — Primitives and references on the stack (or optimized slots); objects on the heap.
- "What happens when you assign an object to another variable?" — The reference is copied; both point to the same heap object.
- "Can closures cause memory leaks?" — Yes, if a closure holds a reference to a large or long-lived object and the closure itself is retained (e.g. by the DOM or a global).

---

## Practice

- [practice/05-memory-reference-leak.js](practice/05-memory-reference-leak.js) — Object assign and reference; closure holding a ref; optional WeakRef demo. Run with `node practice/05-memory-reference-leak.js`.
