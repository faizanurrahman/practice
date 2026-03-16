import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { selectCount } from './counter/counter.selectors';
import { increment, decrement } from './counter/counter.actions';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>NgRx Counter</h1>
    <p>Count: {{ count$ | async }}</p>
    <button (click)="increment()">+</button>
    <button (click)="decrement()">-</button>
  `,
})
export class AppComponent {
  count$ = this.store.select(selectCount);

  constructor(private store: Store) {}

  increment(): void {
    this.store.dispatch(increment());
  }

  decrement(): void {
    this.store.dispatch(decrement());
  }
}
