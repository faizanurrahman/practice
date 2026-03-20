## Senior: metaspace lifecycle and OOM

- **Metaspace** holds class metadata (per class loader). It is **not** the Java heap; it is native memory governed by JVM options (`MaxMetaspaceSize`, etc.).
- Each **class loader** defines a namespace; metadata is freed when the loader becomes **unreachable** — loader leaks ⇒ metaspace growth.
- **Metaspace OOM** often appears in frameworks that generate many dynamic classes (proxies, scripting, bytecode weavers).

**Contrast with heap:** instances and `Class` mirrors live on heap; **descriptions** of methods/bytecode metadata live in metaspace structures (implementation detail varies by VM; interview answer stays at “metadata vs objects”).

**Diagnostic:** native memory tracking + class-loader histogram + “who holds the loader?”
