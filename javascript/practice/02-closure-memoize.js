/**
 * Interview: Implement memoize(fn) using a closure-held cache.
 * See: 02-closures-and-scope.md
 *
 * Same arguments → return cached result instead of recomputing.
 */

function memoize(fn) {
  const cache = new Map(); // closure: survives across calls
  return function (...args) {
    const key = JSON.stringify(args); // simple key; use a better strategy for objects if needed
    if (cache.has(key)) {
      return cache.get(key);
    }
    const result = fn.apply(this, args);
    cache.set(key, result);
    return result;
  };
}

// Example: expensive (or just slow) function
function slowAdd(a, b) {
  console.log("  computing slowAdd(", a, b, ")");
  return a + b;
}

const memoizedAdd = memoize(slowAdd);
console.log(memoizedAdd(1, 2));   // logs "computing...", returns 3
console.log(memoizedAdd(1, 2));   // no log, returns 3 from cache
console.log(memoizedAdd(2, 3));   // logs "computing...", returns 5
