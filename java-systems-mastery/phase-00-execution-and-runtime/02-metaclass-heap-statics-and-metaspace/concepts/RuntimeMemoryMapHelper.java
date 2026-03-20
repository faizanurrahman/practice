public final class RuntimeMemoryMapHelper {
    private RuntimeMemoryMapHelper() {}

    public static String describe() {
        return """
            Heap: object instances and arrays
            Metaspace: class metadata, method metadata, runtime constant pools
            Stack (per thread): frames, locals, operand stack
            PC register: current instruction address per thread
            """;
    }

    public static void main(String[] args) {
        System.out.println(describe());
    }
}
