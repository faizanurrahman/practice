## Mid-level: equals, hashCode, toString contracts

- **`equals`** must be reflexive, symmetric, transitive, consistent; **`hashCode`** must agree with equals.
- **`toString`** is for debugging/logging — keep stable enough for support, not for parsing.
- Use **`Objects.equals` / `Objects.hash`** to reduce boilerplate; records generate these for components.

**Hash collections:** if `equals`/`hashCode` change after insert, maps/sets break — **immutable keys** for hash-based structures.
