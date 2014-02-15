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
import android.view.View;
import com.imminentmeals.android.guava.Optional;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents a single page in the wizard.
 */
@ParametersAreNonnullByDefault
public abstract class Page implements PageTreeNode {
  /**
   * The key into {@link #data()} used for wizards with simple (single) values.
   */
  public static final String SIMPLE_DATA_KEY = "_";

  protected WizardListener _listener;

  /**
   * Current wizard values/selections.
   */
  protected Bundle _data = new Bundle();
  protected String _title;
  protected boolean _required = false;
  protected Optional<String> _parent_key;

  protected Page(WizardListener listener, String title) {
    _listener = listener;
    _title = title;
    _parent_key = Optional.absent();
  }

  public abstract View createView(Context context);

  public Bundle data() {
    return _data;
  }

  public String title() {
    return _title;
  }

  public boolean isRequired() {
    return _required;
  }

  void parentKey(@Nullable String parent_key) {
    _parent_key = Optional.fromNullable(parent_key);
  }

  @Override public <T extends Page> Optional<T> findByKey(String key) {
    //noinspection unchecked
    return key().equals(key)? Optional.of((T) this) : Optional.<T>absent();
  }

  @Override public void flattenCurrentPageSequence(List<Page> dest) {
    dest.add(this);
  }

  public String key() {
    return (_parent_key.isPresent())? _parent_key.get() + ":" + _title : _title;
  }

  public abstract void addReviewItemsToList(List<ReviewItem> dest);

  public boolean isCompleted() {
    return true;
  }

  public void resetData(Bundle data) {
    _data = data;
    notifyDataChanged();
  }

  public void notifyDataChanged() {
    _listener.onPageDataChanged(this);
  }

  public Page require() {
    _required = true;
    return this;
  }

  public Page makeOptional() {
    _required = false;
    return this;
  }
}
