# 06 — Prototypes and Inheritance

## Learning objectives

- Explain the **`[[Prototype]]`** chain and how JavaScript looks up properties.
- Apply the **four rules of `this`**: default, implicit, explicit, `new`.
- Describe how **`class`** is syntactic sugar over prototypes.
- Relate this to debugging (e.g. DI-like patterns, method binding).

---

## The `[[Prototype]]` chain

Every object has an internal **`[[Prototype]]`** (exposed as `Object.getPrototypeOf(obj)` or the deprecated `obj.__proto__`). When you access `obj.foo`, the engine:

1. Looks for `foo` on `obj`.
2. If not found, follows `[[Prototype]]` and repeats.
3. Stops at the first match or when the chain ends (null).

So "inheritance" in JS is **delegation**: objects delegate to another object. Constructor functions have a `.prototype` property; when you call `new F()`, the new object's `[[Prototype]]` is set to `F.prototype`.

---

## Four rules of `this`

- **Default (strict vs non-strict):** In strict mode, standalone function call → `this` is `undefined`. In sloppy mode, `this` is the global object.
- **Implicit:** Method call — `obj.method()` → `this` is `obj`.
- **Explicit:** `call`, `apply`, `bind` — you pass the value of `this`.
- **`new`:** When you call a function with `new`, `this` is the newly created object (and that object's `[[Prototype]]` is set to the function's `.prototype`).

**Senior scenario:** In a callback (e.g. `setTimeout(callback, 0)` or `element.addEventListener('click', handler)`), the caller is the runtime or the DOM, so `this` is not your component. Use arrow functions (lexical `this`) or `.bind(this)`.

---

## `class` as syntactic sugar

- `class Foo extends Bar` sets up the prototype chain: `Foo.prototype` has `[[Prototype]]` pointing to `Bar.prototype`.
- `constructor` is the function you call with `new`.
- `super` in a method calls the parent's method; `super()` in constructor calls the parent constructor.
- Under the hood, you still have constructor functions, `.prototype`, and `[[Prototype]]` — `class` is a clearer syntax for the same model.

---

## Why this matters for Angular

- **Dependency injection:** Angular resolves tokens by walking the injector tree. Understanding "lookup along a chain" (like the prototype chain) helps when debugging why a service is or isn't found.
- **Method binding:** If you pass `this.onClick` to a template or callback, `this` inside `onClick` may be wrong when the function runs. Use arrow functions or bind so that `this` is the component instance.

---

## Interview focus

- "How does property lookup work?" — Look on the object, then follow `[[Prototype]]` until found or null.
- "What is `this` in a callback?" — Depends on how the callback is invoked; often the global or the DOM element; use arrow or bind for component context.
- "What is `class` in JavaScript?" — Syntactic sugar over constructor + prototype; inheritance is still prototype delegation.

---

## Practice

- [practice/04-this-binding.js](practice/04-this-binding.js) — Demos for default, implicit, explicit, `new`, arrow. Run and explain each.
- [practice/06-prototype-chain.js](practice/06-prototype-chain.js) — `Object.create`, constructor, `class` extend. Run and trace the prototype chain.
