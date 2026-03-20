## Idempotency and retry boundaries

Retry only when:
- operation is idempotent, or
- deduplication key is enforced.

Classify errors:
- transient: timeout, overload, temporary network failure,
- permanent: validation error, invariant violation.
