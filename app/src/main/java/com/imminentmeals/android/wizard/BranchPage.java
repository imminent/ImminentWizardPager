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

import android.text.TextUtils;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import flow.Layout;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A page representing a branching point in the wizard. Depending on which choice is selected, the
 * next set of steps in the wizard may change.
 */
@ParametersAreNonnullByDefault @Layout(R.layout.wizard_single_fixed_choice)
public class BranchPage extends SingleFixedChoicePage {

  public BranchPage(WizardListener callbacks, String title) {
    super(callbacks, title);
  }

  @Override public <T extends Page> Optional<T> findByKey(String key) {
    if (key().equals(key)) {
      //noinspection unchecked
      return Optional.of((T) this);
    }

    for (Branch branch : _branches) {
      final Optional<T> found = branch.child_page_list.findByKey(key);
      if (found.isPresent()) return found;
    }

    return Optional.absent();
  }

  @Override public void flattenCurrentPageSequence(List<Page> destination) {
    super.flattenCurrentPageSequence(destination);
    for (Branch branch : _branches) {
      if (branch.choice.equals(_data.getString(SIMPLE_DATA_KEY))) {
        branch.child_page_list.flattenCurrentPageSequence(destination);
        break;
      }
    }
  }

  @Override public String getMortarScopeName() {
    return "BranchPage{key=" + key() + "}";
  }

  public BranchPage addBranch(String choice, Page... child_pages) {
    final PageList child_page_list = new PageList(child_pages);
    for (Page page : child_page_list) page.parentKey(choice);
    _branches.add(new Branch(choice, child_page_list));
    return this;
  }

  public BranchPage addBranch(String choice) {
    _branches.add(new Branch(choice, new PageList()));
    return this;
  }

  @Override public String optionForPosition(int position) {
    return _branches.get(position).choice;
  }

  @Override public int numberOfOptions() {
    return _branches.size();
  }

  @Override public void addReviewItemsToList(List<ReviewItem> destination) {
    destination.add(new ReviewItem(title(), _data.getString(SIMPLE_DATA_KEY), key()));
  }

  @Override public boolean isCompleted() {
    return !TextUtils.isEmpty(_data.getString(SIMPLE_DATA_KEY));
  }

  @Override public void notifyDataChanged() {
    _listener.onPageTreeChanged();
    super.notifyDataChanged();
  }

  public BranchPage value(String value) {
    _data.putString(SIMPLE_DATA_KEY, value);
    return this;
  }

  private static class Branch {
    public String   choice;
    public PageList child_page_list;

    private Branch(String choice, PageList child_page_list) {
      this.choice = choice;
      this.child_page_list = child_page_list;
    }
  }

  private List<Branch> _branches = new ArrayList<>();
}
