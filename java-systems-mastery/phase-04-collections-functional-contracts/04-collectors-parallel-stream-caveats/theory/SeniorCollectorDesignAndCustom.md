## Collector pitfalls

Collectors require associativity and clear mutability rules.
Common mistakes:
- mutating shared containers unsafely,
- assuming encounter order where it is not guaranteed,
- using heavy downstream collectors in hot paths.
