# 02 — RxJS Deep Dive (Phase 2)

## Learning objectives

- Explain the **Observable contract**: producer-consumer, lazy execution, unsubscription and leaks.
- Use **Subjects** (Subject, BehaviorSubject, ReplaySubject, AsyncSubject) and know when to use each in Angular.
- Group **operators** by category (transformation, filtering, combination, utility) and justify choices.
- Handle **errors** (catchError, retry, finalize) and understand propagation.
- Be aware of **schedulers** and **marble testing**.

---

## Observable contract

- **Producer-consumer:** An Observable is a lazy stream of values. A **producer** (the function you pass to `new Observable(subscriber => ...)`) runs only when someone **subscribes**. Each subscription can get its own execution (cold) or share one (hot, via Subjects or multicasting).
- **Lazy execution:** Nothing runs until `subscribe()`. Contrast with Promise, which runs immediately.
- **Unsubscription:** `subscribe()` returns a **Subscription**; call `unsubscribe()` to cancel and release resources. In Angular, always unsubscribe (or use `async` pipe, `takeUntil`, or similar) to avoid memory leaks.
- **Hot vs cold:** Cold = each subscriber triggers the producer. Hot = one shared execution; late subscribers may miss earlier values unless you use ReplaySubject or `shareReplay`.

---

## Subjects — the multicast Swiss Army knife

| Type | Behavior | Use in Angular |
|------|----------|----------------|
| **Subject** | No initial value; emits to current subscribers only; multicasts. | Event bus, one-off events. |
| **BehaviorSubject** | Has current value; new subscribers get latest value immediately; multicasts. | Shared state (e.g. "current user"), services that expose a single current value. |
| **ReplaySubject** | Replays N (or all) previous values to new subscribers; multicasts. | Cache last N values for late subscribers. |
| **AsyncSubject** | Emits only the **last** value when it completes; multicasts. | When you only care about the final result of a process. |

---

## Operators by category

### Transformation

- **map** — Like `Array.map`, for each value.
- **switchMap** — Cancel previous inner subscription when outer emits. Use for search: only latest matters.
- **mergeMap** — All inner Observables in parallel; order of emission by completion. Use for parallel requests; beware of order.
- **concatMap** — One inner after another, in order. Use for sequential tasks (e.g. queue of saves).
- **exhaustMap** — Ignore new outer values until current inner completes. Use for "ignore while busy" (e.g. login button).

### Filtering

- **filter**, **take**, **takeUntil**, **skip**
- **debounceTime** — Wait for silence (e.g. after typing).
- **throttleTime** — At most one value per time window.
- **distinctUntilChanged** — Emit only when value changed from previous.

### Combination

- **combineLatest** — Emits when any source emits, but needs at least one value from each; result is array/latest of all.
- **withLatestFrom** — Primary stream; on each emission, take latest from secondary (does not trigger on secondary alone).
- **forkJoin** — Waits for all to complete; emits single array of last values (like Promise.all).
- **merge** — Merge multiple streams into one.

### Utility and multicasting

- **tap** — Side effects (logging, debugging) without changing the stream.
- **shareReplay** — Share one subscription and replay last N values to late subscribers (cache).

---

## Error handling and completion

- **catchError** — Recover from errors (return a fallback Observable or throw to propagate).
- **retry** / **retryWhen** — Retry on error (e.g. N times or with backoff).
- **finalize** — Run when the stream completes or errors (cleanup).
- Errors propagate down the chain until a handler catches; unhandled errors can trigger the Observable's error callback and then tear down the subscription.

---

## Schedulers (brief)

- **asyncScheduler** — Uses `setTimeout`/setInterval; for time-based operators (e.g. `debounceTime`).
- **queueScheduler** — Sync but queue-based.
- **asapScheduler** — Like microtask (Promise.then).
- **observeOn** / **subscribeOn** — Move emissions or subscription to a different scheduler (e.g. run on UI thread).

---

## Testing — marble testing

- **Marble diagrams** — Visualise streams with `-` (time), `a` (value), `|` (complete), `#` (error). Example: `--a--b--|`.
- **TestScheduler** — RxJS's virtual time scheduler; run tests with deterministic timing. In Angular, use `fakeAsync` or marble tests for services that return Observables.

---

## Practice

- [practice/02-rxjs-operators/](practice/02-rxjs-operators/) — `operators-demo.js` (switchMap, mergeMap, concatMap, exhaustMap); **subjects-demo.js** (Subject, BehaviorSubject, ReplaySubject). Run with `node operators-demo.js` and `node subjects-demo.js` (from that folder; `npm install rxjs` first).
