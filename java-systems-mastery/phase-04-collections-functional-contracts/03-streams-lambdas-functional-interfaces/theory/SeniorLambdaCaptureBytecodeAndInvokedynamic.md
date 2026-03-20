## Senior: lambda capture and `invokedynamic`

- Lambdas compile to **`invokedynamic`** + `LambdaMetafactory` linkage (see Phase 00 bytecode concept).
- **Capturing** lambdas may allocate **closure objects**; non-capturing may reuse singletons.
- **Method references** (`String::length`) are also `invokedynamic`-linked shapes.

**Performance:** hot streams + heavy capture → allocation noise in JFR.
