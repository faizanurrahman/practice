# Angular Practice — How to run

Each subfolder aligns with a concept module and contains runnable or copy-paste code.

| Folder | Concept | How to run |
|--------|---------|------------|
| **01-signals-vs-zone** | Angular Core & Ivy | Copy snippets into a new Angular app; `ng serve`. See folder README. |
| **02-rxjs-operators** | RxJS, NgRx, Performance | `npm install rxjs` then `node operators-demo.js`. |
| **02-ngrx-minimal** | NgRx | `node store-minimal.js` (vanilla Redux-style store). For Angular: `ng add @ngrx/store` and use actions/reducer/selectors. |
| **02-trackby-virtual** | Performance | Copy trackBy and optional virtual scroll snippet into your Angular component; see folder README. |
| **03-component-harness** | Testing | Copy component + harness + spec into an Angular project; `ng test`. |
| **03-a11y-focus-trap** | Accessibility | Copy modal template and focus logic into your app; use `cdkTrapFocus` and ARIA. |

For full Angular apps (01, 03), use an existing project or create one with `ng new`.
