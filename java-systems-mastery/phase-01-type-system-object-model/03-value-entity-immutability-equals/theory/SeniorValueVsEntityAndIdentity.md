## Senior: value vs entity and identity

- **Value object:** defined by **fields** (money amount, date range); interchangeable if values match; usually immutable.
- **Entity:** defined by **identity** (user id, order id); same data can mean different things if identity differs.
- **`==` vs `equals`:** `==` on references is identity; `equals` is logical equality (unless broken).

**Inheritance pitfall:** `instanceof`-based equality across hierarchy breaks symmetry/transitivity — prefer `final` value types or careful Liskov design.

**IdentityHashMap:** reference identity; distinct from logical equality.
