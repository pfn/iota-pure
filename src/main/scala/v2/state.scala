package iota.pure.v2

private[v2] trait PureState[T,S] {
  val state: S
  val zero: T
  /** ignore the result, do not update state */
  def apply[A](a: A): (T,S) = zero -> state
  /** ignore the result, update state */
  def applyState(s: S): (T,S) = zero -> s
  /** return the result, do not update state */
  def applyResult(t: T): (T,S) = t -> state
}
