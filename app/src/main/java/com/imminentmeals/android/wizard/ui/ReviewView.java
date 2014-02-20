package com.imminentmeals.android.wizard.ui;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.imminentmeals.android.R;
import com.imminentmeals.android.wizard.ReviewPage;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import mortar.Mortar;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

/**
 * Created by dandre on 2/11/14.
 */
@ParametersAreNonnullByDefault
public class ReviewView extends LinearLayout {
  @Inject /* package */                         ReviewPage.Presenter presenter;
  @InjectView(android.R.id.list) /* package */  ListView             list;
  @InjectView(android.R.id.title) /* package */ TextView             title;

  public ReviewView(Context context, AttributeSet attributes) {
    super(context, attributes);
    Mortar.inject(context, this);
    final LayoutInflater layout_inflater =
        (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    layout_inflater.inflate(R.layout.wizard_view_choice, this, true);
    ButterKnife.inject(this, this);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> _, View __, int position, long ___) {
        presenter.onItemSelected(position);
      }
    });
    list.setChoiceMode(CHOICE_MODE_SINGLE);
    assert getResources() != null;
    title.setText(R.string.review);
    title.setTextColor(getResources().getColor(R.color.review_green));
  }

  public void title(String title) {
    this.title.setText(title);
  }

  public void reviewAdapter(ListAdapter adapter) {
    list.setAdapter(adapter);
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
