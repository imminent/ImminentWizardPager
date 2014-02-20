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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imminentmeals.android.wizard;

import android.content.Context;
import android.view.View;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.wizard.ui.MultipleChoiceView;
import com.imminentmeals.android.wizard.ui.PageCallback;
import flow.Layout;
import flow.Layouts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import mortar.Mortar;

/**
 * A page offering the user a number of non-mutually exclusive choices.
 */
@ParametersAreNonnullByDefault @Layout(R.layout.wizard_multiple_fixed_choice)
public class MultipleFixedChoicePage extends SingleFixedChoicePage {

  public MultipleFixedChoicePage(WizardListener callbacks, String title) {
    super(callbacks, title);
  }

  @Override public View createView(Context context) {
    context = Mortar.getScope(context).requireChild(this).createContext(context);
    return ((MultipleChoiceView) Layouts.createView(context, this)).withKey(key());
  }

  @Override public void addReviewItemsToList(List<ReviewItem> destination) {
    final StringBuilder string_builder = new StringBuilder();

    final Optional<String[]> selections =
        Optional.fromNullable(_data.getStringArray(SIMPLE_DATA_KEY));
    if (selections.isPresent() && selections.get().length > 0) {
      final String comma = ", ";
      for (String selection : selections.get()) {
        if (string_builder.length() > 0) string_builder.append(comma);
        string_builder.append(selection);
      }
    }

    destination.add(new ReviewItem(title(), string_builder.toString(), key()));
  }

  @Override public SingleFixedChoicePage addChoices(String... choices) {
    return super.addChoices(choices);
  }

  @Override public SingleFixedChoicePage value(String value) {
    return super.value(value);
  }

  @Override public boolean isCompleted() {
    final Optional<String[]> selections =
        Optional.fromNullable(_data.getStringArray(SIMPLE_DATA_KEY));
    return selections.isPresent() && selections.get().length > 0;
  }

  @Override public String getMortarScopeName() {
    return "MultipleFixedChoicePage{key=" + key() + "}";
  }

  @Override public Object getDaggerModule() {
    return new Module();
  }

  @dagger.Module(injects = MultipleChoiceView.class, complete = false)
  /* package */final static class Module {}

  @ParametersAreNonnullByDefault @Singleton
  public static class Presenter
      extends ChoiceViewPresenter<MultipleChoiceView, MultipleFixedChoicePage> {

    @Inject /* package */Presenter(PageCallback callback) {
      super(callback);
    }

    public void onItemSelected(String[] selections) {
      page().data().putStringArray(SIMPLE_DATA_KEY, selections);
      page().notifyDataChanged();
    }

    @Override protected void updateView(MultipleFixedChoicePage page, ArrayList<String> choices) {
      final MultipleChoiceView view = getView();
      view.title(page.title());
      view.choices(choices);
      final Optional<String[]> selections =
          Optional.fromNullable(page.data().getStringArray(SIMPLE_DATA_KEY));
      if (!selections.isPresent() || selections.get().length == 0) return;

      final Set<String> selection_set = new HashSet<>(Arrays.asList(selections.get()));
      // Pre-selects currently selected item
      for (int i = 0, total = choices.size(); i < total; i++) {
        if (selection_set.contains(choices.get(i))) view.selectedChoice(i);
      }
    }
  }
}
