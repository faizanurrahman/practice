## Concept: Value, entity, immutability, equals

### Theory index

- [MidLevelEqualsHashCodeContracts.md](theory/MidLevelEqualsHashCodeContracts.md)
- [SeniorValueVsEntityAndIdentity.md](theory/SeniorValueVsEntityAndIdentity.md)
- [StaffBuildersImmutabilityAndAPICost.md](theory/StaffBuildersImmutabilityAndAPICost.md)

### Deep dive

In this week, you stop "using" OO features and start encoding contracts.

Senior/staff engineering work in Java is dominated by correctness under change:
APIs evolve, objects move across boundaries, and performance or concurrency constraints surface.

The contracts you learn here are the ones that keep your systems stable:

- equality and hashing (HashMap/HashSet correctness and performance)
- immutability that is actually immutable (defensive copying, safe publication)
- builders that solve constructor complexity without introducing performance or encapsulation leaks
- ordering contracts for sorting and tree-based collections
- toString/observability that helps debugging without lying or crashing

---

## 1. Learning Objectives

After this week you should be able to:

- implement correct `equals` / `hashCode` for value objects (and understand why inheritance complicates them)
- reason about performance effects of equality in hashed and tree-based collections
- implement immutability safely when objects contain mutable references (arrays, lists, etc.)
- choose builders vs constructors vs records and predict trade-offs
- implement `Comparable`/`Comparator` correctly and avoid ordering bugs that break TreeMap/TreeSet
- answer staff-level interview questions that connect API contracts to JVM collection internals

---

## 2. Value vs Entity: Encoding Identity and Mutability

### 2.1 Mental Model - "Equality is a design decision"

Objects usually represent one of two things:

- **value**: defined by its state; two values are equal if their fields match
- **entity**: defined by identity; equality is often stable across time by using an ID

The equals/hashCode contract is where this decision becomes executable.

### 2.2 Failure Mode - Mutable fields inside hashed keys

If you use an object as a key in a `HashMap` (or store in a `HashSet`),
then changing fields that affect `equals`/`hashCode` after insertion breaks the collection invariants.

Symptoms:

- entries become unreachable
- `containsKey` returns false for keys that "should" exist
- memory leaks due to "lost" entries

---

## 3. equals / hashCode: The Complete Contract

### 3.1 The contract (must be preserved)

You must maintain:

- Reflexive: `x.equals(x)` is true
- Symmetric: `x.equals(y)` iff `y.equals(x)`
- Transitive: if `x.equals(y)` and `y.equals(z)` then `x.equals(z)`
- Consistent: repeated calls yield the same result if state doesn't change
- Non-nullity: `x.equals(null)` is false

And:

- if `x.equals(y)` then `x.hashCode() == y.hashCode()`

### 3.2 Implementation strategy for value objects

For immutable value objects, the safest pattern is:

1. make fields final (and defensively copied if needed)
2. make the class final to prevent subclass equality surprises
3. in `equals`, compare by class (or by an explicit rule you can justify)
4. in `hashCode`, compute based on exactly the fields used by equals

### 3.3 `instanceof` vs `getClass()` in senior code

Using `instanceof` can support equality across a hierarchy, but it introduces symmetry/transitivity risks.
Using `getClass()` enforces "same concrete type" equality and is often safer for value objects.

Senior insight: equality across inheritance is a high-risk design; prefer composition.

### 3.4 Performance notes: why equals can be expensive

Hash-based collections use:

- `hashCode` to choose a bucket
- `equals` to resolve collisions

If equals is expensive (deep comparisons, array traversal, recursion), collision-heavy workloads become slow.

Also note:

- hash collisions do not break correctness, but they break performance
- a poor hash function causes more equals invocations and worse CPU profiles

---

## 4. Immutability That Holds: Builders are Not a Guarantee

Immutability is broken by:

- exposing internal mutable state
- failing to defensively copy mutable inputs
- partial immutability where some fields are effectively mutable

### 4.1 Defensive copying rules

Common cases:

- Arrays: copy on input and copy on output (or store immutable wrappers)
- Lists: copy into an unmodifiable list and never return the mutable backing list
- Maps: same idea; consider unmodifiable wrappers after copying

### 4.2 Safe publication and final fields

Even immutable objects must be safely published.
`final` fields provide stronger visibility guarantees when correctly constructed and published.

If you violate construction safety (e.g., leak `this`), even `final` can become misleading.

---

## 5. toString: Observability without lying

### 5.1 Mental Model - toString is debugging output, not business logic

Good `toString` includes enough identity/state to debug:

- stable formatting
- avoids cycles (deep graphs can create stack overflows)
- does not allocate huge strings in hot paths

Senior note: for logging, consider structured logs or explicit `toString` versions used in observability boundaries.

---

## 6. Builders vs Telescoping Constructors vs Records

### 6.1 Why builders exist

Builders solve:

- many-parameter constructors readability
- optional parameters
- validation and invariants in `build()`

### 6.2 Performance trade-offs

Builders add:

- extra object allocations (builder itself)
- method calls (fluent setters)

In high-throughput paths, prefer:

- constructors for small, fixed field sets
- static factories for named creation and caching
- records for pure data carriers when the JVM/compiler fits your needs

### 6.3 When builders are inappropriate

Avoid builders when:

- object creation is extremely hot and allocation budgets are strict
- you cannot bound builder misuse (e.g., partial state if someone keeps the builder instance)

---

## 7. Comparable and Comparator: Ordering Contracts

### 7.1 Mental Model - ordering must be consistent

`Comparable` defines a natural order.
`Comparator` defines an external order.

For sorting/tree collections, your ordering must be:

- transitive
- antisymmetric (depending on definition)
- consistent (stable comparisons for the same logical state)

### 7.2 Comparator vs equals

Collections like `TreeSet` and `TreeMap` rely on ordering to determine uniqueness.
That means:

- two distinct objects can "collapse" to one entry if comparator returns 0
- this may differ from `equals`

Senior insight: decide whether your "set key" uniqueness should be based on comparator equality or equals.
Often, you want them consistent.

---

## 8. Senior/Staff Interview Questions (with deep answers)

### Q1: Implement equals/hashCode for a value object that contains a list.

Make a defensive copy of the list (e.g., `List.copyOf`) in the constructor.
Ensure you never expose the mutable backing list.
Then base equals on the list elements in order (or define an order-insensitive semantics explicitly).
Compute hashCode from the same fields used in equals.

### Q2: Why can subclassing break equals?

If subclasses add fields and you use `instanceof`, equality can become asymmetric or non-transitive:
`a.equals(b)` could be true while `b.equals(a)` is false, or equality can change as type hierarchy grows.
The safest approach for value objects is to make them `final` or carefully design the equality rule.

### Q3: How do you avoid hashCode performance issues?

Compute hashCode from a minimal set of stable fields.
Avoid `Objects.hash(...)` in extremely hot paths because it may allocate varargs arrays.
Consider precomputing hashCode for immutable value objects if needed.

### Q4: When does toString cause production issues?

When toString:

- allocates large strings in hot paths
- recursively prints cyclic object graphs
- accidentally triggers expensive lazy computations

In staff-level incidents, toString often appears in logs and can amplify failures (e.g., during exception storms).

### Q5: Explain a comparator bug that breaks TreeMap usage.

If comparator is not transitive or is inconsistent across calls, TreeMap invariants break.
The symptoms are missing entries, wrong retrieval, or even runtime exceptions depending on JDK behavior.

The fix is to make comparator purely derived from immutable key state and keep it consistent.

### Q6: Builders vs constructors: how do you decide?

Use constructors when:

- few parameters
- invariants are straightforward
- you want minimal allocations

Use builders when:

- many optional parameters
- you want validation centralized in build()
- readability and evolvability matter more than minor allocation overhead

---

## 9. Summary

Week 4 taught contracts:

- equality and hashing for correct behavior in hashed collections
- immutability rules and defensive copying
- toString for observability without failure amplification
- builders as an API design tool with known trade-offs
- ordering contracts for trees and sorted collections

Next: Week 5 (Robustness) will connect these contracts to error handling and testing so you can catch contract violations early.

---

## 10. Further Reading

- Effective Java (Joshua Bloch): equals/hashCode, builders, Comparable/Comparator
- JavaDoc for `HashMap`, `HashSet`, `TreeMap`, `TreeSet` (contract requirements)
