# 07 — Asynchronous Patterns

## Learning objectives

- Compare **callbacks**, **Promises**, **async/await**, and **Observables** (preview).
- Explain Promise states, chaining, and error handling; link to microtasks (see [03-event-loop-and-async.md](03-event-loop-and-async.md)).
- Describe **async/await** as syntactic sugar and how errors propagate.
- Know **when to use each** — a senior engineer's decision guide.

---

## Callbacks

- Pass a function to be invoked when an async operation completes (e.g. `setTimeout(cb, ms)`, Node-style `fs.readFile(path, cb)`).
- **Problems:** Callback hell (nested callbacks); inversion of control; error handling is manual (convention: first argument is error); no built-in cancellation or composition.

---

## Promises

- A **Promise** represents a future value (or rejection). States: pending → fulfilled or rejected. Once settled, state does not change.
- **Chaining:** `.then(onFulfilled, onRejected)` returns a new Promise; you can chain. `.catch(fn)` is `.then(null, fn)`.
- **Error handling:** Rejections propagate down the chain until a `.catch()` or an `onRejected` handles them. Unhandled rejections can trigger the environment's unhandled-rejection behavior.
- **Microtasks:** `.then`/`.catch`/`.finally` callbacks are scheduled as **microtasks** (see [03-event-loop-and-async.md](03-event-loop-and-async.md)); they run before the next macrotask.
- **Senior scenario:** Always return a value or a Promise from a `.then` so the next step receives it; rethrow in `.catch` if you want to propagate the error.

---

## async/await

- **async** functions always return a Promise. **await** pauses the function until the Promise settles (only inside async functions).
- **Syntactic sugar:** The engine transforms async/await into a state machine over Promises. Same microtask scheduling.
- **Error propagation:** If the awaited Promise rejects, the rejection becomes an exception in the async function. Use try/catch around await, or let the rejection propagate (caller gets a rejected Promise).
- **Senior scenario:** Use try/catch for a single async flow; use `Promise.all` (or similar) for parallel work and catch per-Promise or globally.

---

## Observables (preview)

- **Observable:** A stream of values over time; can represent zero, one, or many values; can represent async or sync delivery.
- **Lazy:** Nothing runs until you **subscribe**. Cancellation is supported (unsubscribe).
- **Contrast with Promise:** One value (or rejection) vs many; no cancellation for standard Promise. In Angular, HTTP and router use Observables; you'll use operators (map, switchMap, etc.) and unsubscribe to avoid leaks.
- Full treatment: [angular/02-rxjs-deep-dive.md](../angular/02-rxjs-deep-dive.md).

---

## When to use each (senior decision guide)

| Need | Use |
|------|-----|
| One-off async result (e.g. single HTTP request, timeout) | **Promise** or **async/await** |
| Multiple values over time, cancellation, or composition of streams | **Observable** (RxJS) |
| Legacy or callback-only APIs | **Callbacks** (or wrap in Promise/Observable) |
| Sequential async steps with clear steps | **async/await** |
| Parallel independent work | **Promise.all** (or `forkJoin` in RxJS) |

---

## Interview focus

- "What is the difference between a Promise and an Observable?" — Promise: single value, no cancellation. Observable: stream, lazy, cancellable.
- "How do errors work with async/await?" — Rejected Promise becomes a thrown exception; use try/catch or let it propagate.
- "When would you use async/await vs Observables?" — Single async result or linear flow → async/await. Streams, events, or need for cancellation → Observables.

---

## Practice

- [practice/07-promises-async-await.js](practice/07-promises-async-await.js) — Promise chaining, catch, async/await, Promise.all. Run with Node.
- [practice/07-observable-preview.js](practice/07-observable-preview.js) — Simple Observable or RxJS-based preview (lazy, subscribe, unsubscribe). Requires `npm install rxjs` if using RxJS.
