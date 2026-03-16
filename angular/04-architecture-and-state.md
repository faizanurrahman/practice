# 04 — Architecture and State Management (Phase 4)

## Learning objectives

- Apply **component design** patterns: smart vs presentational, inputs/outputs, content projection, standalone.
- Choose **state management**: when to use a store vs local state; NgRx, ComponentStore, RxJS + BehaviorSubject, Signals.
- Use **feature modules** and **lazy loading**; routing, preloading, guards.
- Apply **advanced DI** patterns: lazy modules, component-level providers, InjectionToken.
- Handle **side effects**: NgRx Effects, RxJS in services, async pipe vs manual subscribe.

---

## Component design patterns

- **Smart (container) vs presentational:** Smart components hold state and logic, fetch data, and pass it down via **inputs** to presentational components. Presentational components receive data and events via **inputs** and **outputs**; they are reusable and easy to test.
- **Inputs and outputs:** Define a clear API: `@Input() item`, `@Output() itemChange = new EventEmitter()`. Prefer explicit contracts over reaching into children.
- **Content projection:** Use `<ng-content>` (and optionally multiple slots) to let the parent inject markup into the component. Enables flexible layouts (e.g. card with header/body/footer slots).
- **Standalone components:** From Angular 14+, components can be **standalone** (no NgModule required). Import dependencies directly in the component; this is the recommended style for new code and simplifies lazy loading of single components.

---

## State management options

- **When store vs local state:** Use a **store** (NgRx or similar) when state is shared across many components, needs to be predictable (e.g. time-travel debugging), or has complex side effects. Use **local component state** (or a service with BehaviorSubject) when state is limited to one feature or a small subtree.
- **NgRx:** Store (single state tree), **Actions** (events), **Reducers** (pure `(state, action) => newState`), **Effects** (side effects in response to actions), **Selectors** (memoized slices). Install with `ng add @ngrx/store`; use in components via `Store` service. Same idea as the vanilla pattern in `practice/02-ngrx-minimal/` but with the real library — see `practice/02-ngrx-store/`.
- **ComponentStore** (NgRx): Local store for a feature or component; same ideas (actions, reducers, selectors) but scoped. Good when you don’t need a global tree.
- **RxJS + BehaviorSubject / shareReplay:** A service that holds a `BehaviorSubject` and exposes `get state$()` or `select(...)`; components subscribe or use async pipe. Simple and sufficient for many apps.
- **Signals:** Angular 16+; `signal()`, `computed()`, `effect()` for fine-grained reactivity. Can replace or complement BehaviorSubject for local or shared state.

---

## Feature modules and lazy loading

- **Organise by feature:** Group components, services, and routes by feature (e.g. `user/`, `orders/`) rather than by type. Keeps the codebase navigable and aligns with lazy loading.
- **Lazy loading:** Load a route’s module (or standalone config) only when the user navigates to that route. In the route config: `loadChildren: () => import('./feature/feature.module').then(m => m.FeatureModule)` (or `loadComponent` for standalone). Reduces initial bundle size.
- **Preloading:** `PreloadAllModules` loads all lazy modules in the background after the app boots; custom preloading strategies can load specific routes on a condition (e.g. after login).
- **Guards:** `canActivate`, `canMatch` (Angular 14+), `canLoad` control access to routes. Use for auth, feature flags, or data preloading.

---

## Advanced dependency injection

- **Providing in lazy-loaded modules:** A service provided in a lazy-loaded module’s `providers` gets a **new instance** for that module subtree (unless you also provide it in root). Be aware of multiple instances if you expect a singleton.
- **Component-level providers:** A component can list a service in its `providers`; that service is available to the component and its descendants. Use when you want a scoped instance (e.g. one service per list item).
- **InjectionToken:** Use for non-class tokens (e.g. config objects, API URLs). Type-safe and avoids string tokens.
- **Optional and self:** `@Optional()` — don’t throw if no provider. `@Self()` — only look in the current injector, not parent.

---

## Side effects

- **NgRx Effects:** Listen for actions, perform async work (HTTP, navigation), dispatch new actions. Keeps reducers pure and centralises side effects. Use when you already use NgRx and have non-trivial async flows.
- **RxJS in services:** Use `switchMap`, `mergeMap`, etc. in services that expose Observables; components subscribe or use async pipe. No store required; good for feature-level data loading.
- **Component subscriptions:** Prefer **async pipe** (auto-unsubscribe and triggers CD with OnPush). If you subscribe manually, unsubscribe in `ngOnDestroy` (or use `takeUntil(destroy$)`).

---

## Practice

- [practice/02-ngrx-minimal/](practice/02-ngrx-minimal/) — Vanilla Redux-style store (pattern without framework).
- [practice/02-ngrx-store/](practice/02-ngrx-store/) — **Real @ngrx/store** in a minimal Angular app: install with `ng add @ngrx/store`, one feature (e.g. counter), actions, reducer, selector, component that dispatches and selects. Run with `ng serve`. Same way we learn RxJS by installing the library — here we use the real NgRx store.
