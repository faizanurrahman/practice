public final class LoopVsStreamHeuristic {
    private LoopVsStreamHeuristic() {}

    public static String decide(boolean simpleHotPath, boolean complexTransform) {
        if (simpleHotPath) return "Prefer loop for clarity and minimal overhead";
        if (complexTransform) return "Prefer stream for composition and readability";
        return "Either is fine; optimize with measurement";
    }
}
