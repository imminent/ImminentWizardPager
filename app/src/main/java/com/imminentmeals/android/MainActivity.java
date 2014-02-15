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
package com.imminentmeals.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.imminentmeals.android.actionbar.ActionBarOwner;
import com.imminentmeals.android.core.Main;
import com.imminentmeals.android.core.MainView;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.utilities.MortarUtilities;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarContext;
import mortar.MortarScope;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity extends Activity implements MortarContext, ActionBarOwner.View {
  @Inject ActionBarOwner action_bar_owner;

  @Override protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    if (isWrongInstance()) {
      finish();
      return;
    }

    _action_bar_menu_action = Optional.absent();
    final MortarScope parent_scope = MortarUtilities.rootScope(this);
    _activity_scope = Mortar.requireActivityScope(parent_scope, new Main());
    Mortar.inject(this, this);

    _activity_scope.onCreate(icicle);
    setContentView(R.layout.activity_main);

    action_bar_owner.takeView(this);
  }

  @Override protected void onSaveInstanceState(@Nonnull Bundle icicle) {
    super.onSaveInstanceState(icicle);
    _activity_scope.onSaveInstanceState(icicle);
  }

  /** Inform the view about back events. */
  @Override public void onBackPressed() {
    // Give the view a chance to handle going back. If it declines the honor, let super do its thing.
    final MainView view = getMainView();
    if (!view.onBackPressed()) super.onBackPressed();
  }

  /** Inform the view about up events. */
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      final MainView view = getMainView();
      return view.onUpPressed();
    }

    return super.onOptionsItemSelected(item);
  }

  /** Configure the action bar menu as required by {@link ActionBarOwner.View}. */
  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (_action_bar_menu_action.isPresent()) {
      final ActionBarOwner.MenuAction menu_action = _action_bar_menu_action.get();
      menu.add(menu_action.title)
          .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
          .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override public boolean onMenuItemClick(MenuItem action) {
              menu_action.action.call();
              return true;
            }
          });
    }
    return true;
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    action_bar_owner.dropView(this);

    if (isFinishing() && _activity_scope != null) {
      _activity_scope.destroy();
      _activity_scope = null;
    }
  }

  @Override public MortarScope getMortarScope() {
    return _activity_scope;
  }

  @Override public void showHomeIcon() {
    final ActionBar action_bar = getActionBar();
    assert action_bar != null;
    action_bar.setDisplayShowHomeEnabled(true);
  }

  @Override public void hideHomeIcon() {
    final ActionBar action_bar = getActionBar();
    assert action_bar != null;
    action_bar.setDisplayShowHomeEnabled(false);
  }

  @Override public void showUpButton() {
    final ActionBar action_bar = getActionBar();
    assert action_bar != null;
    action_bar.setDisplayHomeAsUpEnabled(true);
    action_bar.setHomeButtonEnabled(true);
  }

  @Override public void hideUpButton() {
    final ActionBar action_bar = getActionBar();
    assert action_bar != null;
    action_bar.setDisplayHomeAsUpEnabled(false);
    action_bar.setHomeButtonEnabled(false);
  }

  @Override public void title(Optional<CharSequence> title) {
    final ActionBar action_bar = getActionBar();
    assert action_bar != null;
    action_bar.setTitle(title.orNull());
  }

  @Override public void title(@Nonnegative int title) {
    final ActionBar action_bar = getActionBar();
    assert action_bar != null;
    action_bar.setTitle(title);
  }

  @Override public void menu(Optional<ActionBarOwner.MenuAction> action) {
    if (action != _action_bar_menu_action) {
      _action_bar_menu_action = action;
      invalidateOptionsMenu();
    }
  }

  /**
   * Dev tools and the play store (and others?) launch with a different intent, and so
   * lead to a redundant instance of this activity being spawned. <a
   * href="http://stackoverflow.com/questions/17702202/find-out-whether-the-current-activity-will-be-task-root-eventually-after-pendin"
   * >Details</a>.
   */
  private boolean isWrongInstance() {
    if (!isTaskRoot()) {
      final Optional<Intent> intent = Optional.fromNullable(getIntent());
      //noinspection ConstantConditions
      boolean is_main_action = intent.isPresent() && intent.get().getAction().equals(ACTION_MAIN);
      return intent.get().hasCategory(CATEGORY_LAUNCHER) && is_main_action;
    }
    return false;
  }

  private MainView getMainView() {
    final ViewGroup root = ButterKnife.findById(this, android.R.id.content);
    return (MainView) root.getChildAt(0);
  }

  private MortarActivityScope                 _activity_scope;
  private Optional<ActionBarOwner.MenuAction> _action_bar_menu_action;
}
