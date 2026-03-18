/**
 * Memory management and GC: reachability, WeakMap, leak patterns (safe demos).
 * Concept: ../12-memory-management-gc.md
 * Run: node practice/12-memory-leak-patterns.js
 */

// --- 1. Reachability: object is collected when unreachable ---
console.log("--- 1. Reachability ---");
function createObj() {
  const obj = { name: "temp" };
  return function getRef() {
    return obj; // closure keeps obj reachable while getRef is alive
  };
}
const getRef = createObj();
console.log("getRef() keeps inner obj reachable:", getRef().name);
// When getRef is no longer used, both getRef and the inner obj can be collected.

// --- 2. WeakMap: cache that does not prevent GC of keys ---
console.log("\n--- 2. WeakMap cache ---");
const cache = new WeakMap();
const key1 = { id: 1 };
const key2 = { id: 2 };
cache.set(key1, "result for key1");
cache.set(key2, "result for key2");
console.log("cache.has(key1):", cache.has(key1));
console.log("cache.get(key1):", cache.get(key1));
// When key1/key2 go out of scope and are not referenced elsewhere, they can be GC'd and entries disappear.

// --- 3. Leak pattern: global (avoid in real code) ---
// In non-strict mode: accidentalGlobal = 1; creates a global. Strict mode throws.
"use strict";
// accidentalGlobal = 1; // would throw in strict mode

// --- 4. Leak pattern: timer not cleared (commented – uncomment to see retention) ---
// const interval = setInterval(() => {}, 1000);
// If never clearInterval(interval), the callback stays scheduled and any closed-over refs stay alive.
// clearInterval(interval); // always clear when done

// --- 5. Closure retaining (safe demo: small object) ---
console.log("\n--- 5. Closure retention (small) ---");
function createClosure() {
  const data = { value: 42 };
  return function getValue() {
    return data.value;
  };
}
const getValue = createClosure();
console.log("getValue():", getValue());
// data is retained as long as getValue is referenced.

console.log("\nDone. See 12-memory-management-gc.md for leak patterns and DevTools usage.");
