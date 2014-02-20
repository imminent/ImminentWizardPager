/*
 * Copyright 2013 Google Inc.
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
 * See the License for the specifiic language governing permissions and
 * limitations under the License.
 */
package com.imminentmeals.android.wizard;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.wizard.ui.PageCallback;
import com.imminentmeals.android.wizard.ui.SingleChoiceView;
import flow.Layout;
import flow.Layouts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import mortar.Blueprint;
import mortar.Mortar;

/**
 * A page offering the user a number of mutually exclusive choices.
 */
@ParametersAreNonnullByDefault @Layout(R.layout.wizard_single_fixed_choice)
public class SingleFixedChoicePage extends Page implements Blueprint {
  protected List<String> _choices;

  public SingleFixedChoicePage(WizardListener listener, String title) {
    super(listener, title);
    _choices = new ArrayList<>();
  }

  public String optionForPosition(int position) {
    return _choices.get(position);
  }

  public int numberOfOptions() {
    return _choices.size();
  }

  public SingleFixedChoicePage addChoices(String... choices) {
    _choices.addAll(Arrays.asList(choices));
    return this;
  }

  public SingleFixedChoicePage value(String value) {
    _data.putString(SIMPLE_DATA_KEY, value);
    return this;
  }

  @Override public View createView(Context context) {
    context = Mortar.getScope(context).requireChild(this).createContext(context);
    return ((SingleChoiceView) Layouts.createView(context, this)).withKey(key());
  }

  @Override public void addReviewItemsToList(List<ReviewItem> dest) {
    dest.add(new ReviewItem(title(), _data.getString(SIMPLE_DATA_KEY), key()));
  }

  @Override public boolean isCompleted() {
    return !TextUtils.isEmpty(_data.getString(SIMPLE_DATA_KEY));
  }

  @Override public String getMortarScopeName() {
    return "SingleFixedChoicePage{key=" + key() + "}";
  }

  @Override public Object getDaggerModule() {
    return new Module();
  }

  @dagger.Module(injects = SingleChoiceView.class, complete = false)
  /* package */final static class Module {}

  @ParametersAreNonnullByDefault @Singleton
  public static class Presenter
      extends ChoiceViewPresenter<SingleChoiceView, SingleFixedChoicePage> {

    @Inject /* package */Presenter(PageCallback callback) {
      super(callback);
    }

    public void onItemSelected(String item) {
      page().data().putString(SIMPLE_DATA_KEY, item);
      page().notifyDataChanged();
    }

    @Override protected void updateView(SingleFixedChoicePage page, ArrayList<String> choices) {
      final SingleChoiceView view = getView();
      view.title(page.title());
      view.choices(choices);
      final Optional<String> selection =
          Optional.fromNullable(page.data().getString(SIMPLE_DATA_KEY));
      if (!selection.isPresent()) return;

      // Pre-selects currently selected item
      for (int i = 0, total = choices.size(); i < total; i++) {
        if (choices.get(i).equals(selection.get())) {
          view.selectedChoice(i);
          break;
        }
      }
    }
  }
}
