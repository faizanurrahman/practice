/**
 * "What is the output?" — interview-style event loop snippet.
 * See: 03-event-loop-and-async.md
 *
 * Predict before running: node practice/03-event-loop-interview.js
 */

console.log("A");

setTimeout(() => console.log("B"), 0);

Promise.resolve()
  .then(() => console.log("C"))
  .then(() => console.log("D"));

console.log("E");

// Answer: A, E, C, D, B
// Sync: A, E. Then microtasks: C, D. Then macrotask: B.
