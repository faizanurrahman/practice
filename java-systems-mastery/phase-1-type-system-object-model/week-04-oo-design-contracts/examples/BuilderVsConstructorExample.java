import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Example: a builder-based immutable value object.
 *
 * Run:
 *   javac BuilderVsConstructorExample.java
 *   java -ea BuilderVsConstructorExample
 */
public class BuilderVsConstructorExample {
    public static final class CustomerKey {
        private final String region;
        private final List<Integer> ids; // immutable copy

        private CustomerKey(String region, List<Integer> ids) {
            this.region = Objects.requireNonNull(region, "region must not be null");
            this.ids = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(ids, "ids must not be null")));
        }

        public static Builder builder() {
            return new Builder();
        }

        public String region() {
            return region;
        }

        public List<Integer> ids() {
            return ids;
        }

        @Override
        public String toString() {
            return "CustomerKey{region='" + region + "', ids=" + ids + "}";
        }

        public static final class Builder {
            private String region;
            private List<Integer> ids = new ArrayList<>();

            public Builder region(String region) {
                this.region = region;
                return this;
            }

            public Builder addId(int id) {
                this.ids.add(id);
                return this;
            }

            public CustomerKey build() {
                return new CustomerKey(region, ids);
            }
        }
    }

    public static void main(String[] args) {
        CustomerKey k = CustomerKey.builder()
                .region("eu-west")
                .addId(1)
                .addId(2)
                .build();

        assert k.ids().size() == 2 : "Expected two ids";

        System.out.println(k);
        System.out.println("OK: BuilderVsConstructorExample passed");
    }
}

