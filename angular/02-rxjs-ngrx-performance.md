# 02 — RxJS, NgRx, and Performance

## Learning objectives

- Choose and justify **RxJS** operators for async flows: `switchMap`, `mergeMap`, `concatMap`, `exhaustMap`.
- Explain **NgRx** pattern: Actions (events), Reducers (pure state transitions), Selectors (memoized slices).
- Apply **performance** techniques: **trackBy** in `*ngFor`, **virtualization** (e.g. `CdkVirtualScrollViewport`), and how **ViewContainerRef** / Embedded Views affect LView.

---

## RxJS: four key operators

All of these "flatten" an inner Observable; the difference is **what they do with previous or concurrent inner subscriptions**.

| Operator | Behavior | Use when |
|----------|----------|----------|
| **switchMap** | Cancels the previous inner subscription when a new value arrives. | Search-as-you-type: only the latest search matters. |
| **mergeMap** | Subscribes to all inner Observables in parallel; emits as they complete. | Multiple parallel requests (e.g. load details for many IDs). |
| **concatMap** | Subscribes to inner Observables one after the other, in order. | Sequential operations (e.g. queue of saves). |
| **exhaustMap** | Ignores new outer values until the current inner Observable completes. | Prevent double-submit: ignore clicks until request finishes. |

**Senior scenario:** "User types in search: switchMap vs mergeMap?" — Use **switchMap** so that each new keystroke cancels the previous HTTP request; you only care about the latest result.

---

## NgRx (Redux pattern)

- **Actions:** Plain objects describing an event (e.g. `{ type: '[User] Load', id }`). Dispatched via `store.dispatch()`.
- **Reducers:** Pure functions `(state, action) => newState`. No side effects; same inputs always give same output. Handle actions and return the next state.
- **Selectors:** Memoized functions that return a slice or derived state from the store. Use `createSelector` for composition and memoization so views only update when the selected slice changes.

**Interview:** Explain that actions are events, reducers are pure state transitions, and selectors provide efficient, memoized reads.

---

## Performance

### trackBy in *ngFor

Without `trackBy`, Angular identifies list items by object reference. When the array is replaced (e.g. new data from the server), Angular may destroy and recreate all DOM nodes. With **trackBy**, you provide a function `(index, item) => id`. Angular tracks items by that id and reuses DOM nodes when the same id appears again, updating only bindings. Reduces reflows and improves performance for long lists.

### Virtualization

Instead of rendering 1,000 items in the DOM, **virtual scroll** (e.g. `CdkVirtualScrollViewport`) renders only the items visible in the viewport (plus a small buffer). As the user scrolls, DOM is recycled. Use for very long lists.

### ViewContainerRef and Embedded Views

Dynamic components or `ngTemplateOutlet` insert **Embedded Views** into the LView at specific indices. Understanding that the LView is an array and that views can be inserted/removed helps when debugging dynamic UIs (e.g. modals, popovers).

---

## Interview focus

- Compare switchMap, mergeMap, concatMap, exhaustMap in one sentence each.
- Explain trackBy and when virtualization is needed (long lists, many DOM nodes).

---

## Practice

- `practice/02-rxjs-operators/` — Demos of switchMap, mergeMap, concatMap, exhaustMap (e.g. search typing, parallel requests). Answer: "When would you use switchMap vs mergeMap here?"
- `practice/02-ngrx-minimal/` — Minimal store: one action, one reducer, one selector; dispatch and read state.
- `practice/02-trackby-virtual/` — Component with `*ngFor` and trackBy; optional virtual scroll snippet.
