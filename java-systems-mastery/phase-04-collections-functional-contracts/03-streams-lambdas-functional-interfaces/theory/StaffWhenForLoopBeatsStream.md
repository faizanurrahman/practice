## Staff: when a for-loop beats a stream

- **Hot, tight numeric loops** — plain `for` often clearer and avoids stream machinery + boxing.
- **Checked exceptions** inside lambdas add noise (`Lombok`, wrappers, or loop).
- **Debugging** and **line-level profiling** can be easier on loops.

**Rule:** streams for **composition** and **readability** at boundaries; loops for **innermost** hot paths unless measurement says otherwise.
