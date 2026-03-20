## Mid-level: stack vs heap (what you allocate where)

- **Local variables** and **operand stack** live in **stack frames** (per thread); they are not GC-managed as objects.
- **`new`** allocates on the **heap** (except when the JIT eliminates allocation — senior topic).
- **Static fields** are tied to the `Class` object on the heap; **metadata** about the class lives largely in **metaspace** (native).

**Practical:** large object graphs → heap pressure; deep stacks → `StackOverflowError`, not heap OOM.
