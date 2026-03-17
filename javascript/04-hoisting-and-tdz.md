# 04 — Hoisting and Temporal Dead Zone (TDZ)

## Learning objectives

- Define **hoisting** in terms of the creation phase and binding allocation.
- Differentiate the hoisting behaviour of `var`, `let`, `const`, and function declarations.
- Explain the **Temporal Dead Zone (TDZ)** and why `let`/`const` throw errors when accessed early.
- Understand why “moving declarations to the top” is an oversimplification.
- Distinguish between function **declarations** and function **expressions** in the context of hoisting.
- Recognise common hoisting‑related pitfalls in code reviews and daily development.

---

## 1. What is hoisting?

**Hoisting** is a JavaScript mechanism where variable and function **declarations** are processed before any code is executed. During the **creation phase** of an execution context, the engine scans for declarations and allocates memory for them. The actual assignments happen later, during the **execution phase**.

It is crucial to understand that hoisting is **not** the physical moving of code to the top of the file. Rather, it is a metaphor for how the engine sets up bindings in memory before executing the code.

### Senior mental model

Instead of thinking “JavaScript moves declarations to the top,” think:

> During the creation phase, the engine records all identifiers (variable and function names) and sets up bindings in the LexicalEnvironment or VariableEnvironment. Only after that does execution begin.

This model explains not only why `var` is `undefined` before assignment, but also why `let` and `const` are inaccessible before their declaration line (the TDZ) and why function declarations are fully available.

For a deeper view of creation vs execution, see `01-execution-context-and-memory.md`.

---

## 2. Hoisting behaviour of different declarations

| Declaration Type           | Hoisting Behaviour                          | Initial Value | Temporal Dead Zone?          |
|----------------------------|---------------------------------------------|---------------|------------------------------|
| `var`                     | Declaration hoisted; assignment not         | `undefined`   | No                           |
| `let`                     | Declaration hoisted; binding uninitialised | –             | Yes, until declaration line  |
| `const`                   | Same as `let` (must be initialised)        | –             | Yes, until declaration line  |
| Function declaration      | Name + body hoisted (full function)        | Function      | No                           |
| Function expression (`var f = function..`) | Only the variable is hoisted; value assigned later | `undefined` (for `var`) | TDZ if `let`/`const`; `undefined` for `var` |

### 2.1 `var` hoisting

```javascript
console.log(a); // undefined
var a = 10;
console.log(a); // 10
```

**Creation phase:**

- Allocates a binding for `a` in the VariableEnvironment.  
- Initialises `a` with `undefined`.

**Execution phase:**

1. `console.log(a)` → reads `a` → `undefined`.  
2. `a = 10` → assigns `10`.  
3. `console.log(a)` → `10`.

### 2.2 `let` and `const` hoisting

```javascript
console.log(b); // ReferenceError: Cannot access 'b' before initialization
let b = 20;
```

**Creation phase:**

- Allocates a binding for `b` in the LexicalEnvironment.  
- Leaves `b` **uninitialised**.

**Execution phase:**

1. Accessing `b` before its declaration sees an uninitialised binding → `ReferenceError`.  
2. `let b = 20` initialises `b` with `20`; after that, it can be used.

`const` behaves the same, but must be initialised at declaration (you cannot write `const c;`).

### 2.3 Function declaration hoisting

```javascript
sayHi(); // \"Hello!\"

function sayHi() {
  console.log('Hello!');
}
```

**Creation phase:** the entire function (name and body) is stored in the environment, so it is callable before its definition line.

### 2.4 Function expression hoisting

```javascript
sayHello(); // TypeError: sayHello is not a function

var sayHello = function () {
  console.log('Hello!');
};
```

**Creation phase:** `var sayHello` is hoisted and initialised with `undefined`.  
**Execution phase:** calling `sayHello()` before the assignment calls `undefined` as a function → `TypeError`.

If we use `let`:

```javascript
sayHello(); // ReferenceError
let sayHello = function () { ... };
```

Now `sayHello` is in the **TDZ** until the declaration line.

---

## 3. The Temporal Dead Zone (TDZ) in depth

The **Temporal Dead Zone** is the time between entering a scope (where a `let`/`const` binding is created) and the actual declaration line. During this time, the variable exists but is **uninitialised**, and any access results in a `ReferenceError`.

```javascript
{
  // TDZ for x starts here
  // console.log(x); // ReferenceError
  let x = 5;         // TDZ ends here
  console.log(x);    // 5 – now safe
}
```

### Why TDZ exists

The TDZ catches programming errors where you try to use a variable before its intended initialisation. If `let` behaved like `var` (hoisted with `undefined`), you could get subtle bugs instead of immediate feedback.

### TDZ and `typeof`

```javascript
typeof x; // ReferenceError (x is in TDZ)
let x;
```

But for an undeclared identifier:

```javascript
typeof y; // \"undefined\" – no error
```

This difference helps you reason about TDZ vs truly undeclared variables.

---

## 4. Common hoisting / TDZ pitfalls

### 4.1 Assuming `let`/`const` are “not hoisted”

They **are** hoisted – their bindings exist from the start of the scope – but they are **uninitialised** until the declaration line, which is why accessing them early throws.

### 4.2 Relying on `var` hoisting for behaviour

```javascript
function badExample() {
  console.log(message); // undefined – surprising
  var message = 'Hello';
}
```

It works, but is confusing. Prefer declaring variables before use with `let`/`const`.

### 4.3 Function declarations in blocks

```javascript
if (condition) {
  function sayHi() { console.log('Hi'); }
} else {
  function sayHi() { console.log('Hello'); }
}
sayHi(); // behaviour varies across environments
```

Function declarations inside blocks are historically inconsistent; prefer function expressions.

### 4.4 TDZ in loops

```javascript
for (let i = 0; i < 3; i++) {
  // i is in TDZ before the loop initialiser runs,
  // then has a new binding per iteration.
}
```

Understanding TDZ in loops is essential for decoding closure behaviour with `let` vs `var`.

---

## 5. Senior angle: creation phase and bindings

Hoisting is a consequence of the **creation phase** of an execution context:

- The engine builds the LexicalEnvironment and VariableEnvironment.  
- For `var`, it adds a binding in VariableEnvironment and initialises it to `undefined`.  
- For `let`/`const`, it adds a binding in LexicalEnvironment but leaves it uninitialised.  
- For function declarations, it stores the entire function object.

Only **after** this phase does execution begin. This explains:

- Why `var` is visible (as `undefined`) before its assignment.  
- Why `let`/`const` throw in the TDZ.  
- Why function declarations can be called before their definition line.

---

## 6. Interview questions

### Beginner

- **What is hoisting?**  
  Hoisting is JavaScript’s behaviour where variable and function declarations are processed before executing any code. The engine allocates bindings during the creation phase of the execution context.

- **Why does a `var` variable log `undefined` before assignment?**  
  Because its binding is created and initialised to `undefined` during the creation phase; the assignment happens later.

- **What is the Temporal Dead Zone (TDZ)?**  
  The time between entering a scope and the line where a `let`/`const` variable is initialised, during which accessing it throws a `ReferenceError`.

### Intermediate

- **How do function declarations differ from function expressions with respect to hoisting?**  
  Declarations are fully hoisted (name + body); expressions only hoist the variable, not the value.

- **What happens here?**  
  ```javascript
  console.log(x);
  let x = 5;
  ```  
  A `ReferenceError` is thrown because `x` is in the TDZ.

- **How would you explain hoisting without using the word “move”?**  
  Talk about **binding creation during the creation phase**, not physical code movement.

### Senior

- **Why is “JS moves declarations to the top” an incomplete explanation?**  
  It misrepresents the process and cannot explain TDZ. The correct model is creation‑phase binding allocation with different initialisation semantics.

- **Contrast binding creation for `var`, `let`, `const`, and function declarations.**  
  Map each to VariableEnvironment vs LexicalEnvironment and their initial values.

---

## 7. Practice

Use `practice/01-hoisting-and-tdz.js`:

- **Goal:** Predict the output or error for each snippet, based on the creation/execution model.  
- **Flow:** For each block, write down what bindings exist and their initial values before execution, then step through line by line.  
- **Break:** Uncomment the TDZ lines to see actual errors; add your own examples (e.g., function expressions with `let`).  
- **Explain:** In `workspace/debug-log.md`, describe *why* each example behaved as it did using the language of LexicalEnvironment, VariableEnvironment, and TDZ.

