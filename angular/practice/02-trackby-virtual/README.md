# 02 — trackBy and Virtual Scroll

**Concept:** [../../02-rxjs-ngrx-performance.md](../../02-rxjs-ngrx-performance.md)

## Goal

Use **trackBy** in `*ngFor` so Angular reuses DOM nodes when the list data changes (e.g. after a refresh). Optionally use **virtual scroll** for very long lists.

## trackBy

Without trackBy, Angular tracks items by object reference. If you replace the array (e.g. new data from API), every item may get a new reference and Angular will destroy and recreate all list DOM nodes. With **trackBy**, you provide a function that returns a stable id (e.g. `item.id`). Angular then reuses the DOM node for the same id and only updates bindings.

### Template

```html
<ul>
  <li *ngFor="let item of items; trackBy: trackById">{{ item.name }}</li>
</ul>
```

### Component

```typescript
trackById(index: number, item: { id: number; name: string }): number {
  return item.id;
}
```

When `items` is replaced with a new array that has the same ids, only changed rows are updated; others keep their DOM nodes.

## Virtual scroll (Angular CDK)

For very long lists (e.g. 10,000 rows), render only the visible items:

```html
<cdk-virtual-scroll-viewport itemSize="50" style="height: 400px">
  <div *cdkVirtualFor="let item of items; trackBy: trackById">
    {{ item.name }}
  </div>
</cdk-virtual-scroll-viewport>
```

Install: `ng add @angular/cdk` and import `ScrollingModule`.

## Interview takeaway

- **trackBy:** Stable identity (e.g. `item.id`) so Angular reuses DOM and only updates bindings.
- **Virtual scroll:** Render only visible items + buffer; use for long lists to avoid thousands of DOM nodes.
