## Senior: dynamic proxies and bytecode

- **`java.lang.reflect.Proxy`** builds a class at runtime implementing given interfaces; calls go through **`InvocationHandler.invoke`**.
- Each method call on the proxy is an **interface dispatch** + indirection into your handler — extra layers vs direct calls.
- **Method handles** (`java.lang.invoke`) and **hidden classes** (JDK 15+) are modern alternatives for generated code; frameworks may prefer them over classic proxies.

**Interview:** compare proxy overhead vs generated stub classes vs method handles.
