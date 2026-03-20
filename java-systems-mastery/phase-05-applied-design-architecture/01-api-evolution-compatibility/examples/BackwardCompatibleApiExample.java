public class BackwardCompatibleApiExample {
    static class UserDtoV1 {
        final String id;
        UserDtoV1(String id) { this.id = id; }
    }

    static class UserDtoV2 extends UserDtoV1 {
        final String displayName; // additive change
        UserDtoV2(String id, String displayName) {
            super(id);
            this.displayName = displayName;
        }
    }
}
