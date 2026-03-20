## Senior: C1/C2, inlining, deoptimization

- **Tiered compilation:** methods move across levels as they become hot; **OSR** can replace a long-running interpreted loop body.
- **Inlining** removes call overhead when targets are stable; **devirtualization** helps monomorphic calls.
- **Deoptimization** invalidates bad assumptions (new class loaded, uncommon trap); execution falls back safely then re-optimizes.

**Interview:** why p99 improves after several minutes of traffic.
