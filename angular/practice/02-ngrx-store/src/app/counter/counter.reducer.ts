import { createReducer, on } from '@ngrx/store';
import { increment, decrement } from './counter.actions';

export interface CounterState {
  count: number;
}

export const initialCounterState: CounterState = {
  count: 0,
};

export const counterReducer = createReducer(
  initialCounterState,
  on(increment, (state) => ({ count: state.count + 1 })),
  on(decrement, (state) => ({ count: state.count - 1 }))
);
