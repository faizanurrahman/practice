/**
 * Prototype chain: Object.create, constructor, class.
 * Concept: ../06-prototypes-and-inheritance.md
 * Run: node practice/06-prototype-chain.js
 */

// --- 1. Object.create: set [[Prototype]] directly ---
const proto = { greet() { return "Hello from proto"; } };
const child = Object.create(proto);
child.name = "Child";
console.log(child.name);           // "Child"
console.log(child.greet());        // "Hello from proto"
console.log(Object.getPrototypeOf(child) === proto); // true

// --- 2. Constructor and .prototype ---
function Person(name) {
  this.name = name;
}
Person.prototype.sayName = function () {
  return this.name;
};
const p = new Person("Alice");
console.log(p.sayName());          // "Alice"
console.log(Object.getPrototypeOf(p) === Person.prototype); // true

// --- 3. class extends (syntactic sugar over prototype) ---
class Animal {
  constructor(name) {
    this.name = name;
  }
  speak() {
    return this.name + " makes a sound";
  }
}
class Dog extends Animal {
  speak() {
    return this.name + " barks";
  }
}
const d = new Dog("Rex");
console.log(d.speak());            // "Rex barks"
console.log(d instanceof Dog);     // true
console.log(d instanceof Animal); // true
console.log(Object.getPrototypeOf(Dog.prototype) === Animal.prototype); // true
