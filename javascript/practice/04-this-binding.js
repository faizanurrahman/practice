/**
 * this binding: default, implicit, explicit, new, arrow.
 * See: 04-types-equality-and-this.md
 */

console.log("=== 1. Default (strict mode: undefined) ===");
function defaultThis() {
  "use strict";
  console.log("defaultThis:", this);
}
defaultThis(); // undefined in strict

console.log("\n=== 2. Implicit (method call) ===");
const obj = {
  name: "ObjectName",
  say() {
    console.log("say this:", this?.name);
  },
  hello: function () {
    console.log("hello this:", this?.name);
  },
  bye: () => {
    console.log("bye this:", this?.name);
  },
  world() {
    console.log("world this:", this?.name);
    let arrow = () => {
      console.log("arrow this:", this?.name);
    };

    return arrow;
  },
};
const arrow = obj.world();
arrow(); // ObjectName
obj.say(); // Obj
obj.bye(); // undefined
// obj.world(); // ObjectName, ObjectName, ObjectName
obj.hello(); // ObjectName

console.log("\n=== 3. Explicit (call) ===");
function greet() {
  console.log("greet this:", this?.name);
}
greet.call({ name: "Explicit" }); // Explicit

console.log("\n=== 4. new (constructor) ===");
function Person(name) {
  this.name = name;
}
const p = new Person("New");
console.log("new Person:", p.name);

console.log("\n=== 5. Arrow (lexical this) ===");
const outer = {
  name: "Outer",
  run() {
    const arrow = () => console.log("arrow this:", this?.name);
    arrow();
  },
};
outer.run(); // Outer (arrow inherits run's this)
