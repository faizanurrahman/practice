## Senior: escape analysis and scalar replacement

- **Escape analysis** asks whether an object’s reference **escapes** the method (returned, stored in heap, captured by lambda, etc.).
- If it does **not** escape, the JIT may **scalar-replace** fields into registers/stack and **skip** the heap allocation.
- This explains “I allocate in source but don’t see allocations in JFR for that hot method.”

**TLABs:** per-thread allocation buffers make **short-lived** heap allocation cheap until refill / promotion pressure bites.
