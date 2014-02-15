package com.imminentmeals.android.utilities;

import android.os.Bundle;
import com.imminentmeals.android.guava.Optional;
import flow.Backstack;
import flow.Flow;
import flow.Parcer;
import mortar.Blueprint;
import mortar.ViewPresenter;

/** Base class for all presenters that manage a {@link flow.Flow}. */
public abstract class FlowOwner<S extends Blueprint, V extends FlowOwnerView<S>>
    extends ViewPresenter<V> implements Flow.Listener {

  protected FlowOwner(Parcer<Object> parcer) {
    _parcer = parcer;
    _flow = Optional.absent();
  }

  /** Returns the first screen shown by this presenter. */
  protected abstract S firstScreen();

  @Override public void onLoad(Bundle icicle) {
    super.onLoad(icicle);

    if (!_flow.isPresent()) {
      final Backstack backstack;

      if (icicle != null) {
        backstack = Backstack.from(icicle.getParcelable(_FLOW_KEY), _parcer);
      } else {
        backstack = Backstack.fromUpChain(firstScreen());
      }

      _flow = Optional.of(new Flow(backstack, this));
    }

    //noinspection unchecked
    showScreen((S) _flow.get().getBackstack().current().getScreen(), null);
  }

  @Override public void onSave(Bundle icicle) {
    super.onSave(icicle);
    icicle.putParcelable(_FLOW_KEY, _flow.get().getBackstack().getParcelable(_parcer));
  }

  @Override public void go(Backstack backstack, Flow.Direction flow_direction) {
    //noinspection unchecked
    final S new_screen = (S) backstack.current().getScreen();
    showScreen(new_screen, flow_direction);
  }

  public boolean onRetreatSelected() {
    return flow().get().goBack();
  }

  public boolean onUpSelected() {
    return flow().get().goUp();
  }

  protected void showScreen(S new_screen, Flow.Direction flow_direction) {
    final Optional<V> view = Optional.fromNullable(getView());
    if (!view.isPresent()) return;

    view.get().showScreen(new_screen, flow_direction);
  }

  public final Optional<Flow> flow() {
    return _flow;
  }

  private       Optional<Flow> _flow;
  private final Parcer<Object> _parcer;
  private static final String _FLOW_KEY = "FLOW_KEY";
}
