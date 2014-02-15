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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.wizard.ui.CustomerInfoView;
import com.imminentmeals.android.wizard.ui.PageCallback;
import flow.Layout;
import flow.Layouts;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import mortar.ViewPresenter;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A page asking for a name and an email.
 */
@ParametersAreNonnullByDefault @Layout(R.layout.wizard_customer_info)
public class CustomerInfoPage extends Page {
  public static final String NAME_DATA_KEY  = "name";
  public static final String EMAIL_DATA_KEY = "email";

  public CustomerInfoPage(WizardListener callbacks, String title) {
    super(callbacks, title);
  }

  @Override public View createView(Context context) {
    return ((CustomerInfoView) Layouts.createView(context, this)).withKey(key());
  }

  @Override public void addReviewItemsToList(List<ReviewItem> destination) {
    destination.add(new ReviewItem("Your name", _data.getString(NAME_DATA_KEY), key(), -1));
    destination.add(new ReviewItem("Your email", _data.getString(EMAIL_DATA_KEY), key(), -1));
  }

  @Override public boolean isCompleted() {
    return !TextUtils.isEmpty(_data.getString(NAME_DATA_KEY));
  }

  @dagger.Module(injects = CustomerInfoView.class, complete = false)
  public static class Module {}

  // TODO: not @Singleton since scope is longer than View currently
  @ParametersAreNonnullByDefault
  public static class Presenter extends ViewPresenter<CustomerInfoView> {

    @Inject Presenter(PageCallback callback) {
      _callback = callback;
      _key = Optional.absent();
      _page = Optional.absent();
    }

    public void key(String key) {
      _key = Optional.of(key);
      if (!_page.isPresent()) preparePage(key);
    }

    private void preparePage(String key) {
      _page = _callback.retrievePage(key);
      _page.get();
      updateView();
    }

    public void onNameSet(Optional<String> name) {
      if (name.isPresent()) { _page.get().data().putString(NAME_DATA_KEY, name.get()); } else {
        _page.get().data().remove(NAME_DATA_KEY);
      }
      _page.get().notifyDataChanged();
    }

    public void onEmailSet(Optional<String> email) {
      if (email.isPresent()) { _page.get().data().putString(EMAIL_DATA_KEY, email.get()); } else {
        _page.get().data().remove(EMAIL_DATA_KEY);
      }
      _page.get().notifyDataChanged();
    }

    @Override public void onLoad(Bundle icicle) {
      if (_page.isPresent()) updateView();
    }

    @Override public void onSave(Bundle icicle) { }

    private void updateView() {
      final CustomerInfoView view = getView();
      view.title(_page.get().title());
      final String name = _page.get().data().getString(NAME_DATA_KEY);
      final String email = _page.get().data().getString(EMAIL_DATA_KEY);
      view.name(name);
      view.email(email);
    }

    @Override public void dropView(CustomerInfoView view) {
      assert view.getContext() != null;
      super.dropView(view);
      final InputMethodManager input_manager =
          (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
      input_manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected PageCallback               _callback;
    protected Optional<String>           _key;
    protected Optional<CustomerInfoPage> _page;
  }
}
