## Mid-level: thread pool sizing (rules of thumb)

- **CPU-bound work:** pool size ≈ **CPU cores** (sometimes +1 for pipelining — measure).
- **Blocking I/O–heavy work:** larger pools or **virtual threads** (see [03-virtual-threads](../03-virtual-threads-scopedvalue-structured-concurrency/notes.md)).
- Name thread factories and use **bounded** queues for overload control in services you operate.
