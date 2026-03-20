import java.util.concurrent.atomic.AtomicInteger;

public class CounterWithAtomicAndLock {
    private final AtomicInteger atomic = new AtomicInteger();
    private int locked;

    public int atomicInc() {
        return atomic.incrementAndGet();
    }

    public synchronized int lockInc() {
        locked++;
        return locked;
    }
}
