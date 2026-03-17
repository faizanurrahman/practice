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

let defaultThis2 = function () {
  "use strict";
  console.log("defaultThis2:", this);
};

defaultThis2(); // undefined in strict

console.log("\n=== 2. Implicit (method call) ===");
const obj = {
  name: "ObjectName",
  say() {
    console.log("say this:", this?.name);
  },
};

obj.say(); // ObjectName

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

let arrow = (key) => {
  console.log("arrow this default:", this?.name, "key: ", key);
};
arrow.bind("key")(); // undefined

const outer = {
  name: "Outer",
  run() {
    const arrow = () => console.log("arrow this:", this?.name);
    arrow();
  },
};
outer.run(); // Outer (arrow inherits run's this)

function Test() {
  let arrow = () => {
    console.log("arrow this:", this?.name);
  };
  return arrow;
}
const test = Test.bind({ name: "Faizan" });
test()(); // undefined

// Call, Apply, Bind
function greet(greeting, message) {
  console.log(greeting + " " + this?.name + ", " + message);
}

console.log("=== 6. Call, Apply, Bind ===");

console.log("Call: ");
greet.call({ name: "Faizan" }, "Hello", "How are you?"); // Hello Faizan, How are you?

console.log("Apply: ");
greet.apply({ name: "Faizan" }, ["Hello", "How are you?"]); // Hello Faizan, How are you?

console.log("Bind: ");
const greetFn = greet.bind({ name: "Faizan" });
greetFn("Hello", "How are you?"); // Hello Faizan, How are you?

const greetFn2 = greetFn.bind({ name: "Sushil" }, "Hi!");
greetFn2("Hey How are you?"); // Hi! Sushil, Hey How are you?
