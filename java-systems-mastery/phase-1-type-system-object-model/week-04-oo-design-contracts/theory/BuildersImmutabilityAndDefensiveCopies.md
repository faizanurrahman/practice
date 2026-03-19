## Builders, Immutability, and Defensive Copies (Correctness with Ergonomics)

Staff-level takeaway: builders are an API design tool, not a correctness guarantee by themselves.

### 1. Builders solve telescoping constructors

They improve readability when you have many optional parameters.
They also provide a central place to validate invariants at `build()`.

### 2. Builders do not automatically make you immutable

Immutability still requires:

- final fields in the constructed object
- defensive copies for mutable inputs
- no exposure of internal mutable state

### 3. Performance trade-offs

Builders add:

- extra objects (the builder itself)
- extra method calls

In extreme hot paths:

- prefer constructors for small fixed parameter sets
- consider static factories or records when semantics match

