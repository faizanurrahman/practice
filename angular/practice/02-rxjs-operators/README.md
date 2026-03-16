# 02 — RxJS Operators and Subjects

**Concept:** [../../02-rxjs-deep-dive.md](../../02-rxjs-deep-dive.md) and [../../02-rxjs-ngrx-performance.md](../../02-rxjs-ngrx-performance.md)

## Goal

Run small demos of **operators** (switchMap, mergeMap, concatMap, exhaustMap) and **Subjects** (Subject, BehaviorSubject, ReplaySubject). These run in **Node** (no Angular required). Install: `npm install rxjs` (or `npm init -y && npm install rxjs`).

## How to run

From this folder:

```bash
node operators-demo.js
node subjects-demo.js
```

## Interview question

"User types in a search box; we debounce and then call the API. Which operator and why?"  
**Answer:** **switchMap** — each new keystroke (after debounce) should cancel the previous HTTP request so only the latest search result is shown.

## Snippet: switchMap (cancel previous)

```javascript
// switchMap-demo.js
const { of } = require('rxjs');
const { switchMap, delay } = require('rxjs/operators');

// Simulate: outer emits 1, 2, 3; inner delays 100ms then returns value
of(1, 2, 3).pipe(
  switchMap((x) => of(x).pipe(delay(100)))
).subscribe((v) => console.log('switchMap:', v));
// Output: 3 (only the last; previous inner subscriptions were cancelled)
```

## Snippet: mergeMap (parallel)

```javascript
// mergeMap: all inners run in parallel; order of emission depends on completion
of(1, 2, 3).pipe(
  mergeMap((x) => of(x).pipe(delay(100 * (4 - x))))
).subscribe((v) => console.log('mergeMap:', v));
// Output: 3, 2, 1 (fastest complete first)
```

## Snippet: concatMap (sequential)

```javascript
// concatMap: wait for inner to complete before subscribing to next
of(1, 2, 3).pipe(
  concatMap((x) => of(x).pipe(delay(50)))
).subscribe((v) => console.log('concatMap:', v));
// Output: 1, 2, 3 (order preserved)
```

## Snippet: exhaustMap (ignore new until done)

```javascript
// exhaustMap: while inner is running, ignore new outer values
of(1, 2, 3).pipe(
  exhaustMap((x) => of(x).pipe(delay(200)))
).subscribe((v) => console.log('exhaustMap:', v));
// Output: 1 only (2 and 3 ignored because first inner still running)
```

Create one file per operator or one `operators-demo.js` that requires `rxjs` and `rxjs/operators` and runs all four with `interval` or `of` as above.
