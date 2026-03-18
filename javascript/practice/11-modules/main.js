/**
 * ESM: named imports, default import, dynamic import.
 * Concept: ../../11-modules.md
 * Run from javascript/: node practice/11-modules/main.js
 */
import { add, subtract, PI } from "./math.js";
import myLog from "./logger.js";

console.log("Named imports:", add(1, 2), subtract(5, 2), PI);
myLog("Default import works");

// Dynamic import (lazy load)
const { add: addDynamic } = await import("./math.js");
console.log("Dynamic import:", addDynamic(10, 20));
