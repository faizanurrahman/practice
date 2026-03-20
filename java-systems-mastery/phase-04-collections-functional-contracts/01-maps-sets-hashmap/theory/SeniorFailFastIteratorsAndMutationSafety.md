## Fail-fast Iterators: What They Detect and What They Do Not

Staff-level takeaway: fail-fast is not thread-safety.

### 1. How fail-fast typically works

Many JDK collections store a structural modification counter (conceptually `modCount`).
Iterators record the expected value at creation time.

If the collection structure changes (adds/removes) and the counter differs during iteration,
the iterator throws `ConcurrentModificationException`.

### 2. What it cannot guarantee

Fail-fast is best-effort:

- races may not be detected in all interleavings
- even detected exceptions do not provide safe recovery

### 3. Design alternatives

For concurrent iteration:

- use concurrent collections (weakly consistent iterators)
- use snapshot patterns (Copy-on-Write or explicit copying)
- synchronize externally when you need strict iteration semantics

