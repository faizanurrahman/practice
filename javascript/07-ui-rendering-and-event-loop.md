# 07 — UI Rendering and the Event Loop: How Tasks and Microtasks Affect User Experience

## Learning Objectives

- Understand where rendering fits in the event loop cycle.
- Identify how **microtask chains**, **heavy microtask processing**, and **long tasks** can block rendering and degrade user experience.
- Learn practical techniques to **break up work**, **yield to the event loop**, and **keep the UI responsive**.
- Use browser developer tools to diagnose rendering delays caused by task/microtask scheduling.

---

## 1. Where Does Rendering Happen in the Event Loop?

In a browser environment, the event loop is responsible not only for executing JavaScript but also for updating the rendering (paint, layout, etc.). The exact timing is defined by the HTML specification, but a practical mental model is:

1. **Run the oldest task** from the task queue (macrotask). This task may execute synchronous JavaScript, schedule microtasks, etc.  
2. **Process all microtasks** – execute every microtask in the queue, including any microtasks that are added during this step (i.e., the queue is emptied).  
3. **Perform rendering updates** if it’s time to render (browsers aim for ~60fps, so roughly every 16.6ms). This step may include `requestAnimationFrame` callbacks, style recalc, layout, paint, and compositing.  
4. **Go back to step 1** (next task).

**Key insight:** Rendering happens **between tasks**, but **never between microtasks**. That means:

- All microtasks queued during a task will be executed **before** the browser gets a chance to paint the next frame.  
- If the microtask processing step takes too long, or if microtasks continuously enqueue more microtasks, rendering will be **delayed**.

---

## 2. How Microtasks Can Block Rendering

### 2.1 Long Microtask Chains

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

Here, each microtask enqueues another microtask. The microtask queue is never empty, so the event loop never proceeds to the rendering step or the task queue. The page becomes unresponsive, and any pending tasks (like user events or timers) are also blocked.

### 2.2 Heavy Microtask Processing

Even if the chain is finite, if each microtask performs a significant amount of work (e.g., complex calculations, DOM manipulations), the total time spent in the microtask step could exceed the frame budget (16.6ms). Rendering will be delayed until all microtasks finish, causing dropped frames and jank.

```javascript
// Many microtasks doing heavy work
for (let i = 0; i < 1000; i++) {
  Promise.resolve().then(() => {
    const start = performance.now();
    while (performance.now() - start < 5) {} // simulate 5ms of work
  });
}
// The browser won't render until all microtasks finish.
```

### 2.3 Microtask Starvation of Tasks

Because microtasks are processed in a single batch, they can starve not only rendering but also **tasks** (including user input events, which are tasks). This leads to unresponsiveness.

---

## 3. How Long Tasks Block Rendering

A **long task** is any task that takes more than ~50ms to execute. Since rendering only happens **after** a task completes, a single long task can delay multiple frames.

```javascript
// Long synchronous task
setTimeout(() => {
  const start = performance.now();
  while (performance.now() - start < 500) {} // blocks for 500ms
}, 0);
```

During that 500ms, the browser cannot render updates, handle user input, or run any other tasks. The UI freezes.

---

## 4. Techniques to Keep the UI Responsive

### 4.1 Break Up Long Tasks with `setTimeout`

Instead of doing all work in one synchronous block, split it into chunks scheduled as separate tasks:

```javascript
function processLargeArray(array) {
  let index = 0;
  function chunk() {
    const start = performance.now();
    while (index < array.length && performance.now() - start < 10) {
      process(array[index]);
      index++;
    }
    if (index < array.length) {
      setTimeout(chunk, 0); // schedule next chunk as a task
    }
  }
  setTimeout(chunk, 0);
}
```

This yields to the event loop after each chunk, allowing rendering and user interactions to occur between chunks.

### 4.2 Use `requestAnimationFrame` for Visual Updates

If your work is related to animations or visual changes, schedule it in `requestAnimationFrame`, which runs just before the browser paints:

```javascript
function updateAnimation() {
  element.style.transform = `translateX(${x}px)`;
  x += 1;
  if (x < 100) {
    requestAnimationFrame(updateAnimation);
  }
}
requestAnimationFrame(updateAnimation);
```

### 4.3 Use `requestIdleCallback` for Non‑Urgent Work

`requestIdleCallback` allows you to schedule work during idle periods when the browser has free time before the next frame:

```javascript
requestIdleCallback((deadline) => {
  while (deadline.timeRemaining() > 0 && tasks.length > 0) {
    processTask(tasks.shift());
  }
  if (tasks.length > 0) {
    requestIdleCallback(processRemaining);
  }
});
```

Use it for analytics, logging, or other background tasks that shouldn’t interfere with user interaction.

### 4.4 Avoid Recursive Microtask Chains

Always have a base case. If you need to loop asynchronously, use tasks (`setTimeout`) rather than microtasks to avoid starving the event loop:

```javascript
// Bad: recursive microtask
function badLoop(iterations) {
  if (iterations > 0) {
    Promise.resolve().then(() => badLoop(iterations - 1));
  }
}

// Better: use tasks
function goodLoop(iterations) {
  if (iterations > 0) {
    setTimeout(() => goodLoop(iterations - 1), 0);
  }
}
```

### 4.5 Move Heavy Computation to Web Workers

For CPU‑intensive tasks that don’t need direct DOM access, use Web Workers. They run in a separate thread and communicate via messages (tasks), so they don’t block the main thread:

```javascript
// main.js
const worker = new Worker('worker.js');
worker.postMessage(data);
worker.onmessage = (e) => { /* update UI */ };

// worker.js
self.onmessage = (e) => {
  const result = heavyComputation(e.data);
  self.postMessage(result);
};
```

### 4.6 Be Mindful of Microtasks in Frameworks

In Angular, Zone.js patches many asynchronous APIs and may trigger change detection, which can schedule microtasks. In React, state updates may batch work within a frame. If you have a complex reactive flow (many promise callbacks or RxJS operators), you might inadvertently create a long microtask chain. Use techniques like:

- `debounceTime` or throttling for rapid events.  
- `async` pipe / hooks that batch renders.  
- Running heavy work outside the main reactive path and re‑entering only when needed.

---

## 5. Diagnosing Rendering Blockage with DevTools

### 5.1 Performance Tab

1. Record a performance profile while interacting with your app.  
2. Look for **long tasks** (often flagged in red) in the main thread timeline.  
3. Expand a task to see its call stack – identify if it’s synchronous JS or many promise callbacks.  
4. The Summary view shows time spent in Scripting, Rendering, Painting. Excessive scripting time suggests heavy JS is blocking frames.

### 5.2 Microtask Identification

Microtasks don’t have a separate track, but they appear as continuous JS execution following a task. If you see a task followed by a long contiguous JS block (with no rendering in between), that may be heavy microtask processing.

### 5.3 Using `performance.mark` and `performance.measure`

You can manually mark work to correlate with the event loop:

```javascript
performance.mark('start-work');
// ... your work ...
performance.mark('end-work');
performance.measure('work', 'start-work', 'end-work');
```

Then inspect these marks in DevTools to see their duration and where they fall relative to frames.

---

## 6. Summary

- Rendering occurs **between tasks**, after all microtasks from the previous task have been processed.  
- Long tasks and long or infinite microtask chains can block rendering and cause jank or UI freezes.  
- Techniques like breaking up work (chunking), `requestAnimationFrame`, `requestIdleCallback`, and Web Workers help keep the UI responsive.  
- Developer tools (Performance tab, custom marks) are essential to diagnose where time is spent and how it impacts frames.

---

## 7. Practice

This module is primarily about **reasoning** and **tooling**, so practice is best done in a browser:

- Build or open a simple page that:\n  - Runs a long synchronous loop in response to a button click.  \n  - Runs the same work chunked with `setTimeout`.\n- Use the **Performance** tab in DevTools to record both cases and compare:\n  - Frame rate and long tasks.  \n  - Time spent in scripting vs rendering.\n- Add a small microtask experiment (e.g., a recursive promise loop) and observe how it affects responsiveness.\n\nLog your observations (what blocked, what stayed smooth) in `workspace/debug-log.md` and update `workspace/mental-models.md` with a one‑line definition of “long task” and “microtask starvation.”\n+
