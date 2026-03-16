# Angular workspace (Phase 0)

One Angular app lives here. Create it with the CLI so you have a real workspace for the pyramid (Phase 3+).

## Setup (once)

From this folder (`workspace/angular-workspace/`):

```bash
ng new angular-app --standalone --routing --style=css
cd angular-app
ng serve
```

Then use `angular-app/` as your Angular playground: add components, try change detection, inject services, use RxJS, etc.

## Study rule (every topic)

1. Define in one line → [../mental-models.md](../mental-models.md)  
2. Flow in plain words → [../mental-models.md](../mental-models.md)  
3. Run one tiny example → in this app  
4. Break it intentionally → change code, see error or wrong behavior  
5. Explain why it broke → [../debug-log.md](../debug-log.md)
