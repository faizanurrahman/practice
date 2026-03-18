# 12 — Memory Management and Garbage Collection

## Learning Objectives

- Understand that JavaScript uses **automatic memory management** with garbage collection.
- Learn the key concept: **reachability** – an object stays alive as long as it is reachable from the root.
- Identify common **memory leak patterns**: global references, forgotten listeners, closures, timers.
- Use **WeakMap** and **WeakSet** to avoid preventing garbage collection.
- Diagnose memory leaks in production using browser DevTools and heap snapshots.

---

## 1. Automatic Memory Management

JavaScript engines automatically allocate memory when objects are created and reclaim it when those objects are no longer needed. The primary mechanism is **garbage collection (GC)** based on **reachability**.

**Roots** are objects that are always reachable:

- Global object (`window` in browsers, `global` in Node)
- Currently executing functions and their local variables (the stack)
- Active timers, event listeners, and promise callbacks that are scheduled
- DOM elements that are attached to the document

Any object that can be reached by following references from these roots is considered **reachable** and will not be garbage‑collected. Unreachable objects become eligible for GC.

---

## 2. Common Memory Leak Patterns

A memory leak occurs when an object is no longer needed but remains reachable (often unintentionally), preventing GC.

### 2.1 Global References

Accidentally assigning to a variable without declaration (in non‑strict mode) creates a global variable.

```javascript
function leak() {
  leakedVar = 'I am global'; // becomes property of window
}
```

Even after the function runs, `leakedVar` stays in the global object forever.

### 2.2 Forgotten Event Listeners

Adding an event listener that references an object can keep that object alive, even if the DOM element is removed.

```javascript
class Component {
  constructor(element) {
    element.addEventListener('click', this.onClick.bind(this));
  }
  onClick() { /* ... */ }
}
```

If the element is removed but the listener is not removed, the component instance (and anything it references) remains reachable via the event listener closure.

### 2.3 Closures Retaining Large Objects

A closure that captures a large object keeps that object alive as long as the closure itself is alive.

```javascript
function createLeaky() {
  const hugeArray = new Array(1000000).fill('data');
  return function() {
    console.log('still here');
    // hugeArray is captured, even though not used!
  };
}
const fn = createLeaky(); // hugeArray persists because fn references it
```

### 2.4 Timers and Intervals

If you set an interval that references an object, and forget to clear it, the object remains reachable.

```javascript
class TimerExample {
  start() {
    this.interval = setInterval(() => {
      console.log(this.data);
    }, 1000);
  }
  stop() {
    clearInterval(this.interval);
  }
}
```

If `stop()` is never called, the instance stays alive because the interval callback references `this`.

### 2.5 Detached DOM Nodes

If you hold a JavaScript reference to a DOM node that has been removed from the document, the node may still be kept in memory, along with its children.

---

## 3. WeakMap and WeakSet

`WeakMap` and `WeakSet` are collections that hold **weak references** to their keys. This means that if an object is used as a key and there are no other strong references to it, it can be garbage‑collected, and the entry is automatically removed from the weak collection.

They are useful for storing metadata about objects without preventing their collection.

```javascript
const cache = new WeakMap();

function process(obj) {
  if (!cache.has(obj)) {
    const result = expensiveComputation(obj);
    cache.set(obj, result);
  }
  return cache.get(obj);
}
```

Here, once `obj` becomes unreachable elsewhere, the cache entry does not keep it alive.

---

## 4. Debugging Memory Leaks in Production

### 4.1 Using Browser DevTools

- **Performance tab**: Record a session, look for memory usage that does not drop after actions.
- **Memory tab**: Take heap snapshots before and after an action. Compare to see which objects remain. Look for objects that should have been collected but are still retained.
- **Allocation instrumentation**: Record allocation timeline to see where memory is allocated.

### 4.2 Common Investigation Steps

1. Reproduce the leak in a clean environment.
2. Take a heap snapshot.
3. Perform the action suspected of causing the leak.
4. Take another snapshot.
5. Compare snapshots, focusing on objects that grew in count.
6. Examine retaining paths of those objects – they show why the object is still reachable.
7. Look for patterns: detached DOM trees, large arrays, event listeners, closures.

### 4.3 Automated Leak Detection

- Use Chrome's `--enable-precise-memory-info` and performance APIs to monitor memory.
- Write tests that perform repeated actions and check that memory returns to baseline.

---

## 5. Interview Questions and Answers

### Beginner

**Q1: Does JavaScript have automatic memory management?**  
**A:** Yes. JavaScript engines automatically allocate memory when objects are created and use garbage collection to reclaim memory when objects are no longer reachable.

**Q2: What is garbage collection?**  
**A:** Garbage collection is the process of automatically freeing memory occupied by objects that are no longer reachable from the roots (global objects, stack, etc.).

**Q3: What is a memory leak?**  
**A:** A memory leak occurs when an object that is no longer needed remains reachable, so it cannot be garbage‑collected, causing memory usage to grow unnecessarily over time.

### Intermediate

**Q4: How can closures contribute to memory leaks?**  
**A:** Closures retain references to their outer lexical environment. If a closure is held alive (e.g., stored globally), it keeps all variables from its outer scope alive, even if those variables are not used. If those variables include large objects, memory usage increases.

**Q5: Why can event listeners leak memory?**  
**A:** If an event listener is attached to a DOM element, the listener function references any variables from its outer scope. Even if the element is removed from the DOM, if the listener is not removed, the element (and the closure) may remain reachable through the event system, preventing GC.

**Q6: When would `WeakMap` be useful?**  
**A:** `WeakMap` is useful when you need to associate metadata with an object without preventing that object from being garbage‑collected. For example, caching results of a computation for objects that may be discarded, or storing private data for DOM elements that will be removed.

### Senior

**Q7: Explain "reachability" as the basis of garbage collection.**  
**A:** Reachability is the core concept: an object is considered alive if it can be reached by traversing references starting from a set of root objects (global object, stack variables, active timers, etc.). The GC periodically walks this graph, marks all reachable objects, and frees those not marked. This is why unintentional references (e.g., forgotten listeners) keep objects alive – they make the object reachable even though the application no longer needs it.

**Q8: In a front‑end app, what patterns most commonly retain memory accidentally?**  
**A:** The most common patterns are:

- Detached DOM nodes (references held in JavaScript after removal).
- Event listeners not unsubscribed (especially in single‑page apps where components are destroyed).
- Closures that capture large data and are stored globally or in long‑lived caches.
- Timers or intervals that are not cleared.
- Global state that accumulates (e.g., infinite caches, Redux store with no size limit).
- Observables and subscriptions not unsubscribed.

**Q9: How would you investigate a suspected memory leak in production?**  
**A:** I would use Chrome DevTools' Performance and Memory panels. First, I'd record a timeline of memory usage while reproducing the leaky scenario. Then I'd take heap snapshots before and after the scenario, and compare them to identify objects that persist. I'd examine their retaining paths to see what keeps them alive. I'd also look for patterns like detached DOM trees or event listeners. In production, I might use the `performance.memory` API (if available) to monitor heap size over time and trigger alerts. Additionally, I'd use tools like Lighthouse to audit for memory issues.

---

## 6. Summary

- JavaScript uses automatic garbage collection based on reachability.
- Memory leaks happen when objects remain reachable unintentionally.
- Common culprits: global variables, forgotten listeners, closures, timers.
- `WeakMap` and `WeakSet` help manage object‑associated data without preventing GC.
- Debugging leaks involves heap snapshots and analysing retaining paths.
- Proactive design (unsubscribe, clear timers, avoid global accumulation) prevents most leaks.

---

## Practice

- [practice/12-memory-leak-patterns.js](practice/12-memory-leak-patterns.js) — Safe demos: reachability, WeakMap cache, and commented leak patterns. Run with `node practice/12-memory-leak-patterns.js`.

See also [05-memory-model.md](05-memory-model.md) for stack/heap and closure retention basics.
