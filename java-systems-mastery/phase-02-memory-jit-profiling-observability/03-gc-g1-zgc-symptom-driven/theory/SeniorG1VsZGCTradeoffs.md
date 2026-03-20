## Senior: G1 vs ZGC vs Shenandoah (symptom-driven)

- **G1** (common default): regions, pause targets, good general-purpose balance; tune with care.
- **ZGC / Shenandoah:** low-latency oriented; trade-offs include CPU overhead and JDK/support matrix.
- **Staff interview answer:** pick collector by **pause SLO**, **heap size**, **JDK version**, and **ops maturity** — prove with GC logs + JFR before switching.

Do not memorize dozens of flags; know **what evidence** would justify a change.
