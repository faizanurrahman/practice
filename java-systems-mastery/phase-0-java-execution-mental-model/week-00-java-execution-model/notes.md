## Phase 0 - Week 00: Java Execution Mental Model (Interview-First Runtime Backbone)

This is the one piece that connects everything else.

Learn the complete pipeline from source to execution:

1. `*.java` source code
2. `javac` compilation to `.class` bytecode
3. class loading (parent delegation + loaders)
4. linking (verify/prepare/resolve)
5. initialization (`<clinit>`)
6. interpretation vs JIT compilation
7. objects on the heap + metadata in metaspace
8. threads, stacks, and scheduling
9. memory visibility (JMM) + synchronization
10. GC and allocation behavior

Staff/lead mindset:

- interviews rarely test syntax; they test whether you can explain what the JVM does and why your design choices matter
- every later topic should be answerable in terms of this runtime pipeline

