## Week 03 - Bytecode, invokedynamic, and Dispatch Costs (A Senior View)

This week trains your eyes.
You will learn to connect:

- what javac emits
- what the JVM interprets initially
- what the JIT later optimizes

Senior/staff performance and debugging work often requires that you can inspect what the compiler did
and predict what the runtime will likely do.

---

## 1. Learning Objectives

You should be able to:

- describe the `.class` file layout (constant pool, fields, methods, attributes)
- explain what the `Code` attribute contains (bytecode + exception table + stack map tables)
- recognize common bytecode patterns for:
  - object creation and constructor calls (`new/dup/invokespecial`)
  - field access (`getfield/putfield`, `getstatic/putstatic`)
  - virtual and interface calls (`invokevirtual`, `invokeinterface`)
- understand how lambdas are compiled using `invokedynamic` and `LambdaMetafactory`
- reason about why call-site stability and polymorphism shape affect inlining and performance
- use tools (`javap`) to validate assumptions quickly

---

## 2. Mental Model - Source code is not what the CPU runs

Java code is compiled into bytecode.
At runtime:

1. the JVM verifies and interprets bytecode (initially)
2. the JIT compiles hot methods into native machine code
3. optimizations (inlining, constant folding, escape analysis) depend on runtime profiling

Therefore, correctness and performance are both shaped by what the compiler emitted and what the JIT can prove.

---

## 3. Class File Structure (What is inside a `.class`)

Every `.class` file contains:

1. **magic**: `0xCAFEBABE`
2. **minor_version / major_version**
3. **constant_pool**: symbolic literals and references
4. **access_flags**, **this_class**, **super_class**
5. **interfaces_count / interfaces**
6. **fields_count / fields_info**
7. **methods_count / methods_info**
8. **attributes_count / attributes_info**

Key attribute types:

- `Code`: holds the bytecode instructions for a method
- `Exceptions`: declared thrown exceptions
- `LineNumberTable`: mapping for stack traces and debugging
- `LocalVariableTable`: debug info (sometimes absent)
- `Signature`: preserved generic signatures for reflection
- `StackMapTable`: used by the verifier for type state at control flow join points

---

## 4. Bytecode Patterns You Must Recognize

### 4.1 Object creation: `new`, `dup`, `invokespecial`

Typical pattern for `new C()`:

- `new C` (allocate memory for the object)
- `dup` (duplicate the reference token so both constructor and later code can use it)
- `invokespecial C.<init>` (call constructor)

In bytecode, you often see `new/dup/invokespecial` together.

This pattern helps you:

- identify constructor costs in hot code
- spot accidental allocation in performance regressions

### 4.2 Field access

- instance fields: `getfield` / `putfield`
- static fields: `getstatic` / `putstatic`

If a field is not `final`, JIT must respect potential aliasing and memory effects.
That can affect optimizations around common subexpressions and instruction reordering.

### 4.3 Virtual calls: `invokevirtual` vs interface calls

- `invokevirtual`: method resolved using class hierarchy dispatch
- `invokeinterface`: resolved through interface method tables

The JIT can devirtualize (turn polymorphic calls into monomorphic ones) if it can prove receiver type stability.
If it cannot, call overhead remains.

---

## 5. invokedynamic and Lambdas: Why It Exists

### 5.1 Mental Model - "A lambda is a runtime recipe"

Lambdas are not merely compiler sugar.
They compile into:

- a synthetic method containing the lambda body (for simple cases)
- an `invokedynamic` instruction that at runtime links the lambda to a callable instance

The bootstrap method is typically:

- `java.lang.invoke.LambdaMetafactory`

What happens:

- at the first execution of the call site, the JVM runs the bootstrap method
- the bootstrap returns a "factory" target (a function that produces lambda instances)
- the call site is cached for subsequent executions

### 5.2 Capturing vs non-capturing lambdas (allocation behavior)

Capturing lambdas (capturing variables) generally require a lambda instance that stores captured state.
Non-capturing lambdas can often reuse instances.

That is why "move logic into stateless lambdas" can reduce allocations in some workloads.

---

## 6. Bytecode to JIT: The Call-Site Story

The JIT optimization pipeline cares about:

- method hotness (profiling)
- polymorphism at call sites
- escape analysis results
- inlining profitability heuristics

If a method dispatch site is stable (few receiver types), the JIT is more likely to inline.
Inlining then enables further optimizations:

- constant propagation
- dead code elimination
- loop optimizations

If a call site is megamorphic, the JIT may keep it as an indirect call,
so you see overhead in CPU profiles.

Senior insight: don't only look at the line of code.
Look at how many times the call site executes and how stable the receiver types are.

---

## 7. Tools: `javap` as a Diagnostic Lens

Use:

- `javap -c ClassName` for readable bytecode
- `javap -v ClassName` for constant pool and detailed attributes
- `javap -c -v ClassName$InnerClass` for nested/synthetic classes

Tips:

- search for `invokedynamic` to find lambda linkage
- search for `new` to identify allocations
- compare `invokevirtual` vs `invokespecial` in inheritance patterns

---

## 8. Senior-Level Interview Questions (with deep answers)

### Q1: What is the `Code` attribute and why does it matter?

`Code` contains the actual bytecode for a method plus exception handling metadata.
It also includes `StackMapTable`, which guides bytecode verification.
Without the `Code` attribute (abstract/native methods), the JVM cannot execute bytecode for that method.

### Q2: Why does object construction look like `new/dup/invokespecial` in bytecode?

`new` creates an uninitialized object reference.
The constructor needs that reference to run initialization logic.
`dup` duplicates the reference token because the JVM needs it both for constructor invocation and to leave a reference on the stack for later use.

### Q3: Explain `invokedynamic` for lambdas at a high level.

Lambdas compile into an `invokedynamic` call site.
The runtime uses a bootstrap method (often `LambdaMetafactory`) to link the call site to a target that can create (or reuse) lambda instances.

### Q4: What determines whether lambda overhead remains after JIT?

JIT can inline lambda bodies if:

- the lambda instance and call targets are stable enough
- the call graph becomes resolvable
- allocation/escape analysis shows objects do not require heap materialization

If capturing state prevents optimization or polymorphism remains high, overhead can persist.

### Q5: What is StackMapTable used for?

It is used by the verifier to ensure type safety across control-flow joins.
It allows verification without needing a costly full analysis at every load.

---

## 9. Summary

Week 3 gave you:

- `.class` structure and method attributes mental model
- bytecode instruction patterns for object creation, fields, and dispatch
- why `invokedynamic` is a runtime linkage mechanism for lambdas
- the call-site stability story connecting bytecode to JIT optimizations

Next: Week 4 converts these mechanics into OO design contracts:
how to implement equals/hashCode correctly, design immutability that is actually immutable, and build APIs that don't surprise your users or your profiler.

---

## 10. Further Reading

- JVM Specification: class file format, verification, invocation instructions
- `javap` documentation (JDK tools)
- Java Concurrency in Practice (for linking performance expectations to visibility and ordering)
