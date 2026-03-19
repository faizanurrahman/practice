## invokedynamic and LambdaMetafactory (Why Lambdas are More than Syntax)

Staff-level takeaway: lambdas compile into runtime linkage machinery, not just anonymous classes.

### 1. The lambda call site is linked at runtime

The compiler emits `invokedynamic` for lambda expressions and method references.
At runtime, the JVM runs a bootstrap method (commonly via `LambdaMetafactory`) to:

- determine the functional interface target method shape
- connect the call site to a factory method/handle for producing lambda instances

After linkage, the call site is typically cached and reused.

### 2. Capturing vs non-capturing affects allocation behavior

Non-capturing lambdas can often be represented more efficiently (instance reuse).
Capturing lambdas need to carry captured state, which can force additional lambda instance creation.

### 3. Why call-site stability matters for JIT

The JIT benefits when:

- call targets are stable
- polymorphism at the lambda call path is predictable

In practice, lambda overhead usually disappears in hot code, but poor design can preserve allocation and polymorphism costs.

