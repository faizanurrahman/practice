/**
 * RxJS operators: switchMap, mergeMap, concatMap, exhaustMap.
 * Run: node operators-demo.js  (requires: npm install rxjs)
 * Concept: ../02-rxjs-ngrx-performance.md
 */

const { of } = require('rxjs');
const { switchMap, mergeMap, concatMap, exhaustMap, delay } = require('rxjs/operators');

console.log("=== switchMap (cancel previous) ===");
of(1, 2, 3).pipe(
  switchMap((x) => of(x).pipe(delay(100)))
).subscribe((v) => console.log("  switchMap:", v));
// After 100ms: 3 only

console.log("\n=== mergeMap (parallel) ===");
of(1, 2, 3).pipe(
  mergeMap((x) => of(x).pipe(delay(100 * (4 - x))))
).subscribe((v) => console.log("  mergeMap:", v));
// 3, 2, 1 (fastest first)

console.log("\n=== concatMap (sequential) ===");
of(1, 2, 3).pipe(
  concatMap((x) => of(x).pipe(delay(50)))
).subscribe((v) => console.log("  concatMap:", v));
// 1, 2, 3

console.log("\n=== exhaustMap (ignore new until done) ===");
of(1, 2, 3).pipe(
  exhaustMap((x) => of(x).pipe(delay(200)))
).subscribe((v) => console.log("  exhaustMap:", v));
// 1 only (2, 3 ignored)
