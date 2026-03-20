## Concept: Object layout, header, alignment, dispatch

### Theory index

- [MidLevelObjectHeaderAndPadding.md](theory/MidLevelObjectHeaderAndPadding.md)
- [SeniorMarkWordKlassPointerAndLocking.md](theory/SeniorMarkWordKlassPointerAndLocking.md)
- [StaffFieldReorderingAndAlignment.md](theory/StaffFieldReorderingAndAlignment.md)

### Deep dive (object model week)

This concept is not a "syntax recap". It is a *reasoning toolkit* for senior/staff engineers:
you will learn to predict how Java *behaves* based on the JVM execution model.

By the end, you should be able to explain:

- how primitives vs references map to machine/JVM realities (and why it affects performance)
- how objects are laid out (header + fields + alignment) and what that implies for cache locality
- how the JVM dispatches method calls (invokestatic, invokespecial, invokevirtual, invokeinterface)
- why constructor initialization order can break invariants
- what immutability really guarantees (and what it does not) under concurrency
- the practical meaning of "safe publication" and the special role of `final` fields (canonical detail: [Phase 03 / JMM](../../phase-03-concurrency-jmm-modern-execution/00-jmm-happens-before-safe-publication/notes.md))

---

## 1. Learning Objectives

You will build a mental model that connects:

- **source code** (what you write)
- **bytecode** (what the compiler emits)
- **JVM execution** (what happens at runtime, including dispatch and memory visibility)
- **design consequences** (what makes code correct and fast)

Keep a note of "failure modes" while reading. Each section includes one or more.

---

## 2. Primitives and References: What Your Code Really Contains

### 2.1 Mental Model - "Variables are not values"

In Java, variables have types, and the *category* of the type matters:

- **primitives** (like `int`) store the raw bits directly
- **references** store an address-like token that points to an object

When you pass an object into a method, you pass the reference token. You do not copy the object.
That single distinction explains:

- why object identity is stable (within the same object lifetime)
- why aliasing can happen (two references can point to one object)
- why immutability is primarily about *preventing aliasing-based mutation*

### 2.2 Internal Mechanism - Local values vs heap objects

- Local variables for primitives live in the JVM's local variable area (conceptually like registers/stack slots).
- Object instances live on the heap. Their fields are part of the heap allocation.
- Reference variables store a pointer/offset to that heap allocation.

This is also why boxing hurts:

- `int` is a raw value (no allocation)
- `Integer` is an object allocation (allocation + later GC + pointer indirection)

### 2.3 Failure Mode - "Boxing is invisible until you scale"

At small scale, boxing may look harmless.
At production scale it becomes:

- allocation rate pressure (GC frequency increases)
- latency spikes (GC pauses or allocator contention)
- throughput degradation (more pointer chasing, less locality)

Senior insight: treat allocations as a first-class performance budget.
If a path executes millions of times, "small" allocations are rarely small anymore.

---

## 3. Object Layout: The JVM Memory Geography

### 3.1 Mental Model - An object is a memory block with a header

Every object has:

1. **object header**
2. **instance fields** (primitives inline, references as pointers)
3. **padding/alignment**

The header exists so the JVM can manage:

- identity (hash code / mark word)
- locking state (biased/light/heavy)
- GC metadata (generation/age)
- pointer to class metadata (klass pointer)

### 3.2 Internal Mechanism - Compressed OOPs and why your size "doesn't add up"

On 64-bit HotSpot with compressed ordinary object pointers (compressed OOPs),
references are stored in fewer bytes than you might expect.

That means:

- the size of references depends on JVM configuration (heap size, flags)
- the "naive math" of `header + fields` often surprises people

Practical implication: when you care about memory footprint, measure with JOL (Java Object Layout).
Assume your intuition will be wrong by a small but real factor.

### 3.3 Field layout and cache locality

HotSpot may reorder fields for alignment/performance.
Even if you don't know the exact offsets, the *consequence* is clear:

- frequently accessed fields grouped together can improve cache locality
- false sharing happens when two threads mutate different fields that land on the same cache line

In staff-level workloads (counters, ring buffers), this is a common hidden bottleneck.
You may later use padding (`@Contended`) or redesign data structures to avoid it.

### 3.4 Arrays: objects with a length

Arrays are special:

- they are still objects (have a header)
- they include a length field
- their elements are stored contiguously (primitives inline; references as pointers)

This is why array-based representations often beat node-based structures in throughput.

---

## 4. Method Dispatch: How Calls Become Machine Actions

### 4.1 Mental Model - "Different invocations are different lookup strategies"

The JVM provides different invocation bytecodes because lookup differs:

- `invokestatic` - static methods resolved without receiver polymorphism
- `invokespecial` - constructors, private methods, super calls
- `invokevirtual` - virtual dispatch for class methods
- `invokeinterface` - dispatch for interface methods

The *design* implication:
polymorphism costs something. The key question is whether that cost matters in your hot path.

### 4.2 Dispatch internals: inline caching and polymorphism shape

The JVM and JIT optimize repeated dispatch:

- stable receiver type at a call site can be optimized aggressively (monomorphic)
- multiple receiver types can degrade optimization (megamorphic)

Senior insight: performance often correlates with *type stability* at call sites.
That can be influenced by design:

- use `final`/sealed hierarchies where appropriate
- avoid "everything is an interface" when dispatch sits in a hot loop

### 4.3 Failure Mode - "Polymorphic call sites become a tax"

When call sites see many different receiver classes, the JIT's ability to inline or optimize decreases.
You may feel this as:

- higher CPU usage
- fewer inlining opportunities
- worse tail latency

---

## 5. Initialization Order: Constructors Are Where Invariants Get Broken

### 5.1 Mental Model - Initialization is layered

When creating an instance:

1. superclass is initialized first
2. instance fields and instance initializers run
3. constructor body runs

Also:

- field initializers are compiled into each constructor (after the superclass constructor call)
- static initializers run once per class (first use / first load semantics)

### 5.2 Failure Mode - Calling overridable methods from constructors

If a constructor calls a method that can be overridden,
the override may observe fields that have not yet been initialized in the subclass.

This is a correctness bug, not a style nit.

---

## 6. Immutability and Safe Publication: Correctness Under Concurrency

### 6.1 Mental Model - Immutability is a visibility guarantee *plus* an aliasing guarantee

An immutable object is one whose state cannot change after construction.
But there are two different concerns:

1. **No mutation after publication** (aliasing and defensive copying)
2. **All threads see the fully constructed state** (safe publication and JMM rules)

### 6.2 Internal Mechanism - "final" fields get special JMM semantics

In the Java Memory Model:

- properly constructed and safely published objects with **final fields**
  allow threads to observe the value written in the constructor for those final fields.

This is why many immutable designs are easier to reason about:
reads don't need synchronization for the final fields.

### 6.3 Failure Modes - "Immutable contains mutable" and "this escapes"

Even with `final` references:

- if you store a reference to a mutable object (like an array or `List`),
  immutability is not real unless you defensively copy and avoid exposing internal references
- if `this` escapes during construction (e.g., storing it into a global/static field),
  other threads may observe a partially initialized object

---

## 7. Senior/Staff Insights

This week should change how you design:

- Prefer *value-like objects* for data with clear identity semantics.
- Treat constructor initialization and method dispatch as part of correctness.
- Use `final` to encode immutability and to unlock JMM benefits (when properly published).
- When performance matters, model dispatch shape and allocation budgets.

---

## 8. Senior-Level Interview Questions (with deep answers)

### Q1: Why is `invokeinterface` often slower than `invokevirtual`?

`invokeinterface` needs to resolve interface method dispatch.
At runtime, the JVM must locate the appropriate implementation for the interface method.
While the JIT uses inline caching, the baseline lookup is more complex than class vtable dispatch.

In practice, the difference is usually small compared to allocation, lock contention, and cache effects,
but it can matter in tight hot loops with high polymorphism.

### Q2: What is the correctness risk of calling overridable methods from a constructor?

The subclass's fields may not be initialized yet.
The overridden method can read fields that still have default values (null/0/false),
breaking invariants and producing subtle bugs that are hard to reproduce.

### Q3: Explain "safe publication" in terms of the Java Memory Model.

Safe publication means other threads will observe a fully initialized object.
In Java, rules like synchronization, volatile writes/reads, thread start/join, and final field semantics
provide happens-before edges that ensure visibility of constructor writes.

### Q4: How do `final` fields relate to immutability and concurrency safety?

`final` fields have stronger JMM visibility guarantees for properly constructed objects.
If an object is safely published, threads that observe the reference will also observe the final fields
as written in the constructor, without requiring additional synchronization for those fields.

This does not eliminate aliasing risks (e.g., mutable arrays inside final references).

### Q5: Why can "immutable" classes still be broken?

Common reasons:

- exposing internal mutable structures
- failing to defensively copy mutable inputs
- leaking `this` during construction
- allowing subclassing to mutate internal state (if the class is not designed for extension)

### Q6: How does polymorphism shape affect JIT optimization?

If the JVM sees stable receiver types at a call site (monomorphic),
it can inline and optimize the call aggressively.
If it sees many receiver types (megamorphic), inlining and devirtualization become harder,
so the call overhead remains higher in CPU profiles.

---

## 9. Summary

Week 1 connected:

- primitives vs references (values vs heap allocations)
- object layout (header + fields + alignment; implications for memory and locality)
- method dispatch (lookup strategy and optimization shape)
- constructor initialization order pitfalls (why invariants can break)
- immutability and safe publication (final fields, visibility, and construction safety)

Next: Week 2 turns to generics internals (erasure, bridges, wildcards) so you can predict where runtime behavior surprises you.

---

## 10. Further Reading

- Java Language Specification (JLS): Execution, Types, and Memory Model sections
- JVM Specification: Method invocation and class loading semantics
- JOL (Java Object Layout): measure object headers and field sizes in your exact JVM configuration
- Brian Goetz and Java Concurrency in Practice (safe publication, initialization, and JMM rules)

