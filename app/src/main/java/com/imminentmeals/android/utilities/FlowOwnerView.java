/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imminentmeals.android.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import flow.Flow;
import flow.Layouts;
import javax.annotation.ParametersAreNonnullByDefault;
import mortar.Blueprint;
import mortar.Mortar;
import mortar.MortarScope;

import static android.view.animation.AnimationUtils.loadAnimation;

/**
 * A parent view that displays subviews within a {@link #getContainer() container view}.
 * <p/>
 * Like all Mortar views, subclasses must call {@link mortar.ViewPresenter#takeView},
 * typically from {@link #onFinishInflate()}. E.g.
 * <code><pre>
 * {@literal @}Override protected void onFinishInflate() {
 *   super.onFinishInflate();
 *   getPresenter().takeView(this);
 * }</pre></code>
 *
 * @param <S> the type of the screens that serve as a {@link Blueprint} for subview. Must
 *            be annotated with {@link flow.Layout}, suitable for use with {@link
 *            flow.Layouts#createView}.
 */
@ParametersAreNonnullByDefault
public abstract class FlowOwnerView<S extends Blueprint> extends FrameLayout {

  public FlowOwnerView(Context context, AttributeSet attributes) {
    super(context, attributes);
    Mortar.inject(context, this);
  }

  /**
   * Return the container used to host child views. Typically this is a {@link
   * android.widget.FrameLayout} under the action bar.
   */
  protected abstract ViewGroup getContainer();

  /**
   * Return the {@link FlowOwner} that manages this view. Remember that subclasses
   * can refine the type returned by this method.
   */
  protected abstract FlowOwner<? extends Blueprint, ?> getPresenter();

  public void showScreen(S screen, Flow.Direction direction) {
    final MortarScope scope = Mortar.getScope(getContext());
    final MortarScope new_child_scope = scope.requireChild(screen);

    final Optional<View> old_child = Optional.fromNullable(getChildView());
    final View new_child;

    if (old_child.isPresent()) {
      final MortarScope old_child_scope = Mortar.getScope(old_child.get().getContext());
      if (old_child_scope.getName().equals(screen.getMortarScopeName())) {
        // Short circuits if it's already showing
        return;
      }

      old_child_scope.destroy();
    }

    // Creates the new child
    final Context child_context = new_child_scope.createContext(getContext());
    new_child = Layouts.createView(child_context, screen);

    if (old_child.isPresent()) setAnimation(direction, old_child.get(), new_child);

    // Out with the old, in with the new
    final ViewGroup container = getContainer();
    if (old_child.isPresent()) container.removeView(old_child.get());
    container.addView(new_child);
  }

  protected void setAnimation(Flow.Direction direction, View old_child, View new_child) {
    final int out =
        direction == Flow.Direction.FORWARD? R.anim.slide_out_left : R.anim.slide_out_right;
    final int in =
        direction == Flow.Direction.FORWARD? R.anim.slide_in_right : R.anim.slide_in_left;

    assert getContext() != null;
    old_child.setAnimation(loadAnimation(getContext(), out));
    new_child.setAnimation(loadAnimation(getContext(), in));
  }

  public boolean onBackPressed() {
    return getPresenter().onRetreatSelected();
  }

  public boolean onUpPressed() {
    return getPresenter().onUpSelected();
  }

  private View getChildView() {
    return getContainer().getChildAt(0);
  }
}
