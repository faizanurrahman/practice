# 03 — Event Loop and Async

## Learning objectives

- Explain the **Call Stack**: where synchronous code runs; one thread; blocking.
- Distinguish **macrotasks** (e.g. `setTimeout` callbacks) and **microtasks** (e.g. `Promise.then`, `queueMicrotask`).
- Describe the **event loop** order: run until stack empty → run all microtasks → run one macrotask → repeat.
- Predict the output of code that mixes `setTimeout`, `Promise.then`, and `console.log`.

---

## Call Stack

JavaScript is **single-threaded**. The **Call Stack** holds the Execution Contexts for the code currently running. When you call a function, its EC is pushed; when it returns, it is popped. Only one EC is at the top at a time — that's what's "running." Long-running synchronous code blocks the stack and the thread.

---

## Task queues

When an asynchronous operation (e.g. `setTimeout`, `fetch`, `Promise`) completes, its callback is not run immediately on the stack. It is placed in a **queue**:

- **Macrotask queue (task queue):** Callbacks from `setTimeout`, `setInterval`, I/O, UI rendering (in browsers). The event loop takes **one** macrotask per cycle (after microtasks).
- **Microtask queue:** Callbacks from `Promise.then`/`catch`/`finally`, `queueMicrotask`, and in some environments `MutationObserver`. The event loop runs **all** microtasks after the current stack is empty, before the next macrotask.

---

## Event loop order

1. Execute the current synchronous code (run the Call Stack to empty).
2. Run **all** microtasks (drain the microtask queue).
3. If needed, do a render (browser).
4. Pick **one** macrotask from the task queue and run it (which may push more sync code and microtasks).
5. Go to step 2 (run all microtasks again), then 3, then 4, and so on.

So: **microtasks run before the next macrotask.** That's why `Promise.then` callbacks run before a `setTimeout(..., 0)` callback scheduled in the same turn.

---

## Senior scenario

"What is the output?" — Given a snippet that mixes `console.log`, `setTimeout(..., 0)`, and `Promise.resolve().then(...)`, predict the order. Example: sync logs first, then all microtask logs, then macrotask logs.

---

## Interview focus

- Draw the event loop: stack, macrotask queue, microtask queue, and the order of processing.
- Explain in one sentence why a microtask runs before the next macrotask: "The event loop drains the microtask queue after each macrotask (and after the initial script) before taking the next macrotask."

---

## Async patterns

The event loop explains *when* microtasks run. For *how* to structure async code — callbacks, Promises, async/await, and when to use each — see [07-async-patterns.md](07-async-patterns.md).

---

## Practice

- `practice/03-event-loop-order.js` — Predict the order of `console.log` with mixed `setTimeout`, `Promise.then`, `queueMicrotask`; then run to verify.
- `practice/03-event-loop-interview.js` — Short "what is the output?" snippet you can walk through in an interview.
