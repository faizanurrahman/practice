/**
 * Closure + loop with var vs let.
 * Concept: see 02-closures-and-scope.md (sections 9 and 14).
 *
 * Goal:
 *  - Understand that closures capture a shared binding when using var in a loop.
 *  - See how let creates a new binding per iteration.
 *  - Practice your Phase 0 rule: run → break → explain (log in workspace/debug-log.md).
 *
 * Run:
 *   node practice/02-closure-loop-var.js
 */

console.log("=== var in for-loop (shared binding) ===");
for (var i = 0; i < 3; i++) {
  setTimeout(() => console.log("var i =", i), 0);
}
// Expect: 3, 3, 3

setTimeout(() => {
  console.log("\n=== let in for-loop (per-iteration binding) ===");
  for (let j = 0; j < 3; j++) {
    setTimeout(() => console.log("let j =", j), 0);
  }
  // Expect: 0, 1, 2
}, 20);

