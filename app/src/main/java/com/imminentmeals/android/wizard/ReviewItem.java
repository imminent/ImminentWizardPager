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

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents a single line item on the final review page.
 */
@ParametersAreNonnullByDefault
public class ReviewItem {
  public static final int DEFAULT_WEIGHT = 0;

  public ReviewItem(String title, String display_value, String page_key) {
    this(title, display_value, page_key, DEFAULT_WEIGHT);
  }

  public ReviewItem(String title, String display_value, String page_key, int weight) {
    _title = title;
    _display_value = display_value;
    _page_key = page_key;
    _weight = weight;
  }

  public String displayValue() {
    return _display_value;
  }

  public void displayValue(String display_value) {
    _display_value = display_value;
  }

  public String pageKey() {
    return _page_key;
  }

  public void pageKey(String page_key) {
    _page_key = page_key;
  }

  public String title() {
    return _title;
  }

  public void title(String title) {
    _title = title;
  }

  public int weight() {
    return _weight;
  }

  public void weight(int weight) {
    _weight = weight;
  }

  private int    _weight;
  private String _title;
  private String _display_value;
  private String _page_key;
}
