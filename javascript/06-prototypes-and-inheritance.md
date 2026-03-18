# 09 — Prototype Chain and Inheritance

## Learning Objectives

- Understand that JavaScript uses **prototypal inheritance**, not classical inheritance.
- Explain the **prototype chain**: objects inherit properties from other objects.
- Differentiate between **own properties** and **inherited properties**.
- Master the `constructor`/`prototype` pattern and how `new` works.
- Recognise that ES6 `class` syntax is syntactic sugar over prototypes.
- Analyse performance considerations: prototype lookup cost.
- Debug issues like property shadowing and unintended inheritance.

---

## 1. What Is the Prototype Chain?

Every object in JavaScript has an internal link to another object called its **prototype**. When you access a property on an object, the engine first looks for it on the object itself; if not found, it follows the prototype link, and continues up the chain until either the property is found or the end of the chain (`null`) is reached.

This chain of objects is the **prototype chain**.

```javascript
const animal = {
  eats: true
};

const dog = Object.create(animal);
dog.barks = true;

console.log(dog.barks); // true (own property)
console.log(dog.eats);  // true (inherited from animal)
console.log(dog.toString); // inherited from Object.prototype
```

`Object.create(animal)` sets `dog`'s prototype to `animal`.

---

## 2. Constructor Functions and the `prototype` Property

Before ES6 classes, constructor functions were used to create objects with shared methods.

```javascript
function User(name) {
  this.name = name; // own property
}

User.prototype.sayHi = function() {
  console.log(`Hi, ${this.name}`);
};

const user1 = new User('Ali');
const user2 = new User('Sara');

user1.sayHi(); // "Hi, Ali"
user2.sayHi(); // "Hi, Sara"
```

- Each function has a `prototype` property (an object).
- When you call `new User()`, the new object's prototype is set to `User.prototype`.
- Methods defined on `User.prototype` are shared by all instances – they are not copied to each instance, saving memory.

---

## 3. Property Lookup and Shadowing

Property lookup follows the chain:

1. Check object's own properties.
2. If not found, check its prototype.
3. Continue until found or `null`.

If you assign a property with the same name as an inherited one, you create an **own property** that **shadows** (overrides) the inherited one.

```javascript
function User(name) { this.name = name; }
User.prototype.sayHi = function() { console.log('Hi'); };

const user = new User('Ali');
user.sayHi = function() { console.log('Hello'); };

user.sayHi(); // "Hello" – own property shadows prototype method
```

---

## 4. ES6 Classes – Syntactic Sugar

The `class` syntax provides a cleaner way to define constructors and prototypes.

```javascript
class User {
  constructor(name) {
    this.name = name;
  }

  sayHi() {
    console.log(`Hi, ${this.name}`);
  }
}
```

Under the hood, it still uses prototypes. The `sayHi` method is placed on `User.prototype`. Classes are just a more convenient syntax for the constructor/prototype pattern.

**Important:** JavaScript remains a prototype‑based language; classes do not introduce classical inheritance.

---

## 5. Prototype Chain Lookup Cost

Each property lookup involves walking the prototype chain. In practice, this cost is negligible because engines optimise heavily (inline caching). However, deeply nested chains (e.g., many levels of inheritance) can slow down lookups. It's generally better to keep inheritance hierarchies shallow.

---

## 6. Debugging Inheritance Issues

### 6.1 Checking the Prototype

Use `Object.getPrototypeOf(obj)` or the deprecated `obj.__proto__` (not recommended). Also, `obj instanceof Constructor` checks if `Constructor.prototype` appears in `obj`'s prototype chain.

### 6.2 Shadowing Problem

If you expect an inherited method but get a different result, check if the object has its own property with the same name. Use `obj.hasOwnProperty('prop')`.

### 6.3 Forgetting `new`

Calling a constructor without `new` pollutes the global object. Use strict mode or linting rules to prevent this.

---

## 7. Interview Questions and Answers

### Beginner

**Q1: What is a prototype?**  
**A:** A prototype is an internal object from which another object inherits properties. Every JavaScript object has a prototype (except the root object).

**Q2: What is prototype inheritance?**  
**A:** Prototype inheritance means that an object can access properties defined on its prototype (and that prototype's prototype, etc.), forming a chain. This allows code reuse and sharing of methods.

**Q3: Where does a method defined on prototype live?**  
**A:** The method lives on the prototype object itself, not on each instance. All instances share the same function via the prototype chain.

### Intermediate

**Q4: Why is putting methods on prototype more memory‑efficient than defining them in constructor?**  
**A:** If you define methods inside the constructor (using `this.method = ...`), each instance gets its own copy of the function, consuming more memory. Methods on the prototype are shared by all instances, so only one copy exists.

**Q5: How does property lookup work?**  
**A:** First, the engine checks the object's own properties. If not found, it follows the prototype link and checks that object's own properties, repeating until either the property is found or the end of the chain (null) is reached.

**Q6: Are JavaScript classes truly class‑based internally?**  
**A:** No. ES6 classes are syntactic sugar over the existing prototype‑based inheritance. They still use prototypes under the hood; they just provide a more familiar syntax.

### Senior

**Q7: Explain prototype lookup cost and practical relevance.**  
**A:** Each property lookup involves potentially walking the prototype chain. Modern JavaScript engines use hidden classes and inline caching to make this extremely fast. In practice, for well‑structured code (shallow chains, predictable property access), the cost is negligible. However, extremely deep chains or dynamic property additions can de‑optimise. This is rarely a bottleneck; readability and maintainability are more important.

**Q8: Why does runtime understanding of prototypes matter even when using class syntax?**  
**A:** Because classes are just syntax; the underlying mechanism is still prototypal. When debugging, you may see `__proto__` in the console; understanding prototypes helps you interpret what you see. Also, advanced patterns like mixins, monkey‑patching, and `Object.create` are still used and rely on prototypes. Knowing the foundation helps you reason about edge cases (e.g., `super` resolution, `instanceof` behaviour).

**Q9: How would you debug an inherited property shadowing issue?**  
**A:** I would use `console.log(obj)` to inspect the object in DevTools, expand its `__proto__` to see the chain. Then use `obj.hasOwnProperty('prop')` to check if the property is own. If the expected value differs, I'd look for assignments to that property on the object. If it's a method, I'd verify that the prototype method hasn't been accidentally overwritten. I might also use `Object.getPrototypeOf(obj).methodName` to access the inherited method directly.

---

## 8. Summary

- JavaScript uses **prototypal inheritance** – objects inherit from other objects.
- The **prototype chain** is walked when accessing properties.
- Constructor functions have a `prototype` property that becomes the prototype of instances created with `new`.
- ES6 classes are syntactic sugar over prototypes.
- Property shadowing occurs when an own property has the same name as an inherited one.
- Understanding prototypes is essential for debugging and writing efficient, maintainable code.

---

## Practice

- [practice/04-this-binding.js](practice/04-this-binding.js) — Demos for default, implicit, explicit, `new`, arrow. Run and explain each.
- [practice/06-prototype-chain.js](practice/06-prototype-chain.js) — `Object.create`, constructor, `class` extend. Run and trace the prototype chain.
