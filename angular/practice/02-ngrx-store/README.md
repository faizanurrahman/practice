# 02 — NgRx Store (real @ngrx/store)

**Concept:** [../../04-architecture-and-state.md](../../04-architecture-and-state.md)

## Goal

Same way we understand **RxJS** by installing the library and running code — here we use the **real @ngrx/store** in a minimal Angular app. One feature (counter): actions, reducer, selector, and a component that dispatches and selects.

## How to run

From this folder:

```bash
npm install
ng serve
```

Open http://localhost:4200. You should see a counter with + and - buttons; the value comes from the store.

## If the app is not yet generated

To create a minimal app from scratch with NgRx:

```bash
ng new ngrx-counter --standalone --routing=false --style=css
cd ngrx-counter
ng add @ngrx/store
```

Then copy the `src/app` structure from this folder (counter actions, reducer, selector, app.config.ts, app.component.ts) into your project.

## What this demonstrates

- **Actions:** `increment`, `decrement` (createAction).
- **Reducer:** `counterReducer` with `createReducer` and `on()`.
- **Selector:** `selectCount` with `createFeatureSelector` and `createSelector`.
- **Provider:** `provideStore({ counter: counterReducer })` in app.config.
- **Component:** Injects `Store`, uses `select(selectCount)` and `dispatch(increment())` / `dispatch(decrement())`.

For the **pattern** without the framework (vanilla store), see [02-ngrx-minimal/](../02-ngrx-minimal/).
