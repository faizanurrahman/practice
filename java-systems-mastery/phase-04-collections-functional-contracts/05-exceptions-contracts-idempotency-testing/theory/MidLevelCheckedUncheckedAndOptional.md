## Mid-level: checked vs unchecked exceptions and `Optional`

- **Checked** (`IOException`, etc.) — compiler forces handling; good for **recoverable**, **expected** failures at boundaries.
- **Unchecked** (`IllegalArgumentException`, `NullPointerException`) — for **programming errors** or failures you do not want to force through every layer.
- **`Optional<T>`** — use primarily as a **return type** for “absence”; avoid fields/parameters by default (serialization, frameworks, readability).

**API design:** don’t use checked exceptions for control flow; document which runtime exceptions callers may see.
