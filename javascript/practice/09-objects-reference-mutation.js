/**
 * Objects by reference, mutation, and copying.
 * Concept: ../09-objects-reference-mutation-copying.md
 * Run: node practice/09-objects-reference-mutation.js
 */

// --- 1. Value vs reference (Section 1) ---
console.log("--- 1. Value vs reference ---");
let a = 10;
let b = a;
b = 20;
console.log("a:", a); // 10

const objA = { count: 1 };
const objB = objA;
objB.count = 2;
console.log("objA.count:", objA.count); // 2 – same object

// --- 2. Shallow copy: spread and Object.assign (Section 3.1) ---
console.log("\n--- 2. Shallow copy ---");
const original = { user: { name: "Ali" } };
const shallow = { ...original };
shallow.user.name = "Sara";
console.log("original.user.name after shallow copy mutation:", original.user.name); // 'Sara'

// --- 3. Deep copy: JSON (Section 3.2) ---
console.log("\n--- 3. Deep copy (JSON) ---");
const orig = { user: { name: "Ali" } };
const deep = JSON.parse(JSON.stringify(orig));
deep.user.name = "Sara";
console.log("orig.user.name after deep copy mutation:", orig.user.name); // 'Ali'

// --- 4. Deep copy: structuredClone (Node 17+ / modern browsers) ---
if (typeof structuredClone === "function") {
  const o = { nested: { x: 1 } };
  const cloned = structuredClone(o);
  cloned.nested.x = 2;
  console.log("o.nested.x after structuredClone:", o.nested.x); // 1
}

// --- 5. Immutable update patterns (Section 5.1) ---
console.log("\n--- 5. Immutable updates ---");
const oldState = { count: 0, user: { name: "Ali" } };
const newState = {
  ...oldState,
  count: 1,
  user: {
    ...oldState.user,
    name: "Sara",
  },
};
console.log("oldState unchanged:", oldState.count, oldState.user.name);
console.log("newState:", newState.count, newState.user.name);

// Array: non-mutating
const cart = [1, 2, 3];
const newCart = [...cart, 4];
console.log("cart:", cart, "newCart:", newCart);

// --- 6. Pure function: return new object (Section 5.3) ---
function addItem(cart, item) {
  return [...cart, item];
}
const items = ["a"];
const updated = addItem(items, "b");
console.log("items unchanged:", items, "updated:", updated);
