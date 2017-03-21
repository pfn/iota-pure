package iota.pure.v2

import android.app.Activity
import android.os.Bundle
import android.view._

/** beware:
  * https://youtrack.jetbrains.com/issue/SCL-9888
  * https://issues.scala-lang.org/browse/SI-9658
  */
trait PureActivity[S] extends Activity {
  private[this] var state: S = _
  sealed trait ActivityState[T] extends PureState[T,S]
  trait ActivityStateUnit extends ActivityState[Unit] {
    val zero = ()
  }
  trait ActivityStateBoolean extends ActivityState[Boolean] {
    val zero = false
  }
  case class OnPreCreate(state: S)                     extends ActivityStateUnit
  case class OnCreate(state: S)                        extends ActivityStateUnit
  case class OnDestroy(state: S)                       extends ActivityStateUnit
  case class OnStart(state: S)                         extends ActivityStateUnit
  case class OnStop(state: S)                          extends ActivityStateUnit
  case class OnResume(state: S)                        extends ActivityStateUnit
  case class OnPause(state: S)                         extends ActivityStateUnit
  case class OnCreateOptionsMenu(state: S, menu: Menu) extends ActivityStateBoolean
  case class OnOptionsItemSelected(state: S, item: MenuItem) extends ActivityStateBoolean
  case class TransformState(state: S, oldState: S)     extends ActivityStateUnit
  case class SaveState(state: S, bundle: Bundle)       extends ActivityStateUnit

  def initialState(b: Option[Bundle]): S
  def applyState[T]: PartialFunction[ActivityState[T],(T,S)]
  def transformState(f: S => S): S = {
    state = doApplyState(TransformState(f(state),state))._2
    state
  }

  private[this] def doApplyState[T](s: ActivityState[T]): (T,S) =
    applyState[T].applyOrElse(s, defaultApplyState[T])
  def defaultApplyState[T](s: ActivityState[T]): (T,S) = s.zero -> s.state

  final override def onCreate(savedInstanceState: Bundle) = {
    state = doApplyState(OnPreCreate(initialState(Option(savedInstanceState))))._2
    super.onCreate(savedInstanceState)
    state = doApplyState(OnCreate(state))._2
  }

  final override def onCreateOptionsMenu(m: Menu): Boolean = {
    val b = super.onCreateOptionsMenu(m)
    val (r,st) = doApplyState(OnCreateOptionsMenu(state, m))
    state = st
    b || r
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    val (r,st) = doApplyState(OnOptionsItemSelected(state, item))
    state = st
    r || super.onOptionsItemSelected(item)
  }

  final override def onSaveInstanceState(outState: Bundle) = {
    super.onSaveInstanceState(outState)
    state = doApplyState(SaveState(state, outState))._2
  }

  final override def onStart() = {
    super.onStart()
    state = doApplyState(OnStart(state))._2
  }

  final override def onResume() = {
    super.onResume()
    state = doApplyState(OnResume(state))._2
  }

  final override def onPause() = {
    super.onPause()
    state = doApplyState(OnPause(state))._2
  }

  final override def onStop() = {
    super.onStop()
    state = doApplyState(OnStop(state))._2
  }

  final override def onDestroy() = {
    super.onDestroy()
    state = doApplyState(OnDestroy(state))._2
  }
}
