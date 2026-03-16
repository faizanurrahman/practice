/**
 * RxJS Subjects: Subject, BehaviorSubject, ReplaySubject.
 * Concept: ../02-rxjs-deep-dive.md (and 02-rxjs-ngrx-performance.md)
 * Run: node subjects-demo.js  (requires: npm install rxjs in this folder)
 */

const { Subject, BehaviorSubject, ReplaySubject } = require("rxjs");

console.log("=== Subject (no initial, multicast) ===");
const subj = new Subject();
subj.subscribe((v) => console.log("  A:", v));
subj.subscribe((v) => console.log("  B:", v));
subj.next(1);
subj.next(2);
// A:1 B:1  A:2 B:2

console.log("\n=== BehaviorSubject (has current value) ===");
const behavior = new BehaviorSubject(0);
behavior.subscribe((v) => console.log("  first sub:", v)); // 0 immediately
behavior.next(1);
behavior.subscribe((v) => console.log("  second sub:", v)); // 1 immediately
behavior.next(2);
// first sub: 0, first sub: 1, second sub: 1, first sub: 2, second sub: 2

console.log("\n=== ReplaySubject(2) (replay last 2 to new subs) ===");
const replay = new ReplaySubject(2);
replay.next(10);
replay.next(20);
replay.next(30);
replay.subscribe((v) => console.log("  late sub:", v));
// late sub: 20, late sub: 30
