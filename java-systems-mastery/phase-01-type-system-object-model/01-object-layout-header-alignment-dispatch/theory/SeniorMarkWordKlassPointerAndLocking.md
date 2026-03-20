## Senior: mark word, Klass pointer, locking, dispatch

- **Mark word:** identity hash (when computed), GC bits, **lock state** (unlocked, biased, thin, fat).
- **Klass pointer:** connects instance to class metadata (compressed OOPs on 64-bit).
- **Dispatch:** `invokevirtual` uses vtable; **`invokeinterface`** uses itable; **monomorphic** sites optimize best; **megamorphic** sites hurt inlining.

**Hot path design:** avoid interface dispatch inside tight loops when a single concrete type would do; `final` classes help the JIT.

**Cross-phase:** bytecode view in [Phase 00 / 01-bytecode](../../phase-00-execution-and-runtime/01-bytecode-constant-pool-invokedynamic-and-dispatch/notes.md).
