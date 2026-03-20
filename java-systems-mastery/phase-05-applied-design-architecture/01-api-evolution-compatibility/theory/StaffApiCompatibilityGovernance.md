## Staff: API compatibility as governance

- **Ownership:** who approves breaking changes, deprecation windows, and consumer communication?
- **Measurement:** usage telemetry (internal packages, reflection) before removal.
- **Multi-team:** binary compatibility vs source compatibility for Java libraries — publish **revapi** / japicmp style checks in CI.

**Staff story:** “We kept `v1` REST + `v2` side-by-side for two quarters, sunset `v1` after 95% migration.”
