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
package com.imminentmeals.android.core;

import com.imminentmeals.android.actionbar.ActionBarModule;
import com.imminentmeals.android.actionbar.ActionBarOwner;
import com.imminentmeals.android.annotations.MainScope;
import com.imminentmeals.android.screens.CreationScreen;
import com.imminentmeals.android.utilities.FlowOwner;
import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Parcer;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import mortar.Blueprint;

@ParametersAreNonnullByDefault
public class Main implements Blueprint {

  @Override public String getMortarScopeName() {
    return getClass().getName();
  }

  @Override public Object getDaggerModule() {
    return new Module();
  }

  @dagger.Module( //
                  includes = ActionBarModule.class, //
                  injects = MainView.class, //
                  addsTo = ApplicationModule.class, //
                  library = true //
  )
  public static class Module {

    @Provides @MainScope Flow provideFlow(Presenter presenter) {
      return presenter.flow().get();
    }
  }

  @Singleton @ParametersAreNonnullByDefault
  public static class Presenter extends FlowOwner<Blueprint, MainView> {

    @Inject Presenter(Parcer<Object> flow_parcer, ActionBarOwner action_bar_owner) {
      super(flow_parcer);
      _action_bar_owner = action_bar_owner;
    }

    @Override public void showScreen(Blueprint new_screen, Flow.Direction direction) {
      final boolean has_up = new_screen instanceof HasParent;
      final String title = new_screen.getClass().getSimpleName();
      _action_bar_owner.config(new ActionBarOwner.Config(true, has_up, title));

      super.showScreen(new_screen, direction);
    }

    @Override protected Blueprint firstScreen() {
      return new CreationScreen();
    }

    private final ActionBarOwner _action_bar_owner;
  }
}
