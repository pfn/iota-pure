package iota.pure

import iota.effect.IO

private[pure] trait PureState[T,S] {
  val state: S
  val zero: T
  /** run an IO action, ignore the result, do not update state */
  def apply[A](io: IO[A]): IO[(T,S)] = io map (_ => zero -> state)
  /** run an IO action, ignore the result, update state */
  def applyState(io: IO[S]): IO[(T,S)] = io map (s => zero -> s)
  /** run an IO action, return the result, do not update state */
  def applyResult(io: IO[T]): IO[(T,S)]
}
