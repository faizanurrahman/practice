/**
 * Hoisting and Temporal Dead Zone (TDZ).
 * See: 01-execution-context-and-memory.md
 *
 * Predict the output (or error) before running: node practice/01-hoisting-and-tdz.js
 */

console.log("=== 1. Function declaration is hoisted (full) ===");
greet(); // Allowed: function declaration is created in Creation phase
function greet() {
  console.log("Hello");
}

console.log("\n=== 2. var: hoisted and initialized to undefined ===");
console.log("a (before declaration):", a); // undefined, not ReferenceError
var a = 10;
console.log("a (after assignment):", a);

console.log("\n=== 3. let: hoisted but in TDZ until declaration line ===");
// Uncomment the next line to see TDZ: ReferenceError
// console.log("b (before declaration):", b);
let b = 20;
console.log("b (after declaration):", b);

console.log("\n=== 4. const: same as let (TDZ) ===");
// Uncomment to see TDZ: ReferenceError
// console.log("c (before declaration):", c);
const c = 30;
console.log("c (after declaration):", c);

console.log(
  "\n=== 5. let without initializer: undefined after declaration ===",
);
let d;
console.log("d (after 'let d;'):", d);
d = 40;
console.log("d (after assignment):", d);
