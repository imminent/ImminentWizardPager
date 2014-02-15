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
import com.imminentmeals.android.guava.Optional;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wizard model, including the pages/steps in the wizard, their dependencies, and
 * their
 * currently populated choices/values/selections.
 * <p/>
 * To create an actual wizard model, extend this class and implement {@link #onNewRootPageList()}.
 */
public abstract class WizardModel implements WizardListener {
  protected Context _context;

  public WizardModel(Context context) {
    _context = context;
    _root_page_list = onNewRootPageList();
  }

  /**
   * Override this to define a new wizard model.
   */
  protected abstract PageList onNewRootPageList();

  @Override public void onPageDataChanged(Page page) {
    // can't use for each because of concurrent modification (review view
    // can get added or removed and will register itself as a listener)
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0; i < _listeners.size(); i++) {
      _listeners.get(i).onPageDataChanged(page);
    }
  }

  @Override public void onPageTreeChanged() {
    // can't use for each because of concurrent modification (review view
    // can get added or removed and will register itself as a listener)
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0; i < _listeners.size(); i++) {
      _listeners.get(i).onPageTreeChanged();
    }
  }

  public <T extends Page> Optional<T> findByKey(String key) {
    return _root_page_list.findByKey(key);
  }

  public void load(Bundle icicle) {
    for (String key : icicle.keySet()) {
      final Optional<? extends Page> page = _root_page_list.findByKey(key);
      if (page.isPresent()) page.get().resetData(icicle.getBundle(key));
    }
  }

  public void registerListener(WizardListener listener) {
    _listeners.add(listener);
  }

  public Bundle save() {
    final Bundle bundle = new Bundle();
    for (Page page : getCurrentPageSequence()) {
      bundle.putBundle(page.key(), page.data());
    }
    return bundle;
  }

  /**
   * Gets the current list of wizard steps, flattening nested (dependent) pages based on the
   * user's choices.
   */
  public List<Page> getCurrentPageSequence() {
    final ArrayList<Page> flattened = new ArrayList<>();
    _root_page_list.flattenCurrentPageSequence(flattened);
    return flattened;
  }

  public void unregisterListener(WizardListener listener) {
    _listeners.remove(listener);
  }

  private List<WizardListener> _listeners = new ArrayList<>();
  private PageList _root_page_list;
}
