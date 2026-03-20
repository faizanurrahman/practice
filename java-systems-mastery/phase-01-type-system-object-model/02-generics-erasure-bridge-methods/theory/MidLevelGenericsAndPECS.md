## Mid-level: generics and PECS

- **Generics** let APIs express type-safe collections: `List<String>` not `List`.
- **Wildcards:** `? extends T` (producer — read as `T`), `? super T` (consumer — write `T`).
- **PECS:** *Producer Extends, Consumer Super* — choose wildcard by whether the method reads or writes the collection.

Example: `void copy(List<? extends T> src, List<? super T> dst)`.
