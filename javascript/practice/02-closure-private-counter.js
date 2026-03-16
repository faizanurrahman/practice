/**
 * Interview: Implement a private counter with closure.
 * See: 02-closures-and-scope.md
 *
 * Factory returns an object with increment(), decrement(), getCount().
 * No direct access to the count variable.
 */

function createCounter(initial = 0) {
  let count = initial; // "private" — only inner functions can access it
  return {
    increment() {
      count += 1;
      return count;
    },
    decrement() {
      count -= 1;
      return count;
    },
    getCount() {
      return count;
    },
  };
}

// Usage
const counter = createCounter(10);
console.log(counter.getCount());   // 10
counter.increment();
counter.increment();
console.log(counter.getCount());   // 12
counter.decrement();
console.log(counter.getCount());   // 11
// counter.count would be undefined — no direct access
console.log("count" in counter ? "has count" : "no count property"); // no count property
