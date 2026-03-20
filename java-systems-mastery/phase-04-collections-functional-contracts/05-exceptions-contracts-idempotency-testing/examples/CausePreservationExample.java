public class CausePreservationExample {
    static class DataAccessException extends RuntimeException {
        DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void main(String[] args) {
        try {
            throw new IllegalStateException("db timeout");
        } catch (RuntimeException ex) {
            throw new DataAccessException("Failed to load user", ex);
        }
    }
}
