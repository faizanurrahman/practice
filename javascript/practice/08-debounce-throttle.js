/**
 * Debounce and throttle: control execution frequency.
 * Concept: ../08-performance-fundamentals.md
 * Run: node practice/08-debounce-throttle.js
 */

function debounce(fn, delayMs) {
  let timer = null;
  return function (...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
      timer = null;
    }, delayMs);
  };
}

function throttle(fn, intervalMs) {
  let last = 0;
  return function (...args) {
    const now = Date.now();
    if (now - last >= intervalMs) {
      last = now;
      fn.apply(this, args);
    }
  };
}

// --- Demo: debounce (only after 100ms of no calls) ---
let debounceCount = 0;
const debounced = debounce(() => {
  debounceCount++;
  console.log("Debounced run", debounceCount);
}, 100);

console.log("Calling debounced 5 times quickly:");
debounced();
debounced();
debounced();
debounced();
debounced();

setTimeout(() => {
  console.log("After 150ms: one debounced run should have happened.");
  // --- Demo: throttle (at most once per 100ms) ---
  let throttleCount = 0;
  const throttled = throttle(() => {
    throttleCount++;
    console.log("Throttled run", throttleCount);
  }, 100);
  console.log("Calling throttled 5 times at 0, 50, 100, 150, 200 ms:");
  throttled(); // 0
  setTimeout(() => throttled(), 50);
  setTimeout(() => throttled(), 100);
  setTimeout(() => throttled(), 150);
  setTimeout(() => throttled(), 200);
  setTimeout(() => console.log("Done."), 350);
}, 150);
