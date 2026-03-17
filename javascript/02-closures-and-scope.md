# 02 — Scope, Lexical Environment, and Closures

## Learning objectives

- Define **scope** and distinguish between **global**, **function**, and **block** scope.
- Understand what **lexical** means and why JavaScript is a **lexically scoped** language.
- Explain the internal representation of scope via **Lexical Environments** (Environment Record + outer reference).
- Trace how variable lookup walks the **scope chain**.
- Differentiate between `var` (function‑scoped) and `let`/`const` (block‑scoped).
- Define a **closure** and explain how it retains access to its lexical environment.
- Understand that closures capture **bindings** (live links), not frozen values.
- Recognise and fix classic pitfalls (loop‑with‑`var`, accidental globals, block vs function scope confusion).
- Apply closures in real‑world patterns (private state, memoization, factories, debounce/throttle).
- Analyse memory implications and debug closure/scope bugs at a senior level.

---

## 1. What is scope?

**Scope** is the region of your code where a particular variable or identifier is accessible. It answers the question: “If I write `x` here, which `x` will I get?”

In JavaScript, scope is primarily determined by the **placement** of functions and blocks in the source code. This is called **lexical scoping** (we’ll explore that soon).

There are three main kinds of scope in modern JavaScript:

- **Global scope** – The default scope for code running outside any function or block. Variables declared in global scope are accessible everywhere (unless shadowed).
- **Function scope** – Variables declared with `var` (and function declarations) are scoped to the entire function body, regardless of blocks inside that function.
- **Block scope** – Variables declared with `let` and `const` are scoped to the nearest enclosing `{ ... }` block (`if`, `for`, `while`, etc.).

**Example**:

```javascript
var globalVar = "I'm global";

function outer() {
  var functionScoped = "I'm function‑scoped (var)";
  
  if (true) {
    let blockScoped = "I'm block‑scoped (let)";
    console.log(blockScoped);    // accessible here
    console.log(functionScoped); // accessible here (same function)
  }
  
  // console.log(blockScoped); // ReferenceError: blockScoped is not defined
  console.log(functionScoped);   // works
}

console.log(globalVar); // works
// console.log(functionScoped); // ReferenceError
```

---

## 2. What does “lexical” mean?

**Lexical** refers to the fact that scope is determined by the **structure of the source code** – where variables and blocks are written – not by the runtime call stack.

In a **lexically scoped** language (like JavaScript), a function’s access to variables is based on where that function is **defined**, not where it is **called**. This is in contrast to **dynamic scoping**, where scope depends on the calling context.

### Example: definition site vs call site

```javascript
const x = 'global';

function outer() {
  const x = 'outer';
  
  function inner() {
    console.log(x);
  }
  
  return inner;
}

const fn = outer();
fn(); // prints 'outer'
```

If JavaScript were dynamically scoped, `inner` would look at the caller’s scope (global) and print `'global'`. Because it’s lexically scoped, `inner` remembers the scope where it was **defined** – inside `outer` – and prints `'outer'`.

This is the essence of **closures**: a function “closes over” the variables of its lexical environment and carries that environment with it wherever it is called.

---

## 3. Lexical Environment – the internal representation of scope

To implement lexical scoping, the JavaScript engine uses **Lexical Environments**. You can think of them as “scope objects” that exist at runtime.

A Lexical Environment consists of two parts:

1. **Environment Record** – an internal map from identifier names to bindings (variables, functions, etc.).  
   - **Declarative Environment Record** – used for function scopes, block scopes (with `let`/`const`), and `catch` clauses.  
   - **Object Environment Record** – used for the global scope and `with` statements. Identifiers are resolved against a binding object (e.g., the global object).

2. **Outer reference** – a pointer to the Lexical Environment of the **lexically enclosing** code (the scope outside the current one). This forms a chain of environments.

When a function is defined, it captures a reference to the **current** Lexical Environment. Later, when the function is called, a new Lexical Environment is created for that call, and its outer reference is set to the captured one.

### Visualisation

For the example above:

```
Global Environment
  Record: { x: 'global', outer: <function> }
  outer: null
       ↑
       | (outer reference of outer's environment)
outer() Environment (created when outer is called)
  Record: { x: 'outer', inner: <function> }
  outer: Global Environment
       ↑
       | (outer reference of inner's environment – captured at definition)
inner() Environment (created when fn is called)
  Record: {}
  outer: outer() Environment
```

When `console.log(x)` runs inside `inner`:

1. Look in `inner`’s Environment Record – no `x`.  
2. Follow outer → `outer()` Environment – finds `x: 'outer'`.  
3. Use that binding.

This chain is the **scope chain**.

---

## 4. Variable lookup and the scope chain

Whenever you reference a variable, the engine performs a **scope chain lookup**:

1. Start at the current Lexical Environment’s Environment Record.  
2. If the binding is found, use it.  
3. If not found, follow the outer reference to the parent environment and repeat.  
4. Continue until either the binding is found or the global environment is reached (its outer is `null`).  
5. If still not found, a `ReferenceError` is thrown (in strict mode; non‑strict has legacy behaviour like creating accidental globals).

This process is entirely **static** – the shape of the chain is determined by the lexical nesting of functions and blocks in the **source code**, not by the call stack at runtime.

---

## 5. Block scope vs function scope

### `var` – function scope

Variables declared with `var` are **function‑scoped**, not block‑scoped. They are visible throughout the entire function in which they are declared, regardless of block nesting.

```javascript
function test() {
  if (true) {
    var x = 10;
  }
  console.log(x); // 10 – x is accessible because the whole function is its scope
}
test();
```

`var` is hoisted and initialised with `undefined` during the creation phase, as discussed in `01-execution-context-and-memory.md`.

### `let` and `const` – block scope

Variables declared with `let` and `const` are **block‑scoped**. They are confined to the nearest pair of `{ }` braces.

```javascript
function test() {
  if (true) {
    let y = 20;
    const z = 30;
  }
  console.log(y); // ReferenceError
  console.log(z); // ReferenceError
}
test();
```

`let` and `const` are hoisted but remain **uninitialised** until their declaration line (the Temporal Dead Zone). See `01-execution-context-and-memory.md` for the full TDZ explanation.

**Senior takeaway:** Prefer `let`/`const` over `var` in modern code. They prevent accidental leakage outside blocks and avoid hoisting confusion.

---

## 6. What is a closure?

A **closure** is the combination of a function and the lexical environment within which that function was declared. That environment consists of any local variables that were in‑scope at the time the closure was created. The function retains **access** to those variables even after the outer function has finished executing.

In simpler terms: a closure lets a function “remember” the variables from where it was born, long after that place is gone.

### Mental model

Do **not** think: “a closure is a function inside a function.” That’s an oversimplification.

Think instead:  
**closure = function + retained access to outer bindings**

The outer function may have already returned; its stack frame is gone. Yet the inner function still holds a **reference** to the outer variables – those variables are kept alive in memory because the closure needs them.

---

## 7. How closures work – under the hood

Closures are implemented using Lexical Environments:

1. When the outer function runs, an Execution Context is created with its own Lexical Environment (containing its local variables).  
2. When an inner function is **defined**, it captures a reference to the current Lexical Environment as its **outer environment**.  
3. When the outer function returns, its Execution Context is popped off the call stack, but its Lexical Environment stays alive **if** at least one closure still references it.  
4. When the closure is later called, a new Lexical Environment is created for that call, whose outer reference points to the preserved outer environment. Variable lookup then walks this chain.

**Example:**

```javascript
function createCounter() {
  let count = 0; // lives in createCounter's lexical environment

  return function () { // closure captures that environment
    count++;
    return count;
  };
}

const counter = createCounter();
console.log(counter()); // 1
console.log(counter()); // 2
```

After `createCounter()` finishes, its Execution Context is gone, but the Lexical Environment containing `count` remains because `counter` still references it.

---

## 8. Closures capture bindings, not snapshots

A critical senior‑level insight: **closures capture the variable binding (live link), not a frozen copy of its value at the moment of creation.**

```javascript
let x = 1;

function printX() {
  console.log(x);
}

x = 2;
printX(); // 2 – not 1
```

`printX` was defined when `x` was 1, but when it runs later, it sees the **current** value of `x`. It captures the binding (the storage location), not the earlier value.

This distinction is crucial in asynchronous code (timers, promises, event handlers) and in loops.

---

## 9. The classic loop‑with‑`var` trap

```javascript
for (var i = 0; i < 3; i++) {
  setTimeout(() => console.log(i), 0);
}
// Output: 3, 3, 3
```

**Why?**

- `var i` is **function‑scoped**: there is only **one** binding of `i` for the entire loop.  
- Each arrow function forms a closure that captures that **single binding** of `i`.  
- By the time the callbacks run, the loop has finished and `i` is `3`. All callbacks see the same final value.

### Fix 1: Use `let` (block‑scoped binding per iteration)

```javascript
for (let i = 0; i < 3; i++) {
  setTimeout(() => console.log(i), 0);
}
// Output: 0, 1, 2
```

`let` creates a **new binding** of `i` on each iteration; each closure captures its own binding.

### Fix 2: IIFE (pre‑`let` workaround)

```javascript
for (var i = 0; i < 3; i++) {
  (function (j) {
    setTimeout(() => console.log(j), 0);
  })(i);
}
```

The IIFE creates a new function scope for each iteration; `j` becomes a separate parameter binding captured by the closure.

### Fix 3: `bind` to snapshot values

```javascript
for (var i = 0; i < 3; i++) {
  setTimeout(console.log.bind(console, i), 0);
}
```

`bind` creates a new function that, when called, invokes `console.log` with the **argument value as it was when `bind` was called**.

---

## 10. Real‑world patterns using closures

### 10.1 Private state (data encapsulation)

```javascript
function makeCounter() {
  let count = 0;
  return {
    increment() { count++; },
    decrement() { count--; },
    getCount() { return count; }
  };
}

const c = makeCounter();
c.increment();
console.log(c.getCount()); // 1
```

`count` is private; only the returned methods can access it.

### 10.2 Memoization (caching)

```javascript
function memoize(fn) {
  const cache = {};
  return function (x) {
    if (cache[x] === undefined) {
      cache[x] = fn(x);
    }
    return cache[x];
  };
}
```

The returned function retains `cache` across calls.

### 10.3 Function factories (currying / partial application)

```javascript
function multiplier(factor) {
  return function (x) {
    return x * factor;
  };
}

const double = multiplier(2);
console.log(double(5)); // 10
```

### 10.4 Debounce / throttle

```javascript
function debounce(fn, delay) {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}
```

`timer` is kept alive between calls.

### 10.5 Event handlers

```javascript
function setupButton(id) {
  let clicks = 0;
  document.getElementById(id).addEventListener('click', () => {
    clicks++;
    console.log(`Clicked ${clicks} times`);
  });
}
```

Each handler keeps its own `clicks`.

---

## 11. Memory implications and leaks

Because closures keep outer environments alive, they can **prevent garbage collection** if those environments contain large objects or DOM nodes.

Common leak scenarios:

- Closures stored on long‑lived objects (e.g., singletons, globals).  
- Event listeners on DOM elements that are removed but never cleaned up.  
- Capturing more than necessary (e.g., entire objects when only one field is needed).

Mitigation:

- Remove event listeners when elements are destroyed.  
- Null out references in closures when no longer needed.  
- Use `WeakMap` / `WeakSet` for caches keyed by objects.  
- Be intentional about what you capture in asynchronous callbacks.

For a deeper memory discussion, see `05-memory-model.md`.

---

## 12. Debugging closure and scope bugs (senior lens)

When faced with a bug that might be closure/scope related, ask:

1. **Which binding is captured?**  
   - Is it per‑iteration (`let`) or shared (`var`)?  
   - Is it a primitive or an object?
2. **When is the closure created vs executed?**  
   - Is there a delay (setTimeout, promises, events)?  
   - Could the variable change between creation and execution?
3. **Is state shared unintentionally?**  
   - Multiple closures capturing the same mutable object.  
4. **Is there a leak?**  
   - Does the closure hold references to large data / DOM?  
   - Is it stored in a long‑lived container (global, singleton array)?

Work a minimal reproduction (like the loop‑with‑`var` example) to isolate the behaviour.

---

## 13. Interview focus

### Beginner

- **What is scope?**  
  Scope is the region of code where a variable is visible. In JavaScript we have global, function, and block scope.

- **What is a closure?**  
  A closure is a function that retains access to variables from its lexical environment even after the outer function has returned.

### Intermediate

- **What is lexical scoping?**  
  Scope is determined by where functions and blocks are **defined** in the source code, not by where functions are **called**.

- **Why does `var` in loops often cause bugs with closures?**  
  Because `var` is function‑scoped, all closures share the same binding. By the time callbacks run, the loop variable has its final value.

- **How does `let` fix the loop issue?**  
  `let` creates a new binding per iteration, so each closure captures its own binding.

### Senior

- **Explain the difference between capturing a binding and capturing a value.**  
  Closures capture the variable’s storage location (binding), not a snapshot of its value. Any later changes to that binding are visible to the closure.

- **How can closures cause memory leaks?**  
  By retaining references to outer environments that contain large objects or DOM nodes. As long as the closure is reachable, that environment can’t be collected.

- **Given a bug with stale data in an async callback, how would you diagnose whether it’s a closure issue or a race condition?**  
  Check creation vs execution timing, inspect captured variables at both times, and see if shared mutable state or out‑of‑order async operations are responsible.

---

## 14. Practice

Work through the following exercises and follow your Phase 0 rule (define → flow → run → break → explain, ideally logging in `workspace/debug-log.md`):

- **Private counter:**  
  - File: `practice/02-closure-private-counter.js`  
  - **Goal:** Implement a factory that returns an object with `increment`, `decrement`, and `getCount`, using a private `count` variable.  
  - **Break:** Expose `count` on the returned object and see how external mutation breaks encapsulation.  
  - **Reflect:** Is the closure capturing a binding or a value?

- **Memoization:**  
  - File: `practice/02-closure-memoize.js`  
  - **Goal:** Implement `memoize(fn)` using a closure‑held cache.  
  - **Break:** Clear the cache on every call and observe loss of benefit; or accidentally share one cache for multiple memoized functions.  
  - **Reflect:** Where does the cache live? Why does it persist?

- **Currying / function factory:**  
  - File: `practice/02-closure-curry.js`  
  - **Goal:** Implement `curry(fn)` or `add(a)(b)(c)`; show how outer arguments are retained.  
  - **Break:** Remove closure and try to pass all arguments at once; compare flexibility and composability.

- **Loop with `var` vs `let` (optional additional file):**  
  - Create your own small snippet (or a new practice file) where `for (var i...)` + `setTimeout` prints `3,3,3` and then fix it with `let`.  
  - Log in `debug-log.md` why the first version fails and the second succeeds.

Try to implement each exercise from scratch before peeking at any reference solution. Focus on tracing **which environment** each function closes over and how that affects behaviour over time.

