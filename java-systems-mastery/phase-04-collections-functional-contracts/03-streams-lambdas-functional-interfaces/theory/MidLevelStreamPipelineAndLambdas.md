## Mid-level: stream pipeline and lambdas

- **Pipeline:** `source` → 0+ **intermediate** ops (`map`, `filter`, `sorted`) → **terminal** op (`collect`, `reduce`, `forEach`).
- **Lazy:** intermediate ops build a pipeline; nothing runs until a terminal op.
- **Lambdas** must be **effectively final** captures; prefer **stateless** lambdas in streams unless you explicitly control mutability.

**Debug skill:** split pipeline, assign to variables, use `peek` sparingly (side-effect smell).
