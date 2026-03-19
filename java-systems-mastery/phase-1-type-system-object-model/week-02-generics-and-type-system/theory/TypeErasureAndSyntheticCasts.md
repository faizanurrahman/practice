## Type Erasure and Synthetic Casts (Where Runtime Checks Actually Happen)

Staff-level takeaway: generics guarantee safety mostly at compile time.
At runtime, the JVM enforces correctness mainly via casts at usage sites and any remaining runtime checks from bounds.

### 1. Erasure replaces type parameters with bounds

After erasure:

- `T` becomes `Object` if unbounded
- bounded `T extends Number` becomes the leftmost bound (e.g., `Number`)

### 2. Why you still see ClassCastException

Because casts are inserted where values are read from generic containers.

Example idea:

- a generic container stores values as `Object`
- retrieval performs a cast to the expected erased element type
- if heap pollution occurred earlier, the cast fails here

### 3. Synthetic casts and "cast site" confusion

Stack traces often point to a cast inserted by the compiler, not to the original operation that introduced heap pollution.

Senior debugging workflow:

1. scan for raw types / unchecked operations
2. locate the earliest unchecked cast
3. treat the later cast site as the symptom, not the cause

