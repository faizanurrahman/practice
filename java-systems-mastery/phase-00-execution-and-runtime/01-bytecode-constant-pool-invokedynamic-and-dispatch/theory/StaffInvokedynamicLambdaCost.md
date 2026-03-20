## Staff: invokedynamic, lambdas, and JIT cost

**Lambda linkage**

- Lambdas compile to **`invokedynamic`** + bootstrap (`LambdaMetafactory` pattern).
- First linkage resolves the call site; later calls reuse the linked target when stable.

**Capturing vs non-capturing**

- Non-capturing lambdas can reuse a single generated implementation instance.
- Capturing lambdas allocate closure state; hot paths may show allocation churn if poorly structured.

**JIT**

- Stable call sites → inline + devirtualization opportunities.
- Unstable or megomorphic lambda/indirect call paths → residual dispatch + allocation cost.

**Staff question:** “Why is my stream/lambda hot path slower after a refactor?” — check capture, call-site polymorphism, and allocation in JFR.
