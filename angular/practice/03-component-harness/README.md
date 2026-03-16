# 03 — Component Harness (Testing)

**Concept:** [../../03-testing-and-accessibility.md](../../03-testing-and-accessibility.md)

## Goal

Test a component using a **Component Harness** so the test does not depend on internal DOM structure (classes, tags). When the template changes, only the harness implementation is updated.

## Steps

1. Create a simple component (e.g. a button that emits a value or a custom dropdown).
2. Create a **Harness** that extends `ComponentHarness` (or `HarnessWithStaticAndDynamicOptions`) and exposes methods like `click()`, `getLabel()`, `selectOption(index)`.
3. In the spec, get the harness via `TestBed.runInInjectionContext` or `HarnessLoader.getHarness(MyHarness)` and call those methods; assert on behavior, not DOM.

## Example (minimal)

### Component

```typescript
@Component({
  selector: 'app-submit-btn',
  standalone: true,
  template: `<button (click)="submit.emit()">Submit</button>`,
})
export class SubmitBtnComponent {
  @Output() submit = new EventEmitter<void>();
}
```

### Harness

```typescript
export class SubmitBtnHarness extends ComponentHarness {
  static hostSelector = 'app-submit-btn';
  async clickSubmit(): Promise<void> {
    await this.locatorFor('button')().then((b) => b.click());
  }
}
```

### Spec

```typescript
it('emits when clicked', async () => {
  const loader = TestbedHarnessEnvironment.loader(fixture);
  const harness = await loader.getHarness(SubmitBtnHarness);
  let emitted = false;
  component.submit.subscribe(() => (emitted = true));
  await harness.clickSubmit();
  expect(emitted).toBe(true);
});
```

The test never queries by CSS or tag; it uses the harness API. If you change the button to an `<a>`, you only update the harness.

## Interview takeaway

A **Component Harness** is a test API that abstracts the component’s DOM so tests are stable and resilient to UI refactors.
