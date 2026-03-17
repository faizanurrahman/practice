# 01 — Execution Context and `this` Binding

## Learning objectives

- Explain what an **Execution Context (EC)** is and when it is created.
- Distinguish between **Global**, **Function**, and **Eval** execution contexts.
- Describe the **Creation phase** and **Execution phase** of an EC.
- Understand the internal structure: **LexicalEnvironment**, **VariableEnvironment**, and **ThisBinding**.
- Define **Lexical Environment** (Environment Record + outer reference) and how it forms the **scope chain**.
- Explain **hoisting** and the **Temporal Dead Zone (TDZ)** for `let` and `const`.
- Master the **five rules** for `this` binding and the “Battle of Bindings” order.
- Apply this knowledge to debug real‑world issues and answer senior‑level interview questions.

---

## 1. What Is an Execution Context?

An **Execution Context** is an abstract concept used by the JavaScript engine to manage the execution of code. Think of it as a **wrapper** or **environment** that holds all the necessary information for a particular piece of code to run: which variables are accessible, what `this` refers to, and how to look up variables in outer scopes.

Every time JavaScript code runs, it does so inside an execution context. There are three types:

1. **Global Execution Context (GEC)**  
   - Created when your script first starts.  
   - Only one GEC exists throughout the lifetime of the page.  
   - In browsers, it creates the global object (`window`) and sets `this` to that object (or `undefined` in ES modules).

2. **Function Execution Context (FEC)**  
   - Created **each time a function is invoked** (not when it is defined).  
   - Each function call gets its own independent FEC.

3. **Eval Execution Context**  
   - Created when code is executed inside the `eval()` function.  
   - Rarely used, but the same principles apply.

**Key insight:** The engine maintains a **stack** of these contexts – the **call stack**. The context at the top is the currently running code.

---

## 2. Internal Structure of an Execution Context

Every execution context is composed of three main components:

| Component            | Purpose                                                                                     |
|----------------------|---------------------------------------------------------------------------------------------|
| **LexicalEnvironment** | Holds bindings for identifiers declared with `let`, `const`, and `function` (except some legacy cases). Also stores captured variables for closures. |
| **VariableEnvironment**| Similar to LexicalEnvironment, but specifically for `var` declarations. It exists for historical reasons and helps implement the different hoisting behaviour of `var`. |
| **ThisBinding**       | The value of `this` inside that context.                                                    |

Both environments are **Lexical Environment** objects. Let’s dissect them.

### 2.1 Lexical Environment

A Lexical Environment is a structure with two parts:

1. **Environment Record** – the actual storage for variable/function bindings in that scope.  
   - **Declarative Environment Record** – used for function scopes, block scopes (with `let`/`const`), and `catch` clauses.  
   - **Object Environment Record** – used for the global scope and `with` statements. Here identifiers are resolved against a binding object (e.g., the global object).

2. **Reference to the outer environment** – a pointer to the Lexical Environment of the **parent** scope (the scope where the code was **written** lexically). This chain of outer references is the **scope chain**.

When the engine needs to resolve a variable name, it:
- Looks in the current Environment Record.
- If not found, follows the outer reference to the next Lexical Environment.
- Repeats until it either finds the binding or reaches the global environment (whose outer reference is `null`). If still not found, a `ReferenceError` is thrown in strict mode (or an implicit global is created in non‑strict).

### 2.2 LexicalEnvironment vs VariableEnvironment

- **LexicalEnvironment** is the “modern” environment for `let`, `const`, `class`, and function declarations (except those that behave like `var` in certain legacy situations).  
- **VariableEnvironment** is exclusively for `var` declarations.  

During the **Creation phase** of a context, the VariableEnvironment is often initialised as a **copy** of the LexicalEnvironment. Then, during the **Execution phase**, they diverge because `var` variables are hoisted and initialised with `undefined` immediately, whereas `let`/`const` remain uninitialised until their declaration line. This separation enables the **Temporal Dead Zone** for `let`/`const` while preserving `var` semantics.

### 2.3 ThisBinding

The `this` value is determined when the context is created, based on how the code is invoked. We’ll explore the rules in detail in Section 5.

---

## 3. The Two‑Phase Lifecycle of an Execution Context

Every execution context goes through two distinct phases **before any code runs** and **while code runs**.

### Phase 1: Creation Phase (Memory Allocation Phase)

1. **Create the LexicalEnvironment and VariableEnvironment**  
   - For each environment, the engine creates an Environment Record and sets the outer reference to the LexicalEnvironment of the **lexically enclosing function** (or `null` for global).  
   - For the **global context**, the outer reference is `null`. The Environment Record is an Object Environment Record backed by the global object.  
   - For a **function context**, the outer reference points to the environment where the function was **defined** (this is how closures work).

2. **Scan for declarations** and populate the environment records:
   - **`var` declarations**:  
     - Memory is allocated and the variable is initialised with `undefined`.  
     - If a function declaration has the same name, the function body overrides the `undefined`.
   - **`let` and `const` declarations**:  
     - Memory is allocated, but they are **not initialised**. They are in a special “uninitialised” state. Accessing them before the declaration line will throw a `ReferenceError`. This is the **Temporal Dead Zone**.
   - **Function declarations**:  
     - The entire function (name and body) is stored in the environment record, and it is available for use even before its declaration line (hoisting with full definition).

3. **Determine the `this` binding** based on the invocation context (see Section 5).

At the end of the creation phase, the context is fully set up, but no code has been executed. `var` variables already hold `undefined`; `let`/`const` are uninitialised; functions are ready to be called.

### Phase 2: Execution Phase

The engine starts running the code line by line, **assigning actual values** to variables and executing statements.

- For `var` variables, when the assignment line is reached, the `undefined` is replaced with the real value.
- For `let` and `const`, when the declaration line is reached, they are initialised with the given value (or `undefined` if none). From that point on, they can be used.
- If a `let` or `const` is never initialised (e.g., only declared), it remains uninitialised until the declaration, causing a TDZ error if accessed before.
- For function declarations, they are already fully available from the creation phase, so calls before the declaration line work.

---

## 4. Hoisting and the Temporal Dead Zone – Under the Hood

Consider this code:

```javascript
console.log(a); // undefined
var a = 5;

console.log(b); // ReferenceError: Cannot access 'b' before initialization
let b = 10;
```

**Step‑by‑step (global context):**

- **Creation phase**:
  - LexicalEnvironment: empty (no `let/const` yet), outer = `null`.
  - VariableEnvironment: copy of LexicalEnvironment.
  - Scan for `var a` → in VariableEnvironment, allocate `a` and initialise with `undefined`.
  - Scan for `let b` → in LexicalEnvironment, allocate `b` but leave it **uninitialised**.
  - `this` → global object.

- **Execution phase** (line by line):
  1. `console.log(a)` – looks up `a` in VariableEnvironment (since `var` lives there). Finds `undefined`. Prints `undefined`.
  2. `a = 5` – updates VariableEnvironment’s `a` to 5.
  3. `console.log(b)` – looks up `b` in LexicalEnvironment. It’s still uninitialised → throws `ReferenceError`.
  4. `let b = 10` – initialises `b` in LexicalEnvironment to 10 (but we never reach this line because the error is thrown).

**Key takeaway:** `let` and `const` are hoisted (memory is allocated), but they are **not initialised** until the declaration line. The period between entering the scope and the declaration is the **Temporal Dead Zone**.

---

## 5. `this` Binding – The Five Rules

The value of `this` inside a function is determined entirely by **how the function is called**, not where it is defined (with one exception: arrow functions). There are five rules, and they have a strict precedence.

### Rule 1: Default Binding

- **When?** A plain, undecorated function call, without any context object.
- **`this` value:**  
  - In non‑strict mode: the global object (`window` in browsers).  
  - In strict mode: `undefined`.
- **Example:**
  ```javascript
  function show() {
    console.log(this);
  }
  show(); // default binding → window (non‑strict) or undefined (strict)
  ```

### Rule 2: Implicit Binding

- **When?** The function is called as a method of an object (using the dot notation or square brackets).
- **`this` value:** The object before the dot.
- **Example:**
  ```javascript
  const obj = {
    name: 'My Object',
    show: function() { console.log(this.name); }
  };
  obj.show(); // implicit binding → obj
  ```
- **Gotcha:** If you extract the method and call it separately, you lose the implicit binding:
  ```javascript
  const fn = obj.show;
  fn(); // default binding now → global or undefined
  ```

### Rule 3: Explicit Binding

- **When?** You use `call`, `apply`, or `bind` to explicitly set `this`.
- **`this` value:** The object you pass as the first argument.
- **Example:**
  ```javascript
  function greet() { console.log(`Hello, ${this.name}`); }
  const user = { name: 'Alice' };
  greet.call(user); // explicit binding → user
  ```
- **`bind`** returns a new function permanently bound to the given `this`.

### Rule 4: `new` Binding

- **When?** The function is called with the `new` keyword (as a constructor).
- **`this` value:** A brand new object created by the engine, which is returned implicitly (unless you return an object explicitly).
- **Example:**
  ```javascript
  function Person(name) {
    this.name = name;
  }
  const p = new Person('Bob'); // new binding → the newly created object
  ```

### Rule 5: Arrow Functions

- **When?** The function is defined using arrow syntax (`=>`).
- **`this` value:** Not its own. It inherits `this` from the **enclosing lexical context** at the time the arrow function is **defined**. It cannot be changed, even with `call`/`apply`/`bind`.
- **Example:**
  ```javascript
  const obj = {
    name: 'Obj',
    regular: function() {
      const arrow = () => console.log(this.name);
      arrow(); // inherits this from regular’s this (obj)
    }
  };
  obj.regular(); // 'Obj'
  ```

### The “Battle of Bindings” (Precedence Order)

When multiple rules could apply, they follow this order from **strongest to weakest**:

1. **`new` binding** – always wins. It creates its own `this`.
2. **Explicit binding** (`call`/`apply`/`bind`) – wins unless `new` is used (and you can’t use `new` with a bound function unless the function was bound using `bind` in a way that allows it – but generally, `new` overrides explicit).
3. **Implicit binding** (obj.method()) – used if neither `new` nor explicit is present.
4. **Default binding** – the fallback (global or `undefined`).

**Arrow functions are not in this precedence** because they do not have their own `this`; they always use the `this` of the enclosing lexical scope.

---

## 6. Connecting Execution Contexts, Lexical Environments, and the Call Stack

- The **call stack** is a LIFO stack that holds the execution contexts in the order they are created. When a function is called, its context is pushed; when it returns, it is popped.
- Each context contains a **LexicalEnvironment** and a **VariableEnvironment**, which together form the scope chain via outer references.
- The scope chain is **lexical** – it follows the structure of the source code, not the call stack. That’s why a closure can access variables from its outer function even after that outer function has returned.

**Example trace:**
```javascript
function outer() {
  let a = 10;
  function inner() {
    console.log(a);
  }
  inner();
}
outer();
```
1. Global context is created and pushed.
2. `outer()` called → new function context for `outer` is created (creation phase: LexicalEnvironment gets `a` and `inner`, outer reference points to global). Push onto stack.
3. `inner()` called → new function context for `inner` (creation phase: LexicalEnvironment is empty, outer reference points to `outer`’s environment). Push.
4. `console.log(a)` – resolves `a` by looking in `inner`’s environment (not found), then following outer reference to `outer`’s environment, finds `a=10`. Prints.
5. `inner` returns → its context popped.
6. `outer` returns → its context popped.

---

## 7. Interview Questions and Answers

### Beginner

**Q1: What is an execution context?**  
**A:** An execution context is an internal JavaScript construct that manages the execution of code. It contains the variable bindings, scope chain, and `this` value for a particular piece of code. There is a global context for the whole script, and a new function context is created each time a function is invoked.

**Q2: What happens when a function is called?**  
**A:** When a function is called, a new function execution context is created and pushed onto the call stack. The creation phase sets up the LexicalEnvironment, VariableEnvironment, and `this` binding, and hoists declarations. Then the execution phase runs the code line by line. After the function returns, its context is popped from the stack.

**Q3: Is a new execution context created for every function call?**  
**A:** Yes – every invocation of a function creates a brand new execution context, even if it’s the same function being called recursively.

### Intermediate

**Q4: What lives inside an execution context?**  
**A:** Three main components:
- **LexicalEnvironment**: stores `let`, `const`, and function bindings, plus an outer reference to form the scope chain.
- **VariableEnvironment**: stores `var` bindings (initially a copy of LexicalEnvironment, but diverges during execution).
- **ThisBinding**: the value of `this` for that context.

**Q5: How does JavaScript resolve variable names across nested functions?**  
**A:** Through the **scope chain**, which is formed by the chain of outer references in each LexicalEnvironment. When a variable is used, the engine looks in the current environment record. If not found, it follows the outer reference to the parent environment, and so on, until it reaches the global environment. If still not found, a `ReferenceError` is thrown.

**Q6: How is function execution context different from global execution context?**  
**A:**  
- The global context is created once and lasts for the lifetime of the script. Its outer reference is `null`. Its environment record is an **Object Environment Record** tied to the global object.  
- Function contexts are created and destroyed with each function call. Their environment records are **Declarative Environment Records**. Their outer reference points to the environment of the lexically enclosing function (or the global context if none).

### Senior

**Q7: Explain the relationship between execution context, lexical environment, and call stack.**  
**A:**  
- The **call stack** is the runtime structure that holds execution contexts in the order they are entered.  
- Each **execution context** contains references to its **LexicalEnvironment** and **VariableEnvironment**.  
- Each LexicalEnvironment consists of an **Environment Record** (where variables are stored) and an **outer reference** that points to the LexicalEnvironment of the parent scope.  
- The chain of outer references forms the **lexical scope chain**, which is used for variable resolution **independent of the call stack**. This is why closures work: a function’s context may be popped, but its environment can be retained if referenced by a closure.

**Q8: Why does understanding execution context matter for debugging async code?**  
**A:** Async code (callbacks, promises, event handlers) runs later, often after the original context that created it has been popped. The environment captured by the callback (via closure) is part of the lexical environment chain. If you don’t understand what variables are captured, you may encounter stale values or memory leaks. Also, the value of `this` in callbacks can be surprising if you don’t know the binding rules. For example, losing implicit binding when passing a method as a callback is a classic pitfall.

**Q9: In a production bug, how would you decide whether the issue is context‑related or timing‑related?**  
**A:**  
- **Context‑related issues** often manifest as incorrect `this` values, stale data from closures, or variable resolution errors (`ReferenceError`). They usually depend on how the function was called or defined.  
- **Timing‑related issues** involve race conditions, unresolved promises, or incorrect order of async operations. They typically depend on when something runs relative to other tasks.  
- To differentiate, I would:  
  1. Reproduce the bug and log the call stack and variable values at the point of failure.  
  2. Check whether the function is bound correctly (is `this` what we expect?).  
  3. Examine if captured variables have the expected values – if they are unexpectedly `undefined` or old, it’s likely a context/closure issue.  
  4. If values change depending on event order or network latency, it’s timing.  
  5. Use breakpoints and the debugger to step through the creation vs execution of the relevant context.

---

## 8. Summary: The Battle of Bindings (Reminder)

```
Strongest to weakest:
1. new Binding
2. Explicit Binding (call/apply/bind)
3. Implicit Binding (obj.method())
4. Default Binding (global or undefined)

Arrow functions: always use lexical this (not in the battle).
```

---

## 9. Practice and Exploration

To solidify your understanding:

- Run the examples in this file in a browser console or Node with strict mode on/off.
- Experiment with nested functions and observe how the scope chain works.
- Use `console.trace()` to see the call stack at various points.
- Write a small program that demonstrates a closure and examine its retained environment in Chrome DevTools (Memory tab → heap snapshot).

Understanding execution contexts and `this` is the foundation for mastering JavaScript and, by extension, Angular. Every component, service, and directive you write will be evaluated within these rules.

---

## Further Reading

- [ECMAScript Specification – Execution Contexts](https://tc39.es/ecma262/#sec-execution-contexts)
- [You Don’t Know JS: Scope & Closures](https://github.com/getify/You-Dont-Know-JS/tree/2nd-ed/scope-closures)
- MDN on `this` and closures.

In the next chapter, we’ll build on this to explore the **Event Loop** and how asynchronous tasks are scheduled.

