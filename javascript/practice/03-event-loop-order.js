/**
 * Event loop: predict order of console.log (sync vs microtask vs macrotask).
 * See: 03-event-loop-and-async.md
 *
 * Order: sync → all microtasks → one macrotask. Run and verify.
 */

console.log("1 (sync)");

setTimeout(() => console.log("2 (setTimeout — macrotask)"), 0);

Promise.resolve().then(() => console.log("3 (Promise.then — microtask)"));

queueMicrotask(() => console.log("4 (queueMicrotask — microtask)"));

console.log("5 (sync)");

// Expected order: 1, 5, 3, 4, 2
// (sync first, then all microtasks, then the setTimeout callback)
