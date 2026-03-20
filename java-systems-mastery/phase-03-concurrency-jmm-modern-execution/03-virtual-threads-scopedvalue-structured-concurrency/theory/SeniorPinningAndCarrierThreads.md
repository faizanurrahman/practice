## Senior: pinning, carrier threads, scheduler

- Virtual threads **mount** on a small pool of **carrier** (platform) threads.
- **Pinning** occurs when a virtual thread blocks in a **synchronized** block/method or native code in ways that prevent unmounting — can reduce scalability (carrier starvation).
- Prefer **`ReentrantLock`** over `synchronized` in hot paths inside virtual-thread code when profiling shows pinning (JDK guidance evolves — measure).

**Monitor:** carrier thread pool utilization, queueing, and pinned event diagnostics (JDK-dependent).
