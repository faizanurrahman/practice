## Staff: builders, immutability, API and performance cost

- **Builders** improve ergonomics for many optional parameters; **immutability** still needs `final` fields + **defensive copies** of mutable inputs.
- **Cost:** extra builder object + indirection; in ultra-hot allocation paths prefer constructors or records.
- **API evolution:** widening builders is easier than telescoping constructors; document thread-safety of builders (usually not thread-safe).

**Staff review:** DTOs crossing process boundaries — validation at boundary, immutable snapshots inside domain core where possible.
