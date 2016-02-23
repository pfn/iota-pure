package iota.pure

import android.app.Activity
import android.os.Bundle
import android.view._
import iota.IO

/** beware:
  * https://youtrack.jetbrains.com/issue/SCL-9888
  * https://issues.scala-lang.org/browse/SI-9658
  */
trait PureActivity[S] extends Activity {
  private[this] var state: S = _
  sealed trait ActivityState[T] extends PureState[T,S]
  trait ActivityStateUnit extends ActivityState[Unit] {
    val zero = ()
    def applyResult(io: IO[Unit]): IO[(Unit,S)] = apply(io)
  }
  trait ActivityStateBoolean extends ActivityState[Boolean] {
    val zero = false
    def applyResult(io: IO[Boolean]): IO[(Boolean,S)] = io map (b => b -> state)
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
  def transformState(f: S => S): IO[S] = IO {
    state = applyState(TransformState(f(state),state)).perform()._2
    state
  }

  def applyState[T](s: ActivityState[T]): IO[(T,S)]
  def defaultApplyState[T](s: ActivityState[T]): IO[(T,S)] = IO(s.zero -> s.state)

  final override def onCreate(savedInstanceState: Bundle) = {
    state = applyState(OnPreCreate(initialState(Option(savedInstanceState)))).perform()._2
    super.onCreate(savedInstanceState)
    state = applyState(OnCreate(state)).perform()._2
  }

  final override def onCreateOptionsMenu(m: Menu): Boolean = {
    val b = super.onCreateOptionsMenu(m)
    val (r,st) = applyState(OnCreateOptionsMenu(state, m)).perform()
    state = st
    b || r
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    val (r,st) = applyState(OnOptionsItemSelected(state, item)).perform()
    state = st
    r || super.onOptionsItemSelected(item)
  }

  final override def onSaveInstanceState(outState: Bundle) = {
    super.onSaveInstanceState(outState)
    state = applyState(SaveState(state, outState)).perform()._2
  }

  final override def onStart() = {
    super.onStart()
    state = applyState(OnStart(state)).perform()._2
  }

  final override def onResume() = {
    super.onResume()
    state = applyState(OnResume(state)).perform()._2
  }

  final override def onPause() = {
    super.onPause()
    state = applyState(OnPause(state)).perform()._2
  }

  final override def onStop() = {
    super.onStop()
    state = applyState(OnStop(state)).perform()._2
  }

  final override def onDestroy() = {
    super.onDestroy()
    state = applyState(OnDestroy(state)).perform()._2
  }
}

