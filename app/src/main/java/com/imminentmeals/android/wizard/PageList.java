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

import com.imminentmeals.android.guava.Optional;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of wizard pages.
 */
public class PageList extends ArrayList<Page> implements PageTreeNode {

  public PageList() { }

  public PageList(Page... pages) {
    for (Page page : pages) add(page);
  }

  @Override public <T extends Page> Optional<T> findByKey(String key) {
    for (Page child_page : this) {
      final Optional<T> found = child_page.findByKey(key);
      if (found.isPresent()) return found;
    }

    return Optional.absent();
  }

  @Override public void flattenCurrentPageSequence(List<Page> destination) {
    for (Page childPage : this) childPage.flattenCurrentPageSequence(destination);
  }
}
