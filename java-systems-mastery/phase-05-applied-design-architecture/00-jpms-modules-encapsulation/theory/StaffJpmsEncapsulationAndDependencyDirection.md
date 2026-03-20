## Staff: JPMS encapsulation and dependency direction

JPMS supports explicit module boundaries:
- `exports` controls public packages,
- `requires` controls dependencies.

Use dependency direction to keep domain stable and infrastructure replaceable.
