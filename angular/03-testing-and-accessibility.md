# 03 — Testing and Accessibility

## Learning objectives

- Use **Component Harnesses** (or Spectator) for resilient tests that don’t depend on internal DOM structure.
- Apply **accessibility (A11y)** basics: ARIA attributes, focus management (e.g. FocusTrap), and screen-reader-friendly dynamic content (e.g. modals).

---

## Testing: Harnesses vs raw DOM

### Why Harnesses?

Tests that query the DOM by class names, tags, or structure break when you refactor the template (e.g. change a div to a button, or restructure for styling). **Component Harnesses** expose a stable API (e.g. `button.click()`, `getLabel()`) that hides the internal DOM. The component author (or Angular Material) provides the harness; tests use the harness. When the template changes, only the harness implementation is updated, not every test.

### Spectator

**Spectator** is a testing utility that wraps Angular’s `TestBed` and provides helpers for querying, clicking, and asserting. It can be used with or without Harnesses. For custom components, you can write a **Harness** that implements a small interface (e.g. `getOptionLabels()`, `selectOption(0)`) so tests stay stable.

**Senior scenario:** "How do you test a custom dropdown without depending on its DOM structure?" — Create a Component Harness for the dropdown that exposes `selectOption(index)` and `getSelectedLabel()`; tests use only that API.

---

## Accessibility (A11y)

### ARIA

- Use **ARIA** attributes when the native HTML semantics are not enough: `role`, `aria-label`, `aria-expanded`, `aria-controls`, `aria-modal="true"` for modals, etc.
- Prefer native elements (e.g. `<button>`, `<input>`) over custom divs with ARIA when they fit; add ARIA when you build custom widgets.

### Focus management

- **Focus trap:** When opening a modal or dialog, move focus **into** the modal (e.g. first focusable element) and keep Tab from leaving the modal until it is closed. Angular CDK’s `FocusTrap` (or `A11yModule`) does this.
- When closing the modal, return focus to the element that opened it so keyboard users don’t get lost.

### Dynamic content and screen readers

- Ensure dynamic content (e.g. modals, toasts) is **announced** by screen readers. Use `aria-live` regions or move focus into the new content when appropriate.
- For modals: `role="dialog"`, `aria-modal="true"`, and a visible focus indicator inside the modal.

**Senior scenario:** "How do you make a modal accessible?" — Focus trap, `role="dialog"`, `aria-modal="true"`, return focus on close, and ensure the first focusable element is visible.

---

## Interview focus

- "What is a Component Harness?" — A test API that abstracts the component’s DOM so tests are stable and resilient to template changes.
- "What should we do for focus when opening a modal?" — Move focus into the modal (focus trap), keep focus inside until close, return focus to the trigger on close.

---

## Practice

- `practice/03-component-harness/` — One Angular component plus a spec that uses a Component Harness (or Spectator) to click and assert without relying on internal DOM.
- `practice/03-a11y-focus-trap/` — Minimal modal/dialog component using FocusTrap and ARIA (`role="dialog"`, `aria-modal="true"`); demonstrate focus moving into the modal on open.
