## equals and hashCode with Inheritance (Why Contracts Break)

Staff-level takeaway: equality semantics are not "automatically correct" once inheritance exists.

### 1. The equals contract is global

equals must be:

- reflexive
- symmetric
- transitive
- consistent
- non-null for non-null receivers

and hashCode must be consistent with equals.

### 2. Inheritance makes symmetry and transitivity fragile

If a base class uses `instanceof` and a subclass adds fields,
you can easily create equality that is not:

- symmetric (A equals B but B does not equal A)
- transitive (A equals B and B equals C but A does not equal C)

Value objects are often safest when:

- they are `final`
- equality is based on the exact concrete type (e.g., `getClass()` checks)
- they are immutable (fields cannot change after construction)

### 3. Performance pitfalls

When used as keys in HashMap/HashSet:

- `hashCode()` runs on insertion and lookup (hot paths)
- collisions call `equals()` more often

So:

- keep hash computations cheap and stable
- avoid deep comparisons in equals for hot-key objects

