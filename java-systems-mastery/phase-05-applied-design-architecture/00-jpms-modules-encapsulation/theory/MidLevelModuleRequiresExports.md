## Mid-level: `module-info.java` basics

- **`module com.example.app { ... }`** — declares a named module.
- **`requires`** — compile/runtime dependency on another module.
- **`exports com.example.api`** — makes package API visible to other modules at compile and run time.
- **`opens`** — reflection access (often for frameworks / serialization).

**Daily use:** keep **public surface** small; export only packages meant to be consumed.
