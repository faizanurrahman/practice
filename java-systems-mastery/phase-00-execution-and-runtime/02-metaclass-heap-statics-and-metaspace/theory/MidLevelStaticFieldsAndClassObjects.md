## Mid-level: static fields and `Class` objects

- **`static` fields** belong to the type, not to an instance. You access them as `TypeName.field`.
- At runtime, the JVM associates static storage with the **`java.lang.Class`** instance for that type (HotSpot mental model: mirror on heap).
- **`SomeClass.class`** or **`obj.getClass()`** gives you the runtime `Class<?>` object.

**Practical rule:** static state is **shared by all callers** — global mutable statics are a common source of test flakiness and hidden coupling.
