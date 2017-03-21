package iota.pure.v2

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view._

private[pure] trait PureFragmentBase[S] {
  private[pure] var state: S = _
  sealed trait FragmentState[T] extends PureState[T,S]
  trait FragmentStateUnit extends FragmentState[Unit] {
    val zero = ()
  }
  trait FragmentStateBoolean extends FragmentState[Boolean] {
    val zero = false
  }
  trait FragmentStateView extends FragmentState[View] {
    val zero = null
  }
  case class OnCreate(state: S)                        extends FragmentStateUnit
  case class OnActivityCreated(state: S)               extends FragmentStateUnit
  case class OnViewCreated(state: S)                   extends FragmentStateUnit
  case class OnDestroy(state: S)                       extends FragmentStateUnit
  case class OnStart(state: S)                         extends FragmentStateUnit
  case class OnStop(state: S)                          extends FragmentStateUnit
  case class OnResume(state: S)                        extends FragmentStateUnit
  case class OnPause(state: S)                         extends FragmentStateUnit
  case class OnAttach(state: S)                        extends FragmentStateUnit
  case class OnDetach(state: S)                        extends FragmentStateUnit
  case class OnDestroyView(state: S)                   extends FragmentStateUnit
  case class TransformState(state: S, oldState: S)     extends FragmentStateUnit
  case class SaveState(state: S, bundle: Bundle)       extends FragmentStateUnit
  case class OnCreateView(state: S, inflater: LayoutInflater, container: ViewGroup) extends FragmentStateView
  case class OnCreateOptionsMenu(state: S, menu: Menu, inflater: MenuInflater) extends FragmentStateUnit
  case class OnOptionsItemSelected(state: S, item: MenuItem) extends FragmentStateBoolean

  def initialState(savedState: Option[Bundle], arguments: Option[Bundle]): S
  def transformState(f: S => S): S = {
    state = doApplyState(TransformState(f(state),state))._2
    state
  }

  def applyState[T]: PartialFunction[FragmentState[T],(T,S)]
  def doApplyState[T](s: FragmentState[T]): (T,S) =
    applyState[T].applyOrElse(s, defaultApplyState[T])
  def defaultApplyState[T](s: FragmentState[T]): (T,S) = s.zero -> s.state
}

@TargetApi(11)
trait PureFragment[S] extends android.app.Fragment with PureFragmentBase[S] {
  final override def onActivityCreated(savedInstanceState: Bundle) = {
    super.onActivityCreated(savedInstanceState)
    state = doApplyState(OnActivityCreated(state))._2
  }

  final override def onAttach(context: Activity) = {
    super.onAttach(context)
    state = doApplyState(OnAttach(state))._2
  }

  @TargetApi(11)
  final override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    state = doApplyState(OnCreate(initialState(
      Option(savedInstanceState), Option(getArguments))))._2
  }

  final override def onCreateOptionsMenu(m: Menu, inflater: MenuInflater): Unit = {
    super.onCreateOptionsMenu(m, inflater)
    state = doApplyState(OnCreateOptionsMenu(state, m, inflater))._2
  }

  final override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) = {
    val (view, st) = doApplyState(OnCreateView(state, inflater, container))
    state = st
    view
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle) = {
    super.onViewCreated(view, savedInstanceState)
    state = doApplyState(OnViewCreated(state))._2
  }

  final override def onDestroy() = {
    super.onDestroy()
    state = doApplyState(OnDestroy(state))._2
  }

  final override def onDestroyView() = {
    super.onDestroyView()
    state = doApplyState(OnDestroyView(state))._2
  }

  final override def onDetach() = {
    super.onDetach()
    state = doApplyState(OnDetach(state))._2
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    val (r,st) = doApplyState(OnOptionsItemSelected(state, item))
    state = st
    r || super.onOptionsItemSelected(item)
  }

  final override def onPause() = {
    super.onPause()
    state = doApplyState(OnPause(state))._2
  }

  final override def onResume() = {
    super.onResume()
    state = doApplyState(OnResume(state))._2
  }

  final override def onSaveInstanceState(outState: Bundle) = {
    super.onSaveInstanceState(outState)
    state = doApplyState(SaveState(state, outState))._2
  }

  final override def onStart() = {
    super.onStart()
    state = doApplyState(OnStart(state))._2
  }

  final override def onStop() = {
    super.onStop()
    state = doApplyState(OnStop(state))._2
  }
}

trait PureFragmentCompat[S] extends android.support.v4.app.Fragment with PureFragmentBase[S] {
  final override def onActivityCreated(savedInstanceState: Bundle) = {
    super.onActivityCreated(savedInstanceState)
    state = doApplyState(OnActivityCreated(state))._2
  }

  final override def onAttach(context: Context) = {
    super.onAttach(context)
    state = doApplyState(OnAttach(state))._2
  }

  final override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    state = doApplyState(OnCreate(initialState(
      Option(savedInstanceState), Option(getArguments))))._2
  }

  final override def onCreateOptionsMenu(m: Menu, inflater: MenuInflater): Unit = {
    super.onCreateOptionsMenu(m, inflater)
    state = doApplyState(OnCreateOptionsMenu(state, m, inflater))._2
  }

  final override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) = {
    val (view, st) = doApplyState(OnCreateView(state, inflater, container))
    state = st
    view
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle) = {
    super.onViewCreated(view, savedInstanceState)
    state = doApplyState(OnViewCreated(state))._2
  }

  final override def onDestroy() = {
    super.onDestroy()
    state = doApplyState(OnDestroy(state))._2
  }

  final override def onDestroyView() = {
    super.onDestroyView()
    state = doApplyState(OnDestroyView(state))._2
  }

  final override def onDetach() = {
    super.onDetach()
    state = doApplyState(OnDetach(state))._2
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    val (r,st) = doApplyState(OnOptionsItemSelected(state, item))
    state = st
    r || super.onOptionsItemSelected(item)
  }

  final override def onPause() = {
    super.onPause()
    state = doApplyState(OnPause(state))._2
  }

  final override def onResume() = {
    super.onResume()
    state = doApplyState(OnResume(state))._2
  }

  final override def onSaveInstanceState(outState: Bundle) = {
    super.onSaveInstanceState(outState)
    state = doApplyState(SaveState(state, outState))._2
  }

  final override def onStart() = {
    super.onStart()
    state = doApplyState(OnStart(state))._2
  }

  final override def onStop() = {
    super.onStop()
    state = doApplyState(OnStop(state))._2
  }
}
