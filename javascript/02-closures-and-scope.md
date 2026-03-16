# 02 — Closures and Scope

## Learning objectives

- Define **closure**: a function bundled with its Lexical Environment (where it was written).
- Explain **why** closures work: the returned (or stored) function keeps a reference to its outer Lexical Environment, so that environment can outlive the outer Execution Context.
- Apply closures in three senior patterns: **data encapsulation**, **memoization**, and **currying**.

---

## What is a closure?

A **closure** is a function that "remembers" the variables from the scope where it was defined, even when that scope has finished executing. Formally: a function together with its **Lexical Environment**.

When you return a function from another function, the returned function keeps a **reference to the outer Lexical Environment**. The engine does not garbage-collect that environment as long as the closure is still reachable. So the inner function can read (and, if you design it that way, update) those "outer" variables.

---

## Why it works

1. When the outer function runs, an Execution Context is created with its Environment Record (e.g. a variable `count`).
2. The inner function is created in that context, so its Lexical Environment's "outer" reference points to that Environment Record.
3. When the outer function returns the inner function, the outer EC may be popped off the **Call Stack**, but the Environment Record is still referenced by the inner function's closure.
4. When you later call the inner function, it looks up variables in its own record and then in that preserved outer record — so it still sees `count`.

---

## Senior scenarios

### 1. Data encapsulation (private variables)

Use a closure to expose only certain operations (e.g. `increment`, `getCount`) while keeping the actual state (e.g. `count`) inaccessible from outside. No direct access to the variable — only through the returned methods.

### 2. Memoization

Store the results of expensive function calls in an object or Map held in a closure. When the function is called again with the same arguments, return the cached result instead of recomputing. Use when the same inputs are likely to repeat (e.g. recursive Fibonacci, API responses).

### 3. Currying (partial application)

Transform a function that takes multiple arguments into a sequence of functions that each take one (or a few) arguments. Example: `add(a)(b)(c)` or `curry(fn)` so that `curry(f)(1)(2)` is equivalent to `f(1, 2)`. Useful for configuration and composition.

---

## Interview focus

- "Implement a private counter" — factory that returns an object with `increment`, `decrement`, `getCount`; no way to read or set the count directly.
- "Explain memoization and when to use it" — cache in closure; mention same inputs, expensive computation, or repeated calls.

---

## Practice

Run and extend the code in:

- `practice/02-closure-private-counter.js`
- `practice/02-closure-memoize.js`
- `practice/02-closure-curry.js`

Try to implement each from scratch before peeking at the solution.
