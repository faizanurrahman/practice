# 02 — NgRx Minimal (Actions, Reducer, Selector)

**Concept:** [../../02-rxjs-ngrx-performance.md](../../02-rxjs-ngrx-performance.md)

## Goal

Implement a minimal Redux-style store: one **action**, one **reducer**, and one **selector**. No Angular required for the core pattern; this folder shows the idea. In a real app you use `@ngrx/store` and inject `Store`.

## Pattern

1. **Action:** `{ type: '[Counter] Increment', payload?: number }`
2. **Reducer:** `(state, action) => newState` (pure function).
3. **Selector:** Memoized (or simple) function that returns a slice of state, e.g. `getCount(state) => state.count`.

## Minimal store (vanilla JS)

See `store-minimal.js` for a tiny store with `dispatch`, `getState`, and `subscribe`. It demonstrates the same idea: actions describe events, reducer returns next state, and "selectors" read from state.

## In Angular (real @ngrx/store)

For a runnable minimal Angular app that uses the real **@ngrx/store** library (install and run like the RxJS demos), see **[02-ngrx-store/](../02-ngrx-store/)**. There you will `npm install`, `ng serve`, and use actions, reducer, selector, and `Store` in a component.

## Interview takeaway

- **Actions** = events (what happened).
- **Reducers** = pure (state, action) => newState.
- **Selectors** = derived/memoized reads so views only update when the slice changes.
