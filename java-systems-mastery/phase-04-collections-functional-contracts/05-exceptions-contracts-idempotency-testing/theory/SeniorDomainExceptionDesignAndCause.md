## Exception taxonomy and cause preservation

Guideline:
- domain exceptions describe business failures,
- infrastructure exceptions describe technical failures,
- always preserve original cause when wrapping (`new X(msg, cause)`).
