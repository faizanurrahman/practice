## Javap and Constant Pool Mental Model

When you run `javap -v`, you mostly want to answer two questions:

1. "What does the bytecode *reference* symbolically?"
2. "What concrete instructions are executed in this method?"

### The constant pool: the JVM's symbol table

A `.class` file stores symbolic references in the constant pool:

- class and interface references
- method and field references
- string literals

Bytecode instructions like `invokevirtual` use an index into the constant pool to find:

- the resolved method signature to call
- the owning class/interface information

### Attributes: why `Code` matters

For a method, the `Code` attribute contains:

- bytecode instructions
- exception table entries
- `StackMapTable` used by the verifier

Practical debugging flow:

1. `javap -c -v ClassName` to inspect bytecode and constant pool
2. locate `invokedynamic` entries for lambdas
3. inspect `Code` to confirm allocation sites (`new`)
4. verify which invocation form is used (`invokevirtual` vs `invokespecial`)

