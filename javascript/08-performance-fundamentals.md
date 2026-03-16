# 08 — Performance Fundamentals

## Learning objectives

- Explain **event delegation** and why it can save memory and improve performance.
- Use **debouncing** and **throttling** to control execution frequency.
- Describe what triggers **DOM reflow** and **repaint** and how to minimise them.

---

## Event delegation

- **Idea:** Attach one listener to a **parent** element instead of many listeners on each child. Use `event.target` (or currentTarget) to identify which child received the event.
- **Why it helps:** Fewer DOM nodes with listeners → less memory and less work when the DOM changes. Especially useful for long lists (e.g. tables, menus) where adding/removing per-item listeners is costly.
- **Caveat:** Some events do not bubble (e.g. focus/blur in some browsers); use capture phase if needed. Event delegation is a pattern, not a silver bullet.

---

## Debouncing and throttling

- **Debounce:** Run the handler only after the event (e.g. input, resize) has **stopped** for a given delay. Each new event resets the timer. Use for: search-as-you-type (wait for user to stop typing), window resize (wait until resize ends).
- **Throttle:** Run the handler at most once per time interval (e.g. every 100 ms). Use for: scroll handlers, mousemove (limit how often you run). Ensures a steady maximum rate instead of firing on every event.
- **Performance implication:** Without debounce/throttle, rapid events (typing, scroll) can fire hundreds of times per second and block the main thread or cause layout thrash.

---

## DOM reflow and repaint

- **Repaint:** The browser redraws pixels (e.g. color, visibility). No geometry change.
- **Reflow (layout):** The browser recalculates layout (positions, dimensions). Expensive; can be triggered by reading layout properties (offsetWidth, getBoundingClientRect, etc.) after DOM or style changes, or by changing styles that affect layout (width, height, margin, etc.).
- **Minimise:** Batch DOM reads and writes (read all layout properties first, then apply all changes); avoid forcing synchronous layout in loops; use CSS transforms/opacity for animations when possible (compositor-only, no reflow); reduce DOM depth and number of elements that need layout.

---

## Interview focus

- "What is event delegation?" — One listener on a parent; use event.target to handle child events; saves memory and setup cost.
- "When do you use debounce vs throttle?" — Debounce: after activity stops (e.g. search). Throttle: at most every N ms (e.g. scroll).
- "What triggers reflow?" — Reading layout (offsetWidth, getBoundingClientRect) after changes; changing layout-affecting styles; adding/removing DOM nodes.

---

## Practice

- [practice/08-debounce-throttle.js](practice/08-debounce-throttle.js) — Implement or use debounce and throttle; run and observe timing. Run with `node practice/08-debounce-throttle.js`.
- Optional: [practice/08-event-delegation.html](practice/08-event-delegation.html) or a short .js snippet for event delegation (one listener on a parent, log event.target). Omitted here; you can add a minimal HTML file or describe in README.
