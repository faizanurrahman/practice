## Mid-level: compilation and running Java

**Daily confidence**

- You edit `*.java` sources. **`javac`** compiles them into **`*.class`** bytecode (one file per top-level class, plus inner classes as separate classes).
- You run with **`java MainClass`**. The JVM finds the main class, loads it, and starts execution.
- **`java -cp ...`** sets the classpath: where the JVM looks for classes and resources.
- **Module path** (`--module-path`, `-m`) applies when using JPMS; for simple apps, classpath is enough.

**Commands to own**

```bash
javac -d out src/com/example/App.java
java -cp out com.example.App
```

**Mental model:** compile time produces portable bytecode; runtime loads and executes on this machine’s JVM.
