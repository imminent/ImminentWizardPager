package com.imminentmeals.android.wizard.ui;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.wizard.MultipleFixedChoicePage;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import mortar.Mortar;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

/**
 * Created by dandre on 2/10/14.
 */
@ParametersAreNonnullByDefault
public class MultipleChoiceView extends LinearLayout {
  @Inject /* package */                         MultipleFixedChoicePage.Presenter presenter;
  @InjectView(android.R.id.list) /* package */  ListView                          list;
  @InjectView(android.R.id.title) /* package */ TextView                          title;

  public MultipleChoiceView(Context context, AttributeSet attributes) {
    super(context, attributes);
    Mortar.inject(context, this);
    final LayoutInflater layout_inflater =
        (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    layout_inflater.inflate(R.layout.wizard_view_choice, this, true);
    ButterKnife.inject(this, this);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> _, View __, int position, long ___) {
        final Optional<SparseBooleanArray> checked_positions =
            Optional.fromNullable(list.getCheckedItemPositions());
        if (!checked_positions.isPresent()) return;

        final String[] selections = new String[checked_positions.get().size()];
        for (int i = 0, total = checked_positions.get().size(); i < total; i++) {
          if (checked_positions.get().valueAt(i)) {
            selections[i] = list.getAdapter().getItem(position).toString();
          }
        }

        presenter.onItemSelected(selections);
      }
    });
    list.setChoiceMode(CHOICE_MODE_MULTIPLE);
  }

  public View withKey(String key) {
    presenter.key(key);
    return this;
  }

  public void title(String title) {
    this.title.setText(title);
  }

  public void choices(List<String> choices) {
    assert getContext() != null;
    list.setAdapter(new ArrayAdapter<>(getContext(), //
                                       android.R.layout.simple_list_item_multiple_choice, //
                                       android.R.id.text1, //
                                       choices));
  }

  public void selectedChoice(int position) {
    list.setItemChecked(position, true);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    presenter.takeView(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    presenter.dropView(this);
  }
}
