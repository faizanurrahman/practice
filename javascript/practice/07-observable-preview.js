/**
 * Observable preview: lazy execution, subscribe, unsubscribe.
 * Concept: ../07-async-patterns.md
 * Run: node practice/07-observable-preview.js
 *
 * This uses a minimal Observable-like API (no RxJS required).
 * For full RxJS (operators, Subject), see angular/practice/02-rxjs-operators/.
 */

function simpleObservable(produce) {
  return {
    subscribe(observer) {
      let cancelled = false;
      const sub = {
        unsubscribe() {
          cancelled = true;
        },
      };
      try {
        produce({
          next(v) {
            if (!cancelled && observer.next) observer.next(v);
          },
          complete() {
            if (!cancelled && observer.complete) observer.complete();
          },
        });
      } catch (e) {
        if (!cancelled && observer.error) observer.error(e);
      }
      return sub;
    },
  };
}

// Lazy: nothing runs until subscribe
const obs = simpleObservable((subscriber) => {
  console.log("  (producer runs when you subscribe)");
  subscriber.next(1);
  subscriber.next(2);
  subscriber.next(3);
  subscriber.complete();
});

console.log("Before subscribe");
const sub = obs.subscribe({
  next: (v) => console.log("  next:", v),
  complete: () => console.log("  complete"),
});
console.log("After subscribe (values above came from the stream)");
sub.unsubscribe();
console.log("Unsubscribed");
