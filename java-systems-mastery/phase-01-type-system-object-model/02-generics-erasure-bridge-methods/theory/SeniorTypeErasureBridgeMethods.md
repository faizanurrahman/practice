## Senior: type erasure and bridge methods

- At compile time, type parameters are **erased** to their leftmost bound or `Object`.
- The compiler inserts **casts** at read sites; wrong casts → `ClassCastException` at runtime.
- **Bridge methods** (synthetic) preserve polymorphism after erasure when a subclass refines return/parameter types.

**Debugging:** stack traces may point at synthetic casts — trace back to raw types / unchecked warnings.
