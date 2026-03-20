## JMM Happens-Before and Safe Publication (Visibility Contracts)

Staff-level takeaway: correctness depends on establishing happens-before edges.

### 1. Visibility is not guaranteed by "reading a flag"

If you use `volatile` as a flag, you still must ensure the data you care about is properly published.
The safe publication mechanism must connect:

- writes of the data
- the publication action (volatile write / lock release)
- reads of the data (volatile read / lock acquisition)

### 2. final fields and publication

`final` field semantics provide stronger visibility guarantees for correctly constructed and safely published objects.
This helps immutable designs, but it does not fix:

- leaking `this` during construction
- aliasing mutable objects through final references

