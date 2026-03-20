## Senior: class loading, linking, initialization

**Lifecycle (JVM spec)**

1. **Loading** — class loader reads bytes, creates internal `Class` representation (not the same as `java.lang.Class` mirror object timing, but related).
2. **Linking**
   - **Verify** — bytecode safety checks.
   - **Prepare** — static fields allocated/defaulted (not user-written initializers yet).
   - **Resolve** — symbolic references in the constant pool may be resolved eagerly or lazily (implementation-dependent for some steps).
3. **Initialization** — run `<clinit>` (static initializers) exactly **once** per class, under the VM’s lock.

**Constant pool**

- The class file holds a **constant pool**: symbolic references (classes, methods, fields, strings, literals).
- Resolution turns symbolic refs into concrete runtime structures when needed.

**Delegation**

- Typical loaders: bootstrap → platform → application. **Parent delegation** avoids duplicate incompatible definitions of `java.*` types.

**Interview:** “What runs before `main`?” — load/link/init of `main`’s class and its superclasses/static dependencies.
