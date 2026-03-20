## Concept: Generics, erasure, bridge methods

### Theory index

- [MidLevelGenericsAndPECS.md](theory/MidLevelGenericsAndPECS.md)
- [SeniorTypeErasureBridgeMethods.md](theory/SeniorTypeErasureBridgeMethods.md)
- [StaffHeapPollutionAndReflection.md](theory/StaffHeapPollutionAndReflection.md)

### Deep dive

This week is about moving from "I know how to use generics" to "I can predict what the compiler emits and what the JVM will (or will not) protect you from at runtime".

Senior/staff engineers use generics to:

- encode correct variance and API contracts
- avoid heap pollution and runtime ClassCastException
- write libraries that remain type-safe across refactors
- understand why some generic checks exist only at compile time

---

## 1. Learning Objectives

After this week you should be able to:

- explain **type erasure** and its consequences on runtime type checks
- identify where the compiler inserts **synthetic casts** (and why they matter for stack traces)
- understand and reason about **bridge methods**
- use wildcards with intent (`? extends`, `? super`, PECS) and know when to avoid them
- understand why generic reflection works (Signature attribute) even though runtime erasure removes most parameterized types
- recognize and diagnose **heap pollution** and unchecked warnings
- predict inference behavior (diamond operator and failure cases)

---

## 2. The Type System Mental Model

### 2.1 Generics are compile-time constraints

In Java, generics provide:

- compile-time type checking
- stronger API contracts

At runtime, most generic type parameters are removed by the compiler via **erasure**.

Therefore:

- generic type safety is enforced by the compiler
- runtime safety depends on how you avoid raw types, unchecked casts, and heap pollution

### 2.2 Failure Mode - "The compiler was happy, production was not"

When you see a `ClassCastException` involving a generic type, it usually comes from:

- raw type usage
- unchecked casts
- heap pollution from inserting the wrong type into a parameterized container
- reflection or serialization paths that bypass compile-time checks

---

## 3. Type Erasure: What the Compiler Actually Emits

### 3.1 Erasure rules (core intuition)

For an unbounded type parameter `T`:

- it erases to `Object`

For bounded type parameters:

- `T extends Number` erases to `Number` (leftmost bound)
- multiple bounds erase to the first bound for bytecode typing

This is how the compiler can generate a single runtime representation for all instantiations.

### 3.2 Synthetic casts: where runtime checks hide

Consider:

```java
List<String> xs = ...;
String s = xs.get(0);
```

Because `List<String>` becomes raw `List` at runtime, the bytecode must:

1. load an `Object` from the underlying container
2. insert a cast to `String` at the use site

If the element is not actually a String, the cast fails at that line.

Senior insight: Stack traces often point at the *cast site*, not the *origin* of heap pollution.
Your debugging job becomes "find the earliest unchecked operation".

---

## 4. Bridge Methods: Preserving Polymorphism After Erasure

### 4.1 Mental Model - "Override after erasure"

Erasure can change method signatures.
When that would prevent a subclass method from truly overriding a superclass method, the compiler generates **bridge methods**.

Bridge methods are:

- synthetic (not source-level)
- inserted to preserve virtual dispatch and polymorphism
- typically forwarding calls with casts

### 4.2 Practical impact

In bytecode:

- you may see both the "real" typed method and the synthetic bridge method
- reflection and stack traces can include synthetic frames

This is one reason framework authors sometimes prefer bytecode inspection tools (ASM, javap) rather than relying only on source signatures.

---

## 5. Wildcards and Variance: Correct Use of `extends` and `super`

### 5.1 `? extends T` (producer) and `? super T` (consumer)

`List<? extends Number>`:

- you can safely read elements as `Number`
- you cannot add non-null values, because the true element type is unknown

`List<? super Integer>`:

- you can add `Integer`
- reading yields `Object`

### 5.2 PECS as an API design rule

PECS = Producer Extends, Consumer Super.

If a parameter is a source of values, accept `? extends T`.
If it is a sink for values, accept `? super T`.

### 5.3 When to avoid wildcards

Wildcards improve flexibility, but they can also:

- complicate inference
- make APIs harder to read
- create verbose capture situations

Senior heuristic:

- if you control both sides of the call (internal code), prefer explicit type parameters
- reserve wildcards for library boundaries where variance matters

---

## 6. Inference and Diamond Operator: Helpful, but not magic

### 6.1 What the compiler infers

The diamond operator `<>` tells the compiler to infer type arguments from:

- assignment context
- method invocation context
- bounds constraints

### 6.2 Failure Mode - inference gets ambiguous

Inference can fail in scenarios like:

- nested generics with insufficient target typing
- chained calls where intermediate types are not strongly constrained
- method overloads that create multiple viable inference solutions

Fix strategies:

- add explicit type witnesses, e.g. `MyGeneric.<String>method(...)`
- split a statement into two with an intermediate variable

---

## 7. Heap Pollution and Unchecked Warnings

### 7.1 Definitions

Heap pollution is when:

- a parameterized type variable points to an object that is not actually of that parameterized type

Raw types and unchecked casts are the classic causes.

### 7.2 The real rule: unchecked warnings are permission to prove safety

Unchecked warnings are not "turn them off and move on".
They mean "you must know what you're doing".

Senior move: isolate the unchecked operation into a small, well-audited helper.
Then return a value typed safely for callers.

---

## 8. Generic Reflection: Signature attribute survives erasure (mostly)

Although runtime instances lose parameterized type information, the class file can still store it as metadata:

- the `Signature` attribute preserves generic type signatures for classes, methods, and fields

This allows reflective frameworks to recover generic information.

Performance note:

- reflective generic inspection is expensive; cache results in framework code

---

## 9. Senior/Staff Insights

What to internalize:

- erasure explains why "generic runtime checks" are limited
- bridge methods explain odd bytecode/reflection artifacts
- wildcards are a variance contract language, not syntax sugar
- heap pollution is the root cause behind most surprising runtime failures

---

## 10. Senior-Level Interview Questions (with deep answers)

### Q1: Why does `List<String>.class` not exist?

Because `List<String>` is not a reified runtime type after erasure.
At runtime, the JVM only knows it is a `List`.
Generic type parameters are removed, so you cannot represent `List<String>` as a distinct runtime class literal.

### Q2: What is a bridge method and why is it generated?

Bridge methods are synthetic methods created to preserve polymorphism after erasure.
They forward calls between erased signatures and typed implementations using casts.

### Q3: When would you choose `? extends T` vs `? super T`?

Use `? extends T` when the parameter produces values (read-only semantics).
Use `? super T` when the parameter consumes values (write semantics).
PECS is the guiding rule.

### Q4: Explain heap pollution and give a deterministic way to cause it.

Use raw types to bypass generic checks, insert a different type into the container, then retrieve as the parameterized type.
The cast at the retrieval site triggers ClassCastException.

### Q5: Does type erasure always mean "no generic type checks at runtime"?

No. The erased type may still carry runtime checks at cast sites.
However, you cannot check parameterized identity like `List<String>` vs `List<Integer>` at runtime.

### Q6: How do frameworks still know generic types (e.g., `List<String>`)?

They rely on class file metadata stored in Signature attributes and reflection over generic method/field signatures.
The generic information is present as metadata, not as reified runtime types on objects.

---

## 11. Summary

Week 2 tied together:

- type erasure and synthetic casts
- bridge methods and polymorphism preservation
- wildcards and PECS for correct variance
- inference failure patterns and how to resolve them
- heap pollution and unchecked warning handling
- reflection via Signature metadata

Next: Week 3 turns these concepts into a bytecode-level view so you can see invokedynamic, lambda compilation, and common instruction patterns.

---

## 12. Further Reading

- Java Language Specification: Generics and Type Inference sections
- Java Virtual Machine Specification: class file structure and invocation instructions
- "Effective Java" (for variance and equals/hashCode/builders)
