/**
 * Equality and typeof gotchas.
 * See: 04-types-equality-and-this.md
 */

console.log("=== == vs === (coercion) ===");
console.log("1 == '1':", 1 == "1");   // true
console.log("1 === '1':", 1 === "1"); // false
console.log("0 == false:", 0 == false); // true
console.log("null == undefined:", null == undefined); // true

console.log("\n=== typeof ===");
console.log("typeof null:", typeof null);       // "object" (historic bug)
console.log("typeof undefined:", typeof undefined);
console.log("typeof []:", typeof []);           // "object"
console.log("typeof function () {}:", typeof function () {}); // "function"

console.log("\n=== NaN ===");
console.log("NaN === NaN:", NaN === NaN);     // false
console.log("Number.isNaN(NaN):", Number.isNaN(NaN)); // true
