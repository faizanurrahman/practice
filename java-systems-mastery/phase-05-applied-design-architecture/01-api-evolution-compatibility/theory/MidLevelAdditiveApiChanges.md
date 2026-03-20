## Mid-level: additive API changes

- Prefer **new methods / overloads** and **new optional fields** (DTO versioning, feature flags) over breaking signatures.
- **Deprecation** with `@Deprecated(forRemoval=…)` and documented timeline.
- **Semantic compatibility** matters: stricter validation can break clients even if signatures unchanged.

**Rule:** default to **expand** contracts; shrink only with major version + migration.
