# 08 — Objects by Reference, Mutation, and Copying

## Learning Objectives

- Understand the fundamental difference between **primitive values** (copied by value) and **objects/arrays** (passed by reference).
- Recognise how **shared references** lead to accidental mutation bugs.
- Distinguish **shallow copying** (e.g., spread, `Object.assign`) from **deep copying** (e.g., `JSON.parse/JSON.stringify`, structured cloning).
- Analyse how mutation bugs manifest in UI state and why they are so common.
- Explain why **reference stability** matters in rendering systems like Angular's change detection.
- Design state update flows that avoid hidden mutation (immutable updates).

---

## 1. Core Rule: Value vs Reference

JavaScript has two categories of data types:

- **Primitives**: `string`, `number`, `boolean`, `null`, `undefined`, `symbol`, `bigint`. They are stored and assigned **by value**.
- **Objects** (including arrays, functions, dates, etc.): stored and assigned **by reference**.

When you assign a primitive variable to another, a copy of the actual value is made. When you assign an object variable to another, both variables point to the **same underlying object** in memory.

```javascript
let a = 10;
let b = a;    // value copied
b = 20;
console.log(a); // 10 (unchanged)

const objA = { count: 1 };
const objB = objA; // reference copied
objB.count = 2;
console.log(objA.count); // 2 – both refer to the same object
```

This behaviour is not a bug – it's how JavaScript works. But it becomes a frequent source of bugs when we forget that objects are shared.

---

## 2. Mutation Bugs in UI State

In front‑end applications, state is often represented by objects (e.g., component state, store state). Accidental mutation can cause:

- UI not updating because change detection didn't see a new reference.
- Unexpected side effects where one part of the app modifies state that another part relies on.
- Hard‑to‑trace bugs that manifest only under certain interaction sequences.

**Example:**

```javascript
// Component state
this.user = { name: 'Ali', address: { city: 'Istanbul' } };

// Somewhere else, a helper function modifies the address
function updateCity(user, newCity) {
  user.address.city = newCity; // mutation!
  return user;
}

// Later in the component
this.user = updateCity(this.user, 'Ankara');
// this.user.address.city is now 'Ankara' – but did we intend to mutate the original?
```

If the `updateCity` function had instead returned a **new object**, the original would remain unchanged, and any change detection relying on reference comparison would work correctly.

---

## 3. Shallow Copy vs Deep Copy

### 3.1 Shallow Copy

A shallow copy creates a new object, but copies only the top‑level properties. If a property is itself an object, the copy still shares a reference to that nested object.

Methods:

- Spread `{ ...original }`
- `Object.assign({}, original)`
- `Array.slice()` for arrays

```javascript
const original = { user: { name: 'Ali' } };
const copy = { ...original };
copy.user.name = 'Sara';
console.log(original.user.name); // 'Sara' – nested object was shared
```

### 3.2 Deep Copy

A deep copy creates a new object and recursively copies all nested objects, so no references are shared.

Methods:

- `JSON.parse(JSON.stringify(original))` – works for serializable data, but loses functions, `undefined`, `Symbol`, and cannot handle circular references.
- `structuredClone(original)` – modern browser API, handles more types, still no functions.
- Manual recursive copy or using libraries like Lodash's `_.cloneDeep`.

```javascript
const original = { user: { name: 'Ali' } };
const deepCopy = JSON.parse(JSON.stringify(original));
deepCopy.user.name = 'Sara';
console.log(original.user.name); // 'Ali' – untouched
```

**Trade‑offs:** Deep copying is expensive for large objects; often a better design is to avoid deep mutation by structuring state immutably (e.g., using nested updates that produce new objects only where needed).

---

## 4. Reference Stability in Rendering Systems

Angular's change detection uses **reference checks** for `@Input()` bindings when using `OnPush` strategy. If you pass an object to a child component and then mutate it, the reference does not change, so the child may not detect the change.

```typescript
// Parent component
this.data = { value: 1 };

update() {
  this.data.value = 2; // mutation – same object reference
}
```

If the child uses `OnPush`, it will not see this change because the input reference (`data`) hasn't changed. The fix is to create a new object:

```typescript
update() {
  this.data = { ...this.data, value: 2 }; // new reference
}
```

This is why **immutable updates** are essential when using `OnPush` or when working with state management libraries like NgRx (which rely on reference equality to detect changes).

**Senior insight:** Most "change detection not working" problems are not Angular bugs – they are **reference mutation bugs**.

---

## 5. Designing Mutation‑Safe State Updates

### 5.1 Immutable Update Patterns

- Use spread syntax for shallow updates:
  ```javascript
  const newState = { ...oldState, updatedProp: newValue };
  ```
- For nested updates, apply spread at each level:
  ```javascript
  const newState = {
    ...oldState,
    user: {
      ...oldState.user,
      name: newName
    }
  };
  ```
- For arrays, use non‑mutating methods: `map`, `filter`, `concat`, `...` spread.
- Consider using libraries like Immer that let you write mutable‑looking code but produce immutable updates.

### 5.2 Freezing Objects in Development

`Object.freeze()` can be used in development to detect accidental mutations. Many state libraries freeze the state in non‑production builds.

```javascript
const state = Object.freeze({ user: { name: 'Ali' } });
state.user.name = 'Sara'; // in strict mode, this fails silently or throws
```

### 5.3 Avoid Mutating Function Arguments

Functions that receive objects should treat them as read‑only unless explicitly documented as mutators. Instead, return a new object.

**Bad:**
```javascript
function addItem(cart, item) {
  cart.push(item); // mutation
}
```

**Good:**
```javascript
function addItem(cart, item) {
  return [...cart, item];
}
```

---

## 6. Interview Questions and Answers

### Beginner

**Q1: What is the difference between primitive and reference values?**  
**A:** Primitive values (strings, numbers, booleans, etc.) are stored directly in the variable; when assigned to another variable, the actual value is copied. Reference values (objects, arrays, functions) are stored as references to a location in memory; when assigned, both variables point to the same object, so mutations through one affect the other.

**Q2: Why does changing `b.count` affect `a.count` in the example?**  
**A:** Because `a` and `b` both reference the same object. Changing a property through `b` modifies the shared object, and `a` sees that change because it points to the same object.

**Q3: What does spread syntax copy?**  
**A:** Spread syntax copies only the top‑level properties of an object or array. If any property is itself an object, that nested object is still shared (shallow copy).

### Intermediate

**Q4: What is shallow copy vs deep copy?**  
**A:** A shallow copy creates a new object with copies of the top‑level properties; nested objects are still referenced. A deep copy recursively copies all nested objects, creating an entirely independent clone.

**Q5: Why do nested mutations still affect original objects after spread?**  
**A:** Because spread only copies the references to nested objects, not the objects themselves. Both the original and the copy point to the same nested object, so mutating that nested object affects both.

**Q6: How can accidental mutation create bugs in UI state?**  
**A:** UI frameworks often rely on reference equality to detect changes. If you mutate an object passed as input, the reference doesn't change, so change detection may not run. Also, if multiple parts of the app share the same object, one component's mutation can unexpectedly change another component's state, leading to inconsistent UI.

### Senior

**Q7: Why is reference stability important in rendering systems?**  
**A:** Rendering systems optimise by checking whether inputs have changed. If they compare by reference (e.g., Angular's `OnPush`), a new reference triggers an update, while mutating an existing reference does not. Stable references (i.e., the same object) signal "no change," avoiding unnecessary re‑renders. But if you mutate without changing the reference, you lose the ability to signal changes, leading to stale UI.

**Q8: How would you design a state update flow to avoid hidden mutation?**  
**A:** I would enforce immutability at the architectural level: use a state management library like NgRx that encourages reducers (pure functions returning new state); in services or components, always treat state as read‑only and produce new objects via spread or library helpers; use linters to forbid mutation (e.g., `no-param-reassign`); and freeze state in development to catch mutations early.

**Q9: How do mutation bugs interact with memoization and change detection?**  
**A:** Memoization (e.g., in selectors or pure pipes) caches results based on input references. If you mutate an input object but keep the same reference, the memoized function may return a stale cached value because it thinks nothing changed. Change detection similarly may skip updates. Mutation thus breaks both performance optimisations and correctness.

---

## 7. Summary

- Primitives are copied by value; objects are copied by reference.
- Shared references cause unintended mutations unless we are careful.
- Shallow copying protects the top level only; deep copying fully isolates.
- In UI frameworks, reference stability is key for change detection and performance.
- Immutable update patterns prevent mutation bugs and make state predictable.

---

## Practice

- [practice/09-objects-reference-mutation.js](practice/09-objects-reference-mutation.js) — Value vs reference, shallow vs deep copy, immutable update patterns. Run with `node practice/09-objects-reference-mutation.js`.

See also [05-memory-model.md](05-memory-model.md) for stack/heap and reference basics.
