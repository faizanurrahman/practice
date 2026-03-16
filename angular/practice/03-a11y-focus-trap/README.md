# 03 — A11y: Focus Trap and Modal

**Concept:** [../../03-testing-and-accessibility.md](../../03-testing-and-accessibility.md)

## Goal

Implement a minimal **modal/dialog** that is accessible: focus moves into the modal when it opens, Tab keeps focus inside (focus trap), and focus returns to the trigger when the modal closes. Use ARIA attributes so screen readers announce it as a dialog.

## Angular CDK / Material

- Import `A11yModule` from `@angular/cdk/a11y` (or use `DialogModule` from Angular Material which includes focus trap and ARIA).
- **FocusTrap:** Wrap the modal content in `<div cdkTrapFocus>...</div>` so focus stays inside until the user closes the modal (Escape or button).
- On open: after the modal is in the DOM, focus the first focusable element (or the container with `tabindex="-1"` and then move focus to first control).
- On close: call `focus()` on the element that opened the modal (store a reference, e.g. `document.activeElement` when opening).

## ARIA

- Container: `role="dialog"`, `aria-modal="true"`, `aria-labelledby` pointing to the title element.
- Optionally `aria-describedby` for the description.

## Minimal template (concept)

```html
<div
  role="dialog"
  aria-modal="true"
  aria-labelledby="dialog-title"
  cdkTrapFocus
  (keydown.escape)="close()"
>
  <h2 id="dialog-title">Title</h2>
  <p>Content</p>
  <button (click)="close()">Close</button>
</div>
```

In the component, on init (after view is ready), focus the first focusable element or the title. When closing, restore focus to the previously focused element.

## Interview takeaway

For a modal: **focus trap** (Tab stays inside), **focus into modal on open**, **focus back to trigger on close**, and **ARIA** (`role="dialog"`, `aria-modal="true"`).
