# 01 — Execution Context and Memory

## Learning objectives

- Explain what an **Execution Context (EC)** is and when it is created.
- Describe the **Creation phase** vs **Execution phase** of an EC.
- Define **Lexical Environment** (Environment Record + reference to outer environment) and the scope chain.
- Explain **hoisting** and the **Temporal Dead Zone (TDZ)** for `let` and `const`.

---

## Execution Context: the "environment" where code runs

An **Execution Context** is the environment in which a piece of code is evaluated. It holds the variables, `this` binding, and the scope chain for that code.

- **Global Execution Context (GEC):** Created when the script starts. In browsers it creates the global object (`window`) and sets `this` to that object. There is exactly one GEC per script.
- **Function Execution Context (FEC):** Created every time a function is **invoked**. Each call gets its own FEC.

---

## Two phases of an Execution Context

### 1. Creation phase

- The engine allocates memory for **variable declarations** and **function declarations**.
- Variables declared with `var` are initialized to `undefined`; `let` and `const` are left **uninitialized** (TDZ).
- Function declarations are fully created (name + body) and assigned to the identifier.
- `this` is set; the **outer environment reference** is set (based on where the code was written — lexical scope).

### 2. Execution phase

- Code runs line by line.
- Assignments happen (e.g. `x = 5`).
- For `let`/`const`, the variable is initialized when the engine reaches the declaration line; before that, the variable is in the **Temporal Dead Zone** and accessing it throws `ReferenceError`.

---

## Lexical Environment

A **Lexical Environment** consists of:

1. **Environment Record** — storage for local variables and declarations in this scope.
2. **Reference to the outer (lexical) environment** — the "parent" scope where the code was **written**.

This outer reference is what forms the **scope chain**: when you use a variable, the engine looks in the current Environment Record; if not found, it follows the outer reference and repeats until the global scope.

---

## Hoisting

- **Function declarations** are hoisted as a whole (name + body), so you can call them before the line they appear on.
- **`var`** declarations are hoisted and initialized to `undefined`; the assignment stays in place.
- **`let`** and **`const`** are hoisted in the sense that they are "reserved" in the block, but they are **not** initialized until the declaration line. Between the start of the block and that line they are in the **Temporal Dead Zone (TDZ)**. Accessing them in the TDZ throws `ReferenceError`.

---

## Senior scenarios

- **"Why does this throw?"** — e.g. `console.log(x); let x = 1;` → `x` is in the TDZ at the log line.
- **"In what order does the engine allocate?"** — Creation phase first (all declarations), then execution phase (assignments and rest of code).

---

## Interview focus

- Describe the Creation phase vs Execution phase in one or two sentences.
- Draw the scope chain for a nested function (current EC → outer EC → … → global).
- Explain why `let x; console.log(x); x = 1;` prints `undefined` but `console.log(x); let x = 1;` throws.

---

## Memory model (where variables live)

Variable bindings live in the **Environment Record** of their Execution Context (stack frame). Objects and closures may involve the **heap** and references. For stack vs heap, reference vs value, and closure retention, see [05-memory-model.md](05-memory-model.md).

---

## Practice

Run and study `practice/01-hoisting-and-tdz.js`. Predict the output before running, then add comments that explain creation vs execution phase for each snippet.
