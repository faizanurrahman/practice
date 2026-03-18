# 11 — Modules

## Learning Objectives

- Understand why modules are crucial for organising code, managing dependencies, and enabling tooling.
- Distinguish between **named exports** and **default exports**.
- Learn **dynamic imports** for lazy loading.
- Grasp how module resolution and **hoisting** affect initialisation order.
- Recognise the dangers of **cyclic dependencies** and how to avoid them.
- Appreciate how modules shape application architecture and reduce coupling.

---

## 1. Why Modules?

Modules provide:

- **Encapsulation** – variables and functions are scoped to the module, not global.
- **Explicit dependencies** – you must import what you need.
- **Reusability** – modules can be shared across projects.
- **Tooling support** – bundlers, optimisers, and linters work better with modular code.
- **Lazy loading** – dynamic imports allow loading code only when needed.

Before modules, developers used global variables and IIFEs, leading to naming collisions and messy dependencies.

---

## 2. Named vs Default Exports

### 2.1 Named Exports

A module can export multiple named bindings.

```javascript
// math.js
export function add(a, b) { return a + b; }
export function subtract(a, b) { return a - b; }
export const PI = 3.14;
```

Import with the same names (or alias):

```javascript
import { add, subtract as sub, PI } from './math.js';
```

### 2.2 Default Export

Each module can have at most one default export.

```javascript
// logger.js
export default function log(msg) {
  console.log(msg);
}
```

Import with any name:

```javascript
import myLog from './logger.js';
```

Default exports are often used for the main functionality of a module.

---

## 3. Dynamic Imports

Dynamic imports return a promise that resolves to the module object. They enable **code splitting** and lazy loading.

```javascript
button.addEventListener('click', async () => {
  const module = await import('./heavy-module.js');
  module.run();
});
```

This is widely used in Angular for lazy loading feature modules and components.

---

## 4. Module Resolution and Hoisting

Imports are **hoisted** – they are processed before any code in the module runs. This ensures that all imports are available when the module executes.

```javascript
// a.js
import { b } from './b.js';
console.log(b); // b's value is ready

// b.js
export const b = 42;
```

Even though the import appears after the `console.log`, it is hoisted and resolved before execution.

---

## 5. Cyclic Dependencies

A cyclic (circular) dependency occurs when module A imports from module B, and module B imports (directly or indirectly) from module A.

```javascript
// a.js
import { b } from './b.js';
export const a = 'a from a';
console.log(b); // may be undefined if cycle not handled well

// b.js
import { a } from './a.js';
export const b = 'b from b';
console.log(a);
```

This can lead to incomplete initialisation – one module may see the other's export as `undefined` because the other hasn't finished executing.

**Design smell:** Cycles often indicate poor separation of concerns. They make refactoring hard and can cause runtime errors.

**Mitigation:** Refactor to extract shared dependencies into a third module, or use dependency injection to break the cycle.

---

## 6. Modules and Architecture Quality

Modules enforce **explicit dependencies** and **unidirectional data flow**. If you see tangled imports, it's a sign that the architecture needs revisiting. A healthy module graph should be acyclic and layered (e.g., core → feature → UI).

---

## 7. Interview Questions and Answers

### Beginner

**Q1: What is a module?**  
**A:** A module is a self‑contained unit of code that encapsulates its implementation details and exposes a public API via exports. It helps organise code, avoid global scope pollution, and manage dependencies.

**Q2: What is the difference between named and default export?**  
**A:** Named exports allow multiple exports per module, and they must be imported using the exact names (or aliases). Default export allows one main export per module, and it can be imported with any name.

**Q3: Why are modules useful?**  
**A:** They promote code organisation, reusability, and maintainability. They enable tooling like bundlers and linters, and support lazy loading.

### Intermediate

**Q4: When would you use dynamic import?**  
**A:** Dynamic import is useful for lazy loading code that isn't needed immediately, such as a heavy library, a rarely used feature, or a route‑based component in a framework. It helps reduce initial bundle size and improve performance.

**Q5: What is a cyclic dependency?**  
**A:** A cyclic dependency occurs when two or more modules depend on each other directly or indirectly. This can cause runtime errors because modules may be incompletely initialised when they reference each other.

**Q6: Why are imports hoisted?**  
**A:** Imports are hoisted to ensure that all imported bindings are available when the module's code runs. This allows the module to use imported values anywhere in its code, not just after the import statement.

### Senior

**Q7: Why are cyclic imports dangerous in large systems?**  
**A:** Cyclic imports can lead to subtle bugs where imported values are `undefined` because the imported module hasn't finished executing. They also make the module graph hard to understand and refactor. In large systems, cycles can spread and create a tangled architecture that is brittle and difficult to maintain.

**Q8: How do modules influence architecture quality?**  
**A:** Modules enforce explicit dependencies and encourage separation of concerns. A well‑structured module graph is acyclic, layered, and follows the dependency inversion principle. This makes the system easier to test, maintain, and evolve. Modules act as architectural boundaries that prevent unintended coupling.

**Q9: How would you structure a feature to minimise import tangling?**  
**A:** I would follow a feature‑based folder structure with clear public APIs. Each feature exports only what is necessary, and internal modules are private. I'd use a facade pattern: a single entry point that re‑exports the public parts, hiding implementation details. I'd also ensure that dependencies flow from higher‑level to lower‑level modules (e.g., UI → use cases → infrastructure) and avoid cross‑feature direct imports by using shared core modules or dependency injection.

---

## 8. Summary

- Modules provide encapsulation, explicit dependencies, and support tooling.
- Named exports and default exports serve different purposes.
- Dynamic imports enable lazy loading and code splitting.
- Imports are hoisted to guarantee availability.
- Cyclic dependencies are a design smell and should be avoided.
- A healthy module graph is a sign of good architecture.

---

## Practice

- [practice/11-modules/](practice/11-modules/) — ESM example: named exports (`math.js`), default export (`logger.js`), and dynamic import in `main.js`. Run with `node practice/11-modules/main.js` from the `javascript` directory (requires Node with ESM support; the folder uses `"type": "module"` in its `package.json`).
