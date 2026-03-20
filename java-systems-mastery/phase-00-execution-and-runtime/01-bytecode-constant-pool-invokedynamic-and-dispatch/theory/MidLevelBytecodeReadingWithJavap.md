## Mid-level: reading bytecode with `javap`

**What you need daily**

- `javap -c -v ClassName` shows **constant pool** + **bytecode** for methods.
- You can answer: “Is this call virtual, special, static, or `invokedynamic`?”

**Constant pool = symbol table**

- Class, method, field, string references are **indices** into the pool.
- Bytecode instructions reference those indices.

**Code attribute**

- Holds actual instructions, exception table, `StackMapTable` (verification).

**Practice:** run `javap` on a class with a lambda and find `invokedynamic` in `Code`.
