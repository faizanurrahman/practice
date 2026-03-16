/**
 * Interview: Implement curry / partial application.
 * See: 02-closures-and-scope.md
 *
 * add(a)(b)(c) or curry(fn) so that curry(f)(1)(2) === f(1, 2).
 */

// Example: add(a)(b)(c)
function add(a) {
  return function (b) {
    return function (c) {
      return a + b + c;
    };
  };
}
console.log("add(1)(2)(3):", add(1)(2)(3)); // 6

// Generic curry: curry(fn) returns a function that accepts args one (or more) at a time
function curry(fn) {
  return function curried(...args) {
    if (args.length >= fn.length) {
      return fn.apply(this, args);
    }
    return function (...nextArgs) {
      return curried.apply(this, args.concat(nextArgs));
    };
  };
}

const sum3 = (a, b, c) => a + b + c;
const curriedSum3 = curry(sum3);
console.log("curriedSum3(1)(2)(3):", curriedSum3(1)(2)(3));   // 6
console.log("curriedSum3(1, 2)(3):", curriedSum3(1, 2)(3));  // 6
