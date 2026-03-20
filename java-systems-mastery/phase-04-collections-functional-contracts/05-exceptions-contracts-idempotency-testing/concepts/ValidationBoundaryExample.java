public final class ValidationBoundaryExample {
    private ValidationBoundaryExample() {}

    public static void requireNonEmpty(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must be non-empty");
        }
    }
}
