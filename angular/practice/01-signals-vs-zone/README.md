# 01 — Signals vs Zone (Change Detection)

**Concept:** [../../01-angular-core-ivy.md](../../01-angular-core-ivy.md)

## Goal

Compare **zone-based** change detection with **signal-based** reactivity in a minimal Angular app. One component uses a traditional class property (zone triggers CD); another uses `signal()` and updates only when the signal changes.

## How to run

1. Create a new Angular app (or use an existing one): `ng new signals-vs-zone --standalone`
2. Add two components as below (or copy the snippets into your app).
3. Run `ng serve` and open the app. Use DevTools or add `console.log` in a lifecycle hook to observe when change detection runs.

## Snippets

### Zone-based counter (traditional)

```typescript
// zone-counter.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-zone-counter',
  standalone: true,
  template: `
    <p>Zone counter: {{ count }}</p>
    <button (click)="increment()">+1</button>
  `,
})
export class ZoneCounterComponent {
  count = 0;
  increment() {
    this.count++;
  }
}
```

Clicking the button triggers zone-based CD; Angular checks this component (and its ancestors) because something async (the click) happened.

### Signal-based counter

```typescript
// signal-counter.component.ts
import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-signal-counter',
  standalone: true,
  template: `
    <p>Signal counter: {{ count() }}</p>
    <button (click)="increment()">+1</button>
  `,
})
export class SignalCounterComponent {
  count = signal(0);
  increment() {
    this.count.update((n) => n + 1);
  }
}
```

Only the part of the template that reads `count()` is updated when the signal changes; fine-grained reactivity. In a zoneless app, no Zone.js is involved.

## Interview takeaway

- **Zone:** "Something might have changed" → run CD tree.
- **Signals:** "This value changed" → update only dependents.
