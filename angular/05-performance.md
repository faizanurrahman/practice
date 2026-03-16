# 05 — Performance (Phase 5)

## Learning objectives

- Apply **change detection** optimisations: OnPush, detach, runOutsideAngular, Signals.
- Optimise **large lists**: trackBy, virtual scroll, incremental or deferred rendering.
- Understand **bundle** optimisation: lazy loading, tree shaking, bundle analysis.
- Avoid **memory leaks**: unsubscription, detaching references, using DevTools.

---

## Change detection optimisations

- **OnPush:** Set `changeDetection: ChangeDetectionStrategy.OnPush` so the component (and its subtree) is only checked when inputs change by reference, events fire from the component/children, Observables bound with async pipe emit, or you call `markForCheck()` / `detectChanges()`. Reduces the number of components checked each cycle.
- **detach():** For components that never need to be checked (e.g. static content after first render), call `ChangeDetectorRef.detach()`. Re-attach with `reattach()` if you need CD again.
- **runOutsideAngular:** For code that does not affect the UI (e.g. canvas animation, heavy computation), run it inside `NgZone.runOutsideAngular(() => { ... })` so it does not trigger CD. When you need to update the UI, use `NgZone.run()` or inject `ChangeDetectorRef` and call `markForCheck()`.
- **Signals:** With signal-based reactivity, only the parts of the template that read the signal are updated. No full tree walk; suitable for zoneless and high-frequency updates.

---

## Rendering large lists

- **trackBy:** In `*ngFor`, provide a **trackBy** function that returns a stable id (e.g. `item.id`). Angular then reuses the same DOM node (LView) when the same id appears again, updating only bindings. Without trackBy, replacing the array can cause every row to be destroyed and recreated. See [practice/02-trackby-virtual/](practice/02-trackby-virtual/).
- **Virtual scroll:** Use `@angular/cdk/scrolling` (e.g. `CdkVirtualScrollViewport` and `*cdkVirtualFor`) to render only the items visible in the viewport plus a small buffer. DOM is recycled as the user scrolls. Use for very long lists (thousands of items).
- **Deferred loading:** Angular 17+ `@defer` allows loading a block (or component) when a condition is met (e.g. when visible, on idle). Reduces initial render cost.

---

## Bundle optimisation

- **Lazy loading:** Split the app by route so that each lazy chunk is loaded on demand. Reduces initial bundle size and time-to-interactive.
- **Tree shaking:** The Angular CLI and bundler remove unused code. Ivy’s **locality** (each component compiles to a self-contained unit) improves tree shaking. Avoid importing entire libraries when you only need one function; use path imports or barrel-less imports where it helps.
- **Bundle analysis:** Use `ng build --stats-json` and then a tool like `source-map-explorer` or Webpack Bundle Analyzer to see what is in each chunk. Identify large dependencies and lazy load or replace them.

---

## Network and runtime

- **Caching:** HTTP interceptors can cache GET responses (e.g. by URL) to avoid repeated requests. Set cache headers or use a simple in-memory cache.
- **Prefetching:** Preload data or modules before the user navigates (e.g. on hover or after initial load) to make navigation feel instant.
- **Image optimisation:** Use the `NgOptimizedImage` directive (Angular 15+) for responsive images and lazy loading.

---

## Memory leaks

- **Unsubscription:** Subscriptions to Observables (including HttpClient, router, custom subjects) keep the subscription and the component in memory if the component is destroyed but the subscription is not unsubscribed. Use **async pipe** (auto-unsubscribe), **takeUntil(destroy$)** (with a subject that completes in ngOnDestroy), or **subscription.add()** and unsubscribe in ngOnDestroy.
- **Detaching DOM and references:** If you create components dynamically (e.g. ViewContainerRef.createComponent), destroy them when done. Clear references to DOM nodes or large objects in ngOnDestroy so the GC can reclaim them.
- **Chrome DevTools Memory:** Take heap snapshots before and after a flow (e.g. navigate away and back). Compare to find retained objects and fix leaks (dangling subscriptions, global references).

---

## Practice

- [practice/01-signals-vs-zone/](practice/01-signals-vs-zone/) — Compare zone-based vs signal-based CD.
- [practice/02-trackby-virtual/](practice/02-trackby-virtual/) — trackBy and virtual scroll snippets.
- Optional: run `ng build --stats-json` and analyse the bundle with a visualiser; document findings in notes.
