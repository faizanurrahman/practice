/**
 * Promises and Async/Await: microtasks, chaining, async/await, parallel, pitfalls, Observables teaser.
 * Concept: ../07-async-patterns.md
 * Run: node practice/07-promises-async-await.js
 */

// --- 1. Microtask order (Section 3: callbacks run after sync code) ---
console.log("--- 1. Microtask order ---");
console.log(1);
Promise.resolve().then(() => console.log(2));
console.log(3);
// Output: 1, 3, 2

// --- 2. Promise constructor and consumer: then, catch, finally (Sections 2.1–2.2) ---
console.log("\n--- 2. Promise constructor & consumer ---");
const p = new Promise((resolve, reject) => {
  setTimeout(() => resolve("done"), 50);
});
p.then((value) => console.log("then:", value))
  .catch((err) => console.error("catch:", err))
  .finally(() => console.log("finally: cleanup"));

// --- 3. Chaining and error propagation; recovery in .catch (Section 4) ---
console.log("\n--- 3. Chaining & error propagation ---");
const chain = Promise.resolve(10)
  .then((x) => x + 1)
  .then((x) => {
    throw new Error("oops");
  })
  .catch((err) => {
    console.log("Caught:", err.message);
    return 0;
  });
chain.then((v) => console.log("Final value:", v)); // Final value: 0

// Recovery: reject → catch returns value → chain becomes fulfilled
Promise.reject("fail")
  .catch((err) => {
    console.log("Recovery caught:", err);
    return "recovery";
  })
  .then((value) => console.log("After recovery:", value));

// --- 4. async/await and try/catch (Section 5) ---
console.log("\n--- 4. async/await & try/catch ---");
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

// --- 5. Sequential vs parallel (Section 6) ---
console.log("\n--- 5. Sequential vs parallel ---");
async function fetchA() {
  await new Promise((r) => setTimeout(r, 20));
  return "A";
}
async function fetchB() {
  await new Promise((r) => setTimeout(r, 20));
  return "B";
}

// Sequential: total time ~40ms
(async function sequential() {
  const a = await fetchA();
  const b = await fetchB();
  console.log("Sequential:", a, b);
})();

// Parallel: total time ~20ms
(async function parallel() {
  const [a, b] = await Promise.all([fetchA(), fetchB()]);
  console.log("Parallel:", a, b);
})();

// Promise.allSettled: get all results even if some fail
Promise.allSettled([
  Promise.resolve(1),
  Promise.reject(new Error("two")),
  Promise.resolve(3),
]).then((results) => {
  console.log(
    "allSettled:",
    results.map((r) => (r.status === "fulfilled" ? r.value : r.reason?.message))
  );
});

// --- 6. Common mistakes (Section 7) – commented examples ---
// Forgetting await: const user = fetchUser(); console.log(user.name); // user is a Promise!
// Missing return in .then: .then(user => { fetchOrders(user.id); }) → next .then gets undefined
// Empty .catch(() => {}) swallows errors – at least log or rethrow

// --- 7. Unhandled rejection (Section 8) – optional; uncomment to try ---
// process.on('unhandledRejection', (reason, promise) => {
//   console.error('Unhandled Rejection:', reason);
// });

// --- 8. Observables teaser: contrast Promise (single value) vs stream (Section 12) ---
console.log("\n--- 8. Promise vs Observable (teaser) ---");
// Promise: single value, no cancellation
Promise.resolve(1).then((v) => console.log("Promise (single value):", v));

// Minimal Observable: stream of values, lazy, cancellable. Full preview: practice/07-observable-preview.js
function simpleObservable(produce) {
  return {
    subscribe(observer) {
      let cancelled = false;
      try {
        produce({
          next(v) {
            if (!cancelled && observer.next) observer.next(v);
          },
          complete() {
            if (!cancelled && observer.complete) observer.complete();
          },
        });
      } catch (e) {
        if (!cancelled && observer.error) observer.error(e);
      }
      return {
        unsubscribe() {
          cancelled = true;
        },
      };
    },
  };
}
const obs = simpleObservable((sub) => {
  sub.next("stream-1");
  sub.next("stream-2");
  sub.complete();
});
obs.subscribe({
  next: (v) => console.log("Observable (stream):", v),
  complete: () => console.log("Observable complete"),
});
