## Mid-level: records and compact constructors

- **`record Point(int x, int y) { }`** declares an immutable data carrier with `equals`/`hashCode`/`toString` derived from state.
- **Canonical constructor** matches components; **compact constructor** lets you validate/normalize before field assignment:

```java
record Email(String value) {
    Email {
        if (value == null || value.isBlank()) throw new IllegalArgumentException();
        value = value.trim().toLowerCase();
    }
}
```

- Records are classes: they can implement interfaces and have static members; restrictions apply to inheritance (no extension of another class).
