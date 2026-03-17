# 06 — Event Loop, Tasks, and Microtasks

## Learning Objectives

- Understand why the event loop is a critical concept for senior JavaScript developers.
- Visualise the core components: **call stack**, **task queue** (macrotasks), and **microtask queue**.
- Distinguish between **tasks** (setTimeout, events, script) and **microtasks** (promise callbacks, `queueMicrotask`).
- Predict execution order of mixed synchronous, microtask, and task code.
- Explain why `setTimeout(..., 0)` does **not** run immediately.
- Recognise the risk of **microtask starvation** and its performance implications.
- Apply event loop knowledge to diagnose timing and UI issues.

---

## 1. Why the Event Loop Matters

Many developers can write promises and use `setTimeout`, but far fewer can correctly predict the order of execution when these are combined. The event loop is the engine’s orchestration mechanism that determines **when** different pieces of code run. Understanding it is one of the biggest differentiators between a junior and a senior engineer.

Senior engineers use this knowledge to:

- Debug timing‑related bugs.
- Optimise responsiveness.
- Avoid accidental blocking of the main thread.
- Design APIs that behave predictably.

---

## 2. The Core Model

JavaScript runs in a single‑threaded environment, but the browser (or Node.js) provides concurrency through an **event loop**. The loop continuously checks for work and executes it in a specific order.

At a high level, the event loop manages three main structures:

1. **Call Stack** – where synchronous code runs. Functions are pushed when called and popped when they return.
2. **Task Queue** (macrotask queue) – holds tasks like `setTimeout` callbacks, DOM event handlers, and `setInterval`.
3. **Microtask Queue** – holds microtasks like `Promise.then/catch/finally` callbacks and `queueMicrotask` functions.

### Event Loop Cycle

The loop runs indefinitely, following this pattern:

1. **Run all synchronous code** in the call stack until it is empty.  
2. **Process the microtask queue**: execute every microtask in the queue (including any new microtasks enqueued during this step) until the queue is empty.  
3. **Possibly render** (in browsers) – update the rendering if needed.  
4. **Take the next task** from the task queue and execute it.  
5. Repeat.

This cycle ensures that microtasks always run **before** the next task, and that rendering happens between tasks (but not between microtasks).

---

## 3. Tasks vs Microtasks

| Feature            | Tasks (Macrotasks)                          | Microtasks                                  |
|--------------------|----------------------------------------------|---------------------------------------------|
| **Examples**       | `setTimeout`, `setInterval`, DOM events, initial `script`, `setImmediate` (Node) | `Promise.then/catch/finally`, `queueMicrotask`, `MutationObserver` |
| **When they run**  | After the current stack and after all microtasks have been processed. | Immediately after the current stack, before the next task, and before rendering. |
| **Order**          | One task is taken per loop iteration.        | All microtasks are processed in a single go (the queue is emptied). |
| **Starving risk**  | Low – tasks are interleaved with rendering.  | Higher – if microtasks continuously enqueue new microtasks, tasks and rendering can be delayed. |

---

## 4. Basic Example – Predicting Output

```javascript
console.log('A');

setTimeout(() => console.log('B'), 0);

Promise.resolve().then(() => console.log('C'));

console.log('D');
```

**Step‑by‑step:**

1. **Synchronous code** (call stack):  
   - `console.log('A')` → `A`.  
   - `setTimeout` schedules a task (for `B`) in the task queue.  
   - `Promise.resolve().then(...)` schedules a microtask (for `C`) in the microtask queue.  
   - `console.log('D')` → `D`.  
   - Stack is now empty.
2. **Microtask checkpoint:**  
   - Run microtasks: log `C`.  
3. **Task checkpoint:**  
   - Run next task: log `B`.

**Output:**

```
A
D
C
B
```

`setTimeout(..., 0)` does **not** run immediately; it runs after the current stack and after all pending microtasks.

---

## 5. Advanced Example – Nested Promises and Timers

```javascript
console.log(1);

setTimeout(() => console.log(2), 0);

Promise.resolve()
  .then(() => {
    console.log(3);
    setTimeout(() => console.log(4), 0);
  })
  .then(() => console.log(5));

console.log(6);
```

**Trace:**

1. **Synchronous:** logs `1`, schedules task for `2`, schedules first promise microtask, logs `6`.  
2. **Microtasks:**  
   - First `then`: logs `3`, schedules task for `4`.  
   - Second `then`: logs `5`.  
3. **Tasks:**  
   - First scheduled task: logs `2`.  
   - Second scheduled task: logs `4`.

**Output:**

```
1
6
3
5
2
4
```

Even though the `4` timer was scheduled during a microtask, it still runs **after** the previously queued task (`2`), because tasks are processed FIFO.

---

## 6. Microtask Starvation – A Performance Hazard

Because microtasks are processed in a single batch until the queue is empty, a poorly written piece of code that continuously adds new microtasks can **starve** the task queue and prevent rendering.

```javascript
function loopMicrotasks() {
  Promise.resolve().then(() => {
    // do some work
    loopMicrotasks(); // enqueue another microtask
  });
}

loopMicrotasks();
setTimeout(() => console.log('Task finally runs'), 0);
```

Here, the `setTimeout` callback will **never** run because the microtask queue is never emptied. Each microtask re‑enqueues another microtask, so the event loop never reaches the task queue.

**Impact:**

- UI updates are blocked.  
- User interactions (task‑based events) are delayed or never processed.

Mitigation:

- Avoid recursive microtask enqueuing without a base condition.  
- Use tasks (`setTimeout`, `setInterval`) to yield back to the loop for long‑running/repeated work.

---

## 7. How the Event Loop Affects UI Rendering (high‑level)

In browsers, rendering typically happens **between tasks**, after microtasks have been processed. That means:

- Long tasks delay rendering.  
- Long or infinite microtask chains delay **both** tasks and rendering.

This is covered in more depth in `07-ui-rendering-and-event-loop.md`, but the key idea is: **microtasks run before render; tasks are the only place where the browser can paint**.

---

## 8. Interview Questions and Answers

### Beginner

- **What is the event loop?**  
  The event loop is the mechanism that coordinates JavaScript execution with asynchronous callbacks. It pulls tasks from queues and pushes them onto the call stack when the stack is empty.

- **What is the difference between synchronous and asynchronous code?**  
  Synchronous code runs immediately on the call stack; asynchronous code is scheduled to run later via tasks or microtasks.

- **Why doesn’t `setTimeout(fn, 0)` run immediately?**  
  Because it schedules a **task** that only runs after the current stack completes and all pending microtasks have been processed.

### Intermediate

- **What is the difference between a microtask and a task?**  
  Microtasks (promise callbacks, `queueMicrotask`) run right after the current stack, before the next task, and all are processed in one batch. Tasks (timers, events) run one per iteration and are interleaved with rendering.

- **Given a mixed code snippet with `Promise.then` and `setTimeout`, predict the output order.**  
  Use the rule: synchronous → microtasks → tasks.

### Senior

- **Explain why a promise callback usually runs before a timer callback scheduled in the same tick.**  
  Promise callbacks are microtasks; the event loop drains the microtask queue before processing the next task (which includes timer callbacks).

- **How can misusing microtasks impact performance?**  
  Microtasks that chain indefinitely or do heavy work can starve tasks and rendering, freezing the UI.

---

## 9. Summary

- The event loop coordinates the call stack, microtask queue, task queue, and (in browsers) rendering.  
- Microtasks run after the current stack and **before** the next task; all queued microtasks are processed in a batch.  
- Tasks are processed one per loop iteration and are where rendering can occur between them.  
- `setTimeout(..., 0)` still enqueues a task; it never runs “immediately.”  
- Misusing microtasks can starve tasks and render steps, harming responsiveness.

---

## 10. Practice

Use the existing practice files:

- `practice/03-event-loop-order.js` — Predict the order of logs with mixed `setTimeout`, `Promise.then`, and `queueMicrotask`; then run to verify.  
- `practice/03-event-loop-interview.js` — Walk through the snippet as if in an interview; explain stack → microtasks → tasks.

Follow your Phase 0 loop:

1. **Definition:** Write a one‑line definition of the event loop and of microtasks vs tasks in `workspace/mental-models.md`.  
2. **Flow:** Describe the 1–2–3–4 event loop cycle in plain words.  
3. **Run:** Execute the practice files with `node`.  
4. **Break:** Modify the examples (add more promises or timers) to challenge your understanding.  
5. **Explain:** Record why the order changed in `workspace/debug-log.md` using the language of call stack, microtasks, and tasks.

