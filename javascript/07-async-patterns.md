# 07 — Promises and Async/Await

## Learning Objectives

- Understand the **Promise** as an object representing a future value, not the async operation itself.
- Distinguish between the three states: **pending**, **fulfilled**, **rejected**.
- Grasp how promises interact with the **microtask queue** and the event loop.
- Master **chaining** and error propagation with `.then()` and `.catch()`.
- Use `async`/`await` as syntactic sugar for promise‑based code, while understanding its true nature.
- Differentiate between **sequential** and **parallel** async operations using `Promise.all`, `Promise.allSettled`, etc.
- Recognise common pitfalls: forgetting `await`, unhandled rejections, swallowing errors.
- Apply senior‑level debugging techniques for promise‑related issues.

---

## 1. The Promise Mental Model

A **Promise** is an object that acts as a placeholder for a value that will be available in the future – the result of an asynchronous operation. It is **not** the operation itself, but a handle to its eventual outcome.

### 1.1 Promise States

A promise can be in one of three states:

- **pending** – initial state, neither fulfilled nor rejected.
- **fulfilled** – the operation completed successfully, and the promise has a resulting value.
- **rejected** – the operation failed, and the promise has a reason (error).

Once a promise settles (fulfilled or rejected), it becomes **immutable** – its state and value cannot change.

### 1.2 Internal Structure (Simplified)

Internally, a promise has:

- A `[[PromiseState]]` internal slot (pending, fulfilled, rejected).
- A `[[PromiseResult]]` internal slot holding the fulfilled value or rejection reason.
- A list of `[[PromiseFulfillReactions]]` and `[[PromiseRejectReactions]]` – the callbacks attached via `.then()` or `.catch()` that are queued as **microtasks** when the promise settles.

This is why promises are tightly integrated with the event loop: their callbacks always run as microtasks. See [06-event-loop-tasks-microtasks.md](06-event-loop-tasks-microtasks.md) for the full picture.

---

## 2. Creating and Using Promises

### 2.1 The Promise Constructor

```javascript
const p = new Promise((resolve, reject) => {
  // executor function – runs synchronously
  setTimeout(() => {
    resolve('done'); // or reject(new Error('fail'))
  }, 1000);
});
```

The executor runs **immediately** (synchronously) when the promise is created. It receives two functions: `resolve` and `reject`. Calling `resolve` transitions the promise to fulfilled; calling `reject` transitions it to rejected.

### 2.2 Consuming a Promise

```javascript
p.then(value => {
  console.log(value); // 'done' after 1 second
}).catch(error => {
  console.error(error);
}).finally(() => {
  console.log('cleanup');
});
```

- `.then()` takes up to two arguments: onFulfilled and onRejected. It returns a **new promise**.
- `.catch()` is syntactic sugar for `.then(undefined, onRejected)`.
- `.finally()` runs regardless of settlement, and does not receive the value or error; it passes through the original result.

Each of these methods returns a new promise, enabling **chaining**.

---

## 3. The Microtask Connection

When a promise settles, all attached reaction callbacks are queued as **microtasks** in the microtask queue. See [06-event-loop-tasks-microtasks.md](06-event-loop-tasks-microtasks.md) and [03-event-loop-and-async.md](03-event-loop-and-async.md). This is why:

```javascript
console.log(1);
Promise.resolve().then(() => console.log(2));
console.log(3);
// Output: 1, 3, 2
```

The `.then` callback is not executed synchronously; it is scheduled as a microtask and runs after the current synchronous code finishes.

**Senior insight:** Because microtasks run before the next task (and before rendering), promise callbacks have higher priority than `setTimeout` callbacks (tasks). This is by design to allow promise resolutions to be handled as soon as possible.

---

## 4. Chaining and Error Propagation

### 4.1 How Chaining Works

Each call to `.then()` returns a new promise that resolves with the return value of the callback. If the callback returns a plain value, the new promise fulfills with that value. If it returns another promise, the new promise adopts that promise's state.

```javascript
fetchUser()
  .then(user => fetchOrders(user.id)) // returns a promise
  .then(orders => console.log(orders)) // logs when fetchOrders fulfills
  .catch(err => console.error(err));
```

If any promise in the chain rejects, the chain jumps to the nearest `.catch()` (or `.then()` with an onRejected handler).

### 4.2 Error Handling Nuances

- If you omit a `.catch()`, a rejection will become an **unhandled rejection**. Modern environments log a warning and may eventually treat it as an error.
- Throwing an error inside a `.then()` callback (or returning a rejected promise) will reject the promise returned by that `.then()`.
- `.catch()` itself returns a new promise; if you return a value from `.catch()`, the chain becomes fulfilled again (unless you re‑throw).

**Example:**

```javascript
Promise.reject('fail')
  .catch(err => {
    console.log('caught', err);
    return 'recovery';
  })
  .then(value => console.log(value)); // 'recovery'
```

---

## 5. Async/Await – Syntactic Sugar with Substance

### 5.1 What `async` Does

An `async` function always returns a promise. If the function returns a value, that value is wrapped in a resolved promise. If it throws, the returned promise rejects.

```javascript
async function foo() {
  return 42;
}
// equivalent to:
function foo() {
  return Promise.resolve(42);
}
```

### 5.2 What `await` Does

`await` can only be used inside an `async` function. It pauses the execution of that **async function** until the awaited promise settles, and then resumes with the fulfilled value (or throws if the promise rejects).

Crucially, `await` does **not** block the main thread. It only suspends the current async function, allowing other code (microtasks, tasks, UI updates) to run. The function's continuation is scheduled as a microtask when the awaited promise settles.

```javascript
async function load() {
  console.log('start');
  const user = await fetchUser(); // pauses here
  console.log(user); // runs after fetchUser fulfills
}
console.log('before');
load();
console.log('after');
// Output: before, start, after, (later) user
```

### 5.3 Error Handling with Async/Await

Use `try/catch` to handle rejections:

```javascript
async function load() {
  try {
    const user = await fetchUser();
    const orders = await fetchOrders(user.id);
    console.log(orders);
  } catch (err) {
    console.error('Failed:', err);
  }
}
```

If you don't use `try/catch`, the rejection will cause the returned promise to reject, and you must handle it with `.catch()` when calling the async function.

---

## 6. Sequential vs Parallel Operations

One of the most common senior‑level distinctions is knowing when to run async operations sequentially vs in parallel.

### 6.1 Sequential (Wrong for Independent Tasks)

```javascript
const a = await fetchA();
const b = await fetchB(); // waits for fetchA to finish
```

If `fetchA` and `fetchB` are independent, this wastes time because they run one after the other.

### 6.2 Parallel with `Promise.all`

```javascript
const [a, b] = await Promise.all([fetchA(), fetchB()]);
```

`Promise.all` runs both promises concurrently and resolves when **all** have resolved. If any rejects, the whole `Promise.all` rejects immediately (fail‑fast).

### 6.3 Parallel with `Promise.allSettled`

```javascript
const results = await Promise.allSettled([fetchA(), fetchB()]);
```

Waits for all promises to settle (fulfill or reject) and returns an array of objects with status and value/reason. Useful when you need results from all, even if some fail.

### 6.4 `Promise.race` and `Promise.any`

- `Promise.race` settles with the first promise that settles (fulfills or rejects).
- `Promise.any` (ES2021) settles with the first **fulfilled** promise; if all reject, it rejects with an `AggregateError`.

### 6.5 Performance Implications

Parallel execution can significantly reduce total wait time. However, be mindful of:

- **Resource limits** – too many concurrent requests may overwhelm the server or the browser's connection limit.
- **Error handling** – `Promise.all` fails fast, which may be desirable or not.
- **Microtask queue impact** – Each resolved promise queues its `.then` callbacks as microtasks. If you have thousands of concurrently resolving promises, the microtask queue can become a bottleneck and delay rendering (as discussed in [07-ui-rendering-and-event-loop.md](07-ui-rendering-and-event-loop.md)).

---

## 7. Common Mistakes and How to Avoid Them

### 7.1 Forgetting `await`

```javascript
const user = fetchUser(); // user is a promise, not the actual user
console.log(user.name); // undefined or error
```

**Fix:** Always `await` promises inside async functions, or use `.then()`.

### 7.2 Sequential `await` on Independent Tasks

Already covered – use `Promise.all` for concurrency.

### 7.3 Not Returning Promises in `.then()` Chains

```javascript
fetchUser()
  .then(user => {
    fetchOrders(user.id); // missing return – the next then receives undefined
  })
  .then(orders => console.log(orders)); // orders is undefined
```

**Fix:** Always return the promise if you want the chain to wait.

### 7.4 Swallowing Errors with Empty `.catch()`

```javascript
Promise.reject('error').catch(() => {}); // error silently ignored
```

**Better:** At least log the error, or re‑throw if you can't recover.

### 7.5 Unhandled Rejections

If you create a promise and never attach a `.catch()` (or forget `await` inside a try/catch), a rejection may become unhandled. In Node, this can crash the process; in browsers, it logs a warning but may cause silent failures.

**Prevention:** Always handle rejections, either with `.catch()` or try/catch with `await`.

---

## 8. Debugging Unhandled Rejections in Large Applications

### 8.1 Browser Tools

- Chrome DevTools Console shows unhandled rejections with a red warning. You can break on unhandled rejections by enabling the "Pause on exceptions" button (pause on caught exceptions too) or by setting a breakpoint in the "Sources" tab for the `unhandledrejection` event.

### 8.2 Global Handlers

Attach a global handler to log or report unhandled rejections:

```javascript
window.addEventListener('unhandledrejection', event => {
  console.error('Unhandled rejection:', event.reason);
  // Report to error tracking service (Sentry, etc.)
  event.preventDefault(); // optional – prevents default console warning
});
```

In Node:

```javascript
process.on('unhandledRejection', (reason, promise) => {
  console.error('Unhandled Rejection at:', promise, 'reason:', reason);
});
```

### 8.3 Tracking Down the Source

- Use stack traces – modern engines include async stack traces, showing where the promise was created and where it was rejected.
- If the stack trace is insufficient, add logging at creation points: `console.trace('promise created')` inside the executor.
- For large applications, instrument promises with a wrapper that adds contextual information (e.g., a unique ID) and logs lifecycle events.

### 8.4 Linting Rules

ESLint rules like `no-floating-promises` (TypeScript‑eslint) or `promise/catch-or-return` can catch forgotten `await` or missing `.catch()`.

---

## 9. Interview Questions and Answers

### Beginner

**Q1: What is a promise?**  
**A:** A promise is an object that represents the eventual completion (or failure) of an asynchronous operation and its resulting value. It acts as a placeholder for a future value, allowing you to attach callbacks that will run when the operation settles.

**Q2: What are the states of a promise?**  
**A:** A promise can be in one of three states: **pending** (initial state), **fulfilled** (operation completed successfully), or **rejected** (operation failed). Once a promise settles (fulfilled or rejected), it becomes immutable.

**Q3: What does `await` do?**  
**A:** `await` is used inside an `async` function to pause the execution of that function until the awaited promise settles. It then returns the fulfilled value or throws the rejection reason. Importantly, it does not block the main thread; it only suspends the current async function, allowing other code to run.

### Intermediate

**Q4: What is the difference between `.then()` and `await`?**  
**A:** `.then()` is a method on a promise that attaches callbacks and returns a new promise. It can be chained. `await` is a keyword that can only be used inside an `async` function; it suspends the function until the promise settles, making asynchronous code look more like synchronous code. Under the hood, `await` is syntactic sugar for consuming promises, and both rely on the same microtask mechanism.

**Q5: How does error handling work with promises?**  
**A:** With promises, you handle errors by attaching a `.catch()` callback, or by providing a second argument to `.then()`. In an `async` function, you use `try/catch` around `await` expressions. If a promise rejects and there is no handler attached, it becomes an unhandled rejection, which may cause warnings or crashes.

**Q6: What is the benefit of `Promise.all()`?**  
**A:** `Promise.all()` allows you to run multiple promises concurrently and wait for all of them to fulfill. It returns a single promise that resolves with an array of results in the same order. If any promise rejects, the whole `Promise.all()` rejects immediately. This is useful when you have independent asynchronous tasks and you need all results before proceeding.

### Senior

**Q7: When would you avoid `Promise.all()`?**  
**A:** I would avoid `Promise.all()` in the following scenarios:  
- When you need to handle partial success – use `Promise.allSettled()` instead to get results from all promises even if some fail.  
- When you want to limit concurrency to avoid overwhelming a server or browser resources (e.g., too many simultaneous HTTP requests). In that case, you might implement a concurrency limiter or use `Promise.all()` in batches.  
- When you need to preserve order of execution for side effects – if each operation depends on the previous one not just for data but for side effects, sequential processing is safer.  
- When you have a huge number of promises – `Promise.all()` will schedule all their `.then()` callbacks as microtasks when they resolve, potentially clogging the microtask queue and delaying rendering. In such cases, consider processing in chunks or using a streaming approach.

**Q8: Explain the performance difference between sequential and parallel awaiting.**  
**A:** Sequential `await` (one after another) will take the sum of the individual times because each operation waits for the previous to complete. Parallel execution with `Promise.all()` will take approximately the time of the slowest operation, assuming the operations are truly independent and can run concurrently. However, there are trade‑offs:  
- Parallel execution increases memory usage and may saturate network connections.  
- If the operations are CPU‑bound (not I/O), they still run on the main thread and will block each other; true parallelism would require Web Workers.  
- In a UI context, too many microtasks from many resolved promises can delay rendering. Therefore, for very large numbers of promises, a batched approach might be more responsive.

**Q9: How would you debug unhandled promise rejections in a large application?**  
**A:** I would take a multi‑pronged approach:  
1. **Global handlers:** Attach an `unhandledrejection` event listener in the browser (or `process.on('unhandledRejection')` in Node) to log the error with a full stack trace and report it to an error‑tracking service like Sentry.  
2. **Source mapping:** Ensure source maps are available so stack traces point to original code.  
3. **Linting:** Use ESLint rules (`no-floating-promises`, `promise/catch-or-return`) to catch missing error handling at development time.  
4. **Async stack traces:** Modern engines (Chrome, Node) provide async stack traces; I would examine them to see where the promise was created and where it rejected.  
5. **Manual instrumentation:** If a particular rejection is hard to trace, I might temporarily wrap the promise creation with a logger that adds a unique identifier and traces the promise's lifecycle.  
6. **Testing:** Write tests that ensure all promise‑based functions handle rejections appropriately.  
7. **Code review:** Enforce that every promise chain ends with a `.catch()` or is awaited inside a try/catch.

---

## 10. Summary

- Promises are objects representing future values, not the async operations themselves.
- They have three states: pending, fulfilled, rejected.
- Promise callbacks are always queued as **microtasks**, giving them priority over tasks like `setTimeout`.
- Chaining with `.then()` and `.catch()` allows sequential composition; each returns a new promise.
- `async`/`await` provides a more synchronous‑looking syntax, but it's still promise‑based and non‑blocking.
- Understand when to use sequential vs parallel execution (`Promise.all`, `allSettled`, etc.) to optimise performance.
- Be aware of common pitfalls: forgetting `await`, not returning promises, swallowing errors, and unhandled rejections.
- Master debugging techniques for unhandled rejections to maintain robust applications.

---

## 11. Further Reading

- [MDN: Promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise)
- [MDN: async function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function)
- [JavaScript.info: Promises, async/await](https://javascript.info/async)
- [V8 blog: Fast async](https://v8.dev/blog/fast-async) (deep dive on implementation)

---

## 12. Observables (preview)

Next, we explore **RxJS Observables** and how they compare with promises:

- **Observable:** A stream of values over time; can represent zero, one, or many values; can represent async or sync delivery.
- **Lazy:** Nothing runs until you **subscribe**. Cancellation is supported (unsubscribe).
- **Contrast with Promise:** One value (or rejection) vs many; no cancellation for standard Promise. In Angular, HTTP and router use Observables; you'll use operators (map, switchMap, etc.) and unsubscribe to avoid leaks.

Full treatment: [angular/02-rxjs-deep-dive.md](../angular/02-rxjs-deep-dive.md).

### When to use each (senior decision guide)

| Need | Use |
|------|-----|
| One-off async result (e.g. single HTTP request, timeout) | **Promise** or **async/await** |
| Multiple values over time, cancellation, or composition of streams | **Observable** (RxJS) |
| Legacy or callback-only APIs | **Callbacks** (or wrap in Promise/Observable) |
| Sequential async steps with clear steps | **async/await** |
| Parallel independent work | **Promise.all** (or `forkJoin` in RxJS) |

---

## Practice

- [practice/07-promises-async-await.js](practice/07-promises-async-await.js) — Microtask order, Promise constructor/consumer, chaining, async/await, sequential vs parallel, common mistakes, Observables teaser. Run with Node.
- [practice/07-observable-preview.js](practice/07-observable-preview.js) — Simple Observable preview (lazy, subscribe, unsubscribe). No RxJS required.
