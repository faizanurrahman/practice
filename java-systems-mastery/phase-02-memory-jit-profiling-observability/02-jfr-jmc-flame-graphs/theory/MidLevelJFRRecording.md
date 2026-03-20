## Mid-level: Java Flight Recorder (JFR) recording

- **JFR** ships with the JDK; records low-overhead events (CPU, allocation, GC, locks).
- Typical workflow: start a **recording** (continuous or timed), reproduce the issue, stop and **export** the `.jfr` file.
- Open in **JDK Mission Control (JMC)** for analysis.

**You should know:** how to enable a recording without restarting the JVM (JMC / `jcmd`).
