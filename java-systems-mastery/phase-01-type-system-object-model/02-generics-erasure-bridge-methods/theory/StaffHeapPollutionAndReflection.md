## Staff: heap pollution and generic reflection

- **Heap pollution:** a `List<String>` variable points at a list that holds non-`String` (via raw types / unchecked casts). Failure appears **later** at a cast.
- **Reflection:** `Signature` attributes preserve generic types for fields/methods; frameworks use `ParameterizedType` etc.
- **Performance:** reflective generic introspection is expensive — cache metadata in framework code.

**Staff API rule:** never expose raw types from public APIs; isolate `@SuppressWarnings("unchecked")` in proven-safe helpers.
