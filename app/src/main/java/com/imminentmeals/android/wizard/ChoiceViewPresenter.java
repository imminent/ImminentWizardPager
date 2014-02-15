package com.imminentmeals.android.wizard;

import android.os.Bundle;
import android.view.View;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.wizard.ui.PageCallback;
import java.util.ArrayList;
import mortar.ViewPresenter;

/**
 * Created by dandre on 2/13/14.
 */
public abstract class ChoiceViewPresenter<V extends View, T extends SingleFixedChoicePage>
    extends ViewPresenter<V> {

  protected ChoiceViewPresenter(PageCallback callback) {
    _callback = callback;
    _choices = Optional.absent();
    _page = Optional.absent();
  }

  protected abstract void updateView(T page, ArrayList<String> choices);

  public void key(String key) {
    if (!_page.isPresent()) {
      preparePage(key);
      if (_choices.isPresent()) updateView(_page.get(), _choices.get());
    }
  }

  protected T page() {
    return _page.get();
  }

  @Override public void onLoad(Bundle icicle) {
    if (Optional.fromNullable(icicle).isPresent() && !_choices.isPresent()) {
      _choices = Optional.of(icicle.getStringArrayList("choices"));
    }
    if (_page.isPresent()) updateView(_page.get(), _choices.get());
  }

  @Override public void onSave(Bundle icicle) {
    if (_choices.isPresent()) icicle.putStringArrayList("choices", _choices.get());
  }

  private void preparePage(String key) {
    _page = _callback.retrievePage(key);
    final T page = _page.get();
    final int number_of_choices = page.numberOfOptions();
    final ArrayList<String> choices = new ArrayList<>(number_of_choices);
    for (int position = 0; position < number_of_choices; position++) {
      choices.add(page.optionForPosition(position));
    }
    _choices = Optional.of(choices);
  }

  private PageCallback                _callback;
  private Optional<ArrayList<String>> _choices;
  private Optional<T>                 _page;
}
