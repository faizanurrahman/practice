## Senior: invokevirtual, invokeinterface, vtables, itables

**Invocation forms**

- **`invokestatic`** — no receiver; direct.
- **`invokespecial`** — constructors, `private`, `super` — static target resolution (not virtual).
- **`invokevirtual`** — single receiver; **vtable** dispatch on concrete class (HotSpot: Klass → vtable entries).
- **`invokeinterface`** — receiver implements interface; **itable** / search path; historically harder for the JIT to optimize than monomorphic `invokevirtual`.

**Polymorphism shape**

- **Monomorphic** call sites inline best.
- **Megamorphic** sites spend time in dispatch stubs; hard to inline.

**Cross-phase:** object headers, `Klass` pointer, and monitor bits tie to Phase 01 (`01-object-layout-header-alignment-dispatch`). Inline caching and megamorphism: same narrative as “dispatch shape” in layout + JIT phases.
