# 01 — Angular Core and Ivy Architecture

## Learning objectives

- Explain **Change Detection**: Zone.js (monkey-patching, top-down tree check) vs **Signals** (fine-grained, zoneless).
- Describe **Ivy** internals: **LView** (instance data array) vs **TView** (static blueprint).
- Explain **Dependency Injection** trees: ModuleInjector vs ElementInjector and resolution order.

---

## Change Detection

### Zone.js (default)

Angular uses Zone.js to know *when* to run change detection. Zone "monkey-patches" asynchronous browser APIs (e.g. `setTimeout`, `fetch`, DOM events). When such an API runs, Zone notifies Angular that "something might have changed," and Angular runs a **top-down** check of the component tree, comparing bindings and updating the DOM where needed.

- **Pros:** Works with any async source; no need to opt in per component.  
- **Cons:** Whole tree (or subtrees) are checked; can be costly for large apps. Requires Zone.js in the bundle.

### Signals (modern, fine-grained)

With **Signals**, you declare reactive state with `signal()` and derived state with `computed()`. Only the parts of the template that **read** that signal are updated when it changes. No need to run a full tree check; the dependency graph is explicit. This allows **zoneless** applications (no Zone.js).

- **Pros:** Precise updates, better performance, simpler mental model for "what changed."  
- **Cons:** You must use signals (and possibly `effect()`) where you want reactivity; migration from zone-based can be incremental.

**Senior scenario:** "When would you use Signals over Zone?" — When you need better performance, finer control over updates, or are building new features and want to avoid full-tree CD.

---

## Ivy: LView and TView

Angular’s Ivy compiler and runtime use two main data structures for each component type:

### TView (Template View)

- **Static** "blueprint" shared by all instances of a component.
- Stores instructions for creating the DOM, bindings, and structure.  
- Think of it as the class definition; it doesn’t hold current values.

### LView (Logical View)

- **Instance** data: the current values of variables, DOM nodes, child injectors, etc.
- Implemented as an **array** for speed and memory efficiency (index-based access, cache-friendly).
- Each component instance has its own LView; the TView describes how to interpret it.

**Senior scenario:** "Why an array for LView?" — Fast index-based access, predictable layout, better for the engine than a large object with many keys.

---

## Ivy compiler: templates to instructions

- Angular templates are compiled into **instruction code**: functions (often prefixed with `ɵɵ`) that the runtime executes. No interpreter over the template at runtime; the compiler turns bindings and structure into direct instructions.
- **Two-pass system:**
  - **Create pass** — Builds DOM nodes, creates LView slots, attaches listeners. Runs when a view is first created (e.g. component init, *ngIf becomes true).
  - **Update pass** — Refreshes bindings (interpolations, property bindings). Runs on every change detection cycle for dirty views. Only binding expressions are re-evaluated; structure is not recreated.
- **Why two passes:** Separation allows the runtime to do the minimum work on updates (no need to re-create DOM or re-attach listeners unless the view was recreated). This makes change detection fast.

---

## TNode and LContainer

- **TNode (Template Node):** Compile-time metadata for each node in the template (element, text, container, etc.). Stored in TView; describes the shape of the view.
- **LContainer:** Runtime container that holds **embedded views** (e.g. one view per *ngFor item, or one view for the *ngIf branch). When you use `*ngIf` or `*ngFor`, the compiler creates an `<ng-template>` and an LContainer; at runtime, the container either has zero or one LView (*ngIf) or many LViews (*ngFor). Each LView is an instance (e.g. one row in a list).
- Together with TView/LView, TNode and LContainer explain how structural directives inject and remove views without rebuilding the whole component.

---

## Change detection deep dive

- **What triggers CD:** In zone-based apps, **Zone.js** patches async APIs (setTimeout, fetch, XHR, DOM events). When any of these run, Zone notifies Angular; Angular marks the root (or the relevant zone) as needing check and runs CD from top to bottom. So: user event, timer, HTTP callback, etc. all lead to "something might have changed" → run CD.
- **Create vs update:** On the first run (or when a view is created), both create and update run. On subsequent CD runs, only the **update** pass runs for existing views — re-evaluate bindings and update DOM. Create runs again only when a new view is inserted (e.g. *ngIf becomes true, new *ngFor item).
- **OnPush:** A component using `ChangeDetectionStrategy.OnPush` is only checked when (1) an **input reference** changes (Angular compares by reference), (2) an **event** originates from the component or its descendants, or (3) an **Observable** bound via async pipe emits, or (4) you manually call **markForCheck()** or **detectChanges()**. Use **markForCheck()** when you update state outside Angular’s knowledge (e.g. a callback that mutates a property); use **detectChanges()** when you want to run CD immediately on this component and its children.
- **Signals:** With signals, the template subscribes to the signal; when the signal changes, only the affected nodes are updated. No need for Zone or a full tree walk for that component; fine-grained and suitable for zoneless apps.

---

## ViewContainerRef and dynamic rendering

- **ViewContainerRef** — The API to insert, remove, or move **embedded views** at a specific place in the DOM. Each structural directive (e.g. *ngIf, *ngFor) gets a ViewContainerRef for its anchor (the host of the directive).
- **EmbeddedViewRef** — A view created from an `<ng-template>`. Has its own LView; can be attached to or detached from a ViewContainerRef.
- **How *ngIf works:** An LContainer is created. When the condition is true, the runtime calls **createEmbeddedView** (one LView from the ng-template) and inserts it into the container. When false, that view is **detached** (or destroyed). One view in or out.
- **How *ngFor works:** The same LContainer holds **many** LViews — one per item. When the list changes, Angular compares by identity (or **trackBy**); for new items it creates a new LView; for removed items it destroys the LView; for existing items it reuses the LView and runs the update pass. **trackBy** ensures the same item (by id) reuses the same LView and DOM node, reducing thrash.

---

## Structural directives under the hood

- The `*` syntax (e.g. `*ngIf="x"`) is **desugared** into an `<ng-template>` that wraps the host element, with the directive applied to the template. So Angular never renders the host element itself; the directive gets a ViewContainerRef and the template, and it decides whether to create one (or more) embedded views from that template.
- **ngTemplateOutlet** — Renders an `<ng-template>` at a given place (you pass the template ref and optional context). Useful for reusable layout.
- **ngComponentOutlet** — Dynamically creates a component at a given place. Useful for dynamic component loading.

---

## Zone.js — friend or foe?

- **What Zone.js does:** It patches global async APIs so that when any async work runs (setTimeout, fetch, click, etc.), the current Zone is notified. Angular’s zone runs a task after the microtask queue and then triggers change detection. So "anything async" can trigger CD.
- **Downside:** That can mean **unnecessary** CD — e.g. a third-party library that uses setTimeout or setInterval will trigger CD even when your app state did not change. For heavy or frequent async work that doesn’t affect the UI, run it **outside** Angular’s zone: `NgZone.runOutsideAngular(() => { ... })`. Then CD is not triggered by that code.
- **Zoneless future:** With Signals and optional zoneless, Angular can run without Zone.js. Change detection is then triggered only by explicit updates (signals, async pipe, or manual tick). Reduces bundle size and surprise CD runs.

---

## Dependency Injection trees

### ModuleInjector

- Configured via `@NgModule` or `@Injectable({ providedIn: 'root' })`.
- `providedIn: 'root'` puts the service in the root injector (singleton for the app).
- Other providers in `NgModule.providers` are available to that module and its imports.

### ElementInjector

- Created for **every DOM element** that has a directive or component.
- When a component asks for a service, Angular first looks in the **ElementInjector** for that component, then walks **up** the component tree (parent, grandparent, …), and finally falls back to the **ModuleInjector** tree.

So resolution order is: **component’s injector → parent injector → … → root injector → module injectors.**

**Senior scenario:** "Where does Angular look for a service?" — ElementInjector hierarchy first (component and ancestors), then ModuleInjector (e.g. root).

---

## Interview focus

- Draw or describe LView (instance array) vs TView (static blueprint).
- Explain change detection with Zone.js vs with Signals in one or two sentences each.
- Explain DI resolution order: element tree first, then module tree.

---

## Practice

See `practice/01-signals-vs-zone/` for a minimal Angular app comparing a signal-based counter and a zone-based counter. Run and observe (or document) how change detection runs in each case.
