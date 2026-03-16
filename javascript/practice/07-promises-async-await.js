/**
 * Async patterns: Promises, chaining, async/await.
 * Concept: ../07-async-patterns.md
 * Run: node practice/07-promises-async-await.js
 */

// --- 1. Promise: then, catch ---
const p = Promise.resolve(10)
  .then((x) => x + 1)
  .then((x) => {
    throw new Error("oops");
  })
  .catch((err) => {
    console.log("Caught:", err.message);
    return 0;
  });
p.then((v) => console.log("Final value:", v)); // Final value: 0

// --- 2. async/await and try/catch ---
async function fetchSomething() {
  await new Promise((r) => setTimeout(r, 10));
  return "done";
}
async function main() {
  try {
    const result = await fetchSomething();
    console.log("Result:", result);
  } catch (e) {
    console.log("Error:", e);
  }
}
main();

// --- 3. Promise.all for parallel work ---
async function parallel() {
  const [a, b, c] = await Promise.all([
    Promise.resolve(1),
    Promise.resolve(2),
    Promise.resolve(3),
  ]);
  console.log("Parallel result:", a, b, c);
}
parallel();
