/**
 * Minimal Redux-style store (vanilla JS).
 * Concept: ../02-rxjs-ngrx-performance.md
 *
 * Run: node store-minimal.js
 */

function createStore(reducer, initialState) {
  let state = initialState;
  const listeners = [];

  function getState() {
    return state;
  }

  function dispatch(action) {
    state = reducer(state, action);
    listeners.forEach((fn) => fn());
  }

  function subscribe(fn) {
    listeners.push(fn);
    return () => {
      const i = listeners.indexOf(fn);
      if (i >= 0) listeners.splice(i, 1);
    };
  }

  return { getState, dispatch, subscribe };
}

// Reducer: pure (state, action) => newState
function counterReducer(state = { count: 0 }, action) {
  switch (action.type) {
    case "INCREMENT":
      return { count: state.count + (action.payload ?? 1) };
    case "DECREMENT":
      return { count: state.count - 1 };
    default:
      return state;
  }
}

const store = createStore(counterReducer, { count: 0 });

store.subscribe(() => console.log("state:", store.getState()));

store.dispatch({ type: "INCREMENT" });   // state: { count: 1 }
store.dispatch({ type: "INCREMENT", payload: 2 }); // state: { count: 3 }
store.dispatch({ type: "DECREMENT" });    // state: { count: 2 }
