/**
 * Memory model: reference vs value, closure and retention.
 * Concept: ../05-memory-model.md
 * Run: node practice/05-memory-reference-leak.js
 */

// --- 1. Primitives: copy by value ---
let a = 42;
let b = a;
b = 100;
console.log("a =", a, "b =", b); // a = 42, b = 100

// --- 2. Objects: copy by reference ---
let o1 = { x: 1 };
let o2 = o1;
o2.x = 99;
console.log("o1.x =", o1.x); // 99 (same object)

// --- 3. Closure retains outer environment ---
function makeCounter() {
  let count = 0;
  const bigData = new Array(1000).fill(0); // simulated "large" data
  return {
    increment() {
      count++;
      return count;
    },
    getCount() {
      return count;
    },
    // Closure over bigData keeps it in memory while this object is reachable
    getBigDataRef() {
      return bigData.length;
    },
  };
}
const counter = makeCounter();
console.log("counter.increment() =", counter.increment()); // 1
console.log("counter.getCount() =", counter.getCount());  // 1
// As long as `counter` is reachable, the closure (and thus `bigData`) is not GC'd.
// In a real leak: if you store such a closure on a DOM node and never remove it, memory stays.

// --- 4. WeakRef (optional; Node 14+): allow GC to collect the referent ---
if (typeof WeakRef !== "undefined") {
  let obj = { name: "temp" };
  const weakRef = new WeakRef(obj);
  console.log("weakRef.deref() =", weakRef.deref()?.name); // "temp"
  obj = null; // No other reference; GC can collect. weakRef.deref() may later return undefined.
  console.log("After nulling, weakRef.deref() may be undefined:", weakRef.deref() === undefined || weakRef.deref() != null);
} else {
  console.log("WeakRef not available in this runtime");
}
