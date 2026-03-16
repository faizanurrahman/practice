# Debug Log

For each topic: **break the example on purpose**, then **explain why it broke**.

---

## Template (copy for each topic)

### Topic: [name]

**What I broke:**  
(e.g. removed the `return`, passed `null`, etc.)

**What happened:**  
(error message or wrong output)

**Why it broke (one line):**  
…

---

## Example: Closure

**What I broke:**  
Called the inner function after the outer had returned and the outer’s local variable was “gone.”

**What happened:**  
The inner function still printed the correct value (e.g. the counter).

**Why it broke (or didn’t):**  
The inner function holds a **reference** to the outer Lexical Environment, so that environment is not GC’d. The variable is still there for the closure — so it didn’t “break,” it proved how closure keeps the reference.

*(Use this file to log real breaks: wrong type, missing unsubscribe, bad dependency order, etc.)*
