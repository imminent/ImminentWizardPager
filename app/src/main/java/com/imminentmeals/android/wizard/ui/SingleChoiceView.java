package com.imminentmeals.android.wizard.ui;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
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
import com.imminentmeals.android.wizard.SingleFixedChoicePage;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import mortar.Mortar;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

/**
 * Created by dandre on 2/9/14.
 */
@ParametersAreNonnullByDefault
public class SingleChoiceView extends LinearLayout {
  @Inject /* package */                         SingleFixedChoicePage.Presenter presenter;
  @InjectView(android.R.id.list) /* package */  ListView                        list;
  @InjectView(android.R.id.title) /* package */ TextView                        title;

  public SingleChoiceView(Context context, AttributeSet attributes) {
    super(context, attributes);
    Mortar.inject(context, this);
    final LayoutInflater layout_inflater =
        (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    layout_inflater.inflate(R.layout.wizard_view_choice, this, true);
    ButterKnife.inject(this, this);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> _, View __, int position, long ___) {
        presenter.onItemSelected(list.getAdapter().getItem(position).toString());
      }
    });
    list.setChoiceMode(CHOICE_MODE_SINGLE);
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
                                       android.R.layout.simple_list_item_single_choice, //
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
