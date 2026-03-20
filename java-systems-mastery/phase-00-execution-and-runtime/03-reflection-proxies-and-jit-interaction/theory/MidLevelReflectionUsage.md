## Mid-level: reflection usage

- **`Class.getDeclaredMethod` / `invoke`** — discover and call methods by name at runtime.
- **`Field.setAccessible(true)`** — bypass `private` (with module/SecurityManager caveats on older JDKs).
- Common uses: serialization (Jackson), DI frameworks, tests.

**Cost intuition:** reflection is flexible but each call path is harder for the JIT to treat like a normal monomorphic call.
