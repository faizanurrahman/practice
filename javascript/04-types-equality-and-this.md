# 04 — Types, Equality, and `this`

## Learning objectives

- Use **`typeof`** correctly and know its edge cases (e.g. `typeof null === "object"`).
- Explain **`==`** (abstract equality with coercion) vs **`===`** (strict equality, no coercion) and when to use each.
- Describe **`this`** binding in four situations: default (global or undefined in strict), implicit (method call), explicit (`call`/`apply`/`bind`), and `new` (constructor). Explain how **arrow functions** inherit `this` lexically.

---

## typeof

- `typeof` returns a string: `"undefined"`, `"boolean"`, `"string"`, `"number"`, `"bigint"`, `"symbol"`, `"object"`, `"function"`.
- **Gotcha:** `typeof null === "object"` (historic bug). Check for null explicitly if needed.
- **Gotcha:** `typeof` on an undeclared identifier does not throw; it returns `"undefined"`.

---

## == vs ===

- **`===` (strict):** No type conversion. Same type and same value. Prefer this unless you have a reason to allow coercion.
- **`==` (abstract):** Converts types before comparing. Examples: `1 == "1"` → true; `0 == false` → true; `null == undefined` → true. Can lead to subtle bugs, so senior engineers usually avoid `==` except for intentional null/undefined check.

---

## this binding

`this` is determined by **how** the function is **called**, not where it is written (except for arrow functions).

1. **Default:** In non-strict mode, `this` is the global object (`window` in browser). In strict mode, `this` is `undefined`.
2. **Implicit (method call):** `obj.f()` — `this` is `obj`.
3. **Explicit:** `f.call(thisArg, ...args)`, `f.apply(thisArg, args)`, `f.bind(thisArg)` — `this` is the provided `thisArg`.
4. **`new`:** When called with `new`, `this` is the new object being constructed.
5. **Arrow functions:** They do not have their own `this`. They use the `this` of the enclosing lexical scope (where they were written). Useful in callbacks when you want to keep the outer `this`.

---

## Senior scenario

"What is `this` here?" — Given a snippet (e.g. a method passed as a callback, or an arrow inside a method), identify what `this` refers to and why.

---

## Interview focus

- Explain `this` in the four call patterns (default, implicit, explicit, `new`) and how arrow functions differ.
- Explain coercion pitfalls with `==` and why `===` is the default choice.

---

## Practice

- `practice/04-this-binding.js` — Demos for `this` in global, method call, `call`/`apply`/`bind`, constructor, and arrow function.
- `practice/04-equality-gotchas.js` — Demos for `==` vs `===`, `typeof null`, `NaN !== NaN`; comment expected vs actual.
