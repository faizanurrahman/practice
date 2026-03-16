# Phase 0 — Setup and Study System

**Time:** 2 days  
**Goal:** Build the environment and the method you'll use for every topic.

## What’s in this folder

| Item | Purpose |
|------|--------|
| **angular-workspace/** | One Angular app — create with `ng new` (see that folder’s README). |
| **js-playground/** | Plain JavaScript — run with Node; no build. |
| **ts-playground/** | TypeScript — run with `ts-node` or `npx tsx`; has its own `tsconfig.json`. |
| **mental-models.md** | One-line definitions and flow-in-words for each topic. |
| **debug-log.md** | “Break it, then explain why” notes for each topic. |

## Your rule for every topic

For each concept you study:

1. **Write the definition in one line** — in [mental-models.md](mental-models.md).
2. **Draw the flow in plain words** — step-by-step in [mental-models.md](mental-models.md).
3. **Run one tiny example** — in the right playground (JS, TS, or Angular).
4. **Break it intentionally** — change the code so it fails or misbehaves.
5. **Explain why it broke** — in [debug-log.md](debug-log.md).

This loop (define → flow → run → break → explain) is your default for every topic in the pyramid (JavaScript engine, RxJS, Angular internals, etc.).

## Quick start

- **JS:** `cd js-playground && node example.js`
- **TS:** `cd ts-playground && npm install && npx tsx example.ts` (or `npx ts-node example.ts`)
- **Angular:** See [angular-workspace/README.md](angular-workspace/README.md) — create the app with `ng new`, then `ng serve`.
