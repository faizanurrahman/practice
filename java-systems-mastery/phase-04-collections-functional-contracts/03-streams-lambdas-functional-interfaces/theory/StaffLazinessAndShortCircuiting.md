## Staff: laziness, short-circuiting, ordering

- **`findFirst` / `anyMatch`** can short-circuit; **ordering** and **parallelism** interact (parallel may not preserve encounter order unless op enforces it).
- **`sorted` / `distinct`** may buffer — memory pressure on large streams.
- **Staff:** document **stability** requirements for APIs returning streams; infinite streams need careful short-circuit.
