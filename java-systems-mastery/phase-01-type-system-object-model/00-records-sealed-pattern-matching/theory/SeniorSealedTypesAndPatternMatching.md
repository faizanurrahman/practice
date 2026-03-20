## Senior: sealed types and pattern matching

- **`sealed`** restricts which classes may extend/implement the type (`permits` list). Enables exhaustiveness checking.
- **`instanceof` patterns** and **`switch` patterns** (Java 21+) let you destructure and branch by type/shape in one expression.
- Compiler can prove **exhaustive `switch`** over sealed hierarchy — missing case is a compile error.

**Interview:** contrast sealed + records + pattern `switch` vs visitor pattern for closed hierarchies.
