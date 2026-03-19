## Wildcards, PECS, Heap Pollution, and Reflection (The Four Failure Modes)

Staff-level takeaway: each generics feature exists to prevent (or at least contain) a specific class of runtime failures.

### 1. Wildcards are a variance contract

- `? extends T` (producer): you can read as `T` (or subtype), but you cannot safely add non-null values
- `? super T` (consumer): you can add `T` (or subtype), but reads are limited to `Object`

### 2. PECS is the API design rule

Use wildcards based on direction:

- "produces values" => `extends`
- "consumes values" => `super`

### 3. Heap pollution is the root cause

Runtime failures usually come from heap pollution (raw types / unchecked casts).
Heap pollution does not corrupt JVM memory; it triggers a later cast failure.

### 4. Reflection works through class-file signatures

Even though type parameters are erased in runtime objects, class files can preserve generic signatures.
Frameworks recover types by reading signatures via reflection.

Performance note:

- generic reflection is expensive; cache it in framework code.

