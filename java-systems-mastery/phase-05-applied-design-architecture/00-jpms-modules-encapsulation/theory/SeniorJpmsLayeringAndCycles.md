## Senior: JPMS layering and cycles

- **Acyclic module graph** at build time — cycles force awkward split or `requires transitive` sprawl.
- **`requires transitive`** — dependency becomes part of your consumers’ compile graph; use when re-exporting an API type, not to “fix” cycles.
- **Unnamed / automatic modules** — migration bridge; long-term goal is explicit modules.

**Interview:** difference between **encapsulation for teams** (JPMS) vs **classpath** “everything is public if on the path”.
