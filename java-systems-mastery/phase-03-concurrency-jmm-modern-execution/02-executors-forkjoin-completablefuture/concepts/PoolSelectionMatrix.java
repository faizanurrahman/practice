public final class PoolSelectionMatrix {
    private PoolSelectionMatrix() {}

    public static String choose(boolean ioHeavy, boolean shortTasks) {
        if (ioHeavy) return "Virtual threads or larger bounded pool";
        if (shortTasks) return "ForkJoinPool";
        return "Fixed ThreadPoolExecutor";
    }
}
