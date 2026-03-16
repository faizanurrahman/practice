/**
 * Phase 0 — tiny TS example. Run: npx tsx example.ts
 * Break it (e.g. pass a number to greet) and log why in ../debug-log.md
 */

const x: number = 42;
console.log("x =", x);

function greet(name: string): string {
  return `Hello, ${name}`;
}
console.log(greet("World"));
