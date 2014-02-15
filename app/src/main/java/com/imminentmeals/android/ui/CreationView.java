package com.imminentmeals.android.ui;

import android.app.Service;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.models.Recipe;
import com.imminentmeals.android.screens.CreationScreen;
import com.imminentmeals.android.wizard.ui.StepPagerStrip;
import javax.inject.Inject;
import mortar.Mortar;

/**
 * Created by dandre on 2/11/14.
 */
public class CreationView extends LinearLayout {
  @Inject /* package */                       CreationScreen.Presenter presenter;
  @InjectView(R.id.pager) /* package */       ViewPager                pager;
  @InjectView(R.id.strip) /* package */       StepPagerStrip           step_pager_strip;
  @InjectView(R.id.prev_button) /* package */ Button                   button_previous;
  @InjectView(R.id.next_button) /* package */ Button                   button_next;

  public CreationView(Context context, AttributeSet attributes) {
    super(context, attributes);
    Mortar.inject(context, this);
    setOrientation(VERTICAL);
    final LayoutInflater layout_inflater =
        (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    layout_inflater.inflate(R.layout.view_creation, this, true);
    ButterKnife.inject(this);

    step_pager_strip.pageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {

      @Override public void onPageStripSelected(int position) {
        position = Math.min(pager.getAdapter().getCount() - 1, position);
        if (pager.getCurrentItem() != position) pager.setCurrentItem(position);
      }
    });

    pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

      @Override public void onPageSelected(int position) {
        step_pager_strip.currentPage(position);

        presenter.onPageSelected();
      }
    });
    _confirmation_popup = new ConfirmationPopup(context);
  }

  public void updateButtonBar(int total_pages, boolean is_editing_after_review) {
    assert getContext() != null && getContext().getTheme() != null;
    int position = pager.getCurrentItem();
    if (position == total_pages) {
      button_next.setText(R.string.finish);
      button_next.setBackgroundResource(R.drawable.finish_background);
      button_next.setTextAppearance(getContext(), R.style.TextAppearanceFinish);
    } else {
      button_next.setText(is_editing_after_review? R.string.review : R.string.next);
      button_next.setBackgroundResource(R.drawable.selectable_item_background);
      TypedValue v = new TypedValue();
      getContext().getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
      button_next.setTextAppearance(getContext(), v.resourceId);
      button_next.setEnabled(position
                                 != ((CreationScreen.Presenter.WizardAdapter) pager.getAdapter()).cutoffPage());
    }

    button_previous.setVisibility(position <= 0? INVISIBLE : VISIBLE);
  }

  @OnClick(R.id.next_button)
  public void onNext() {
    presenter.onNext(pager.getCurrentItem());
  }

  @OnClick(R.id.prev_button)
  public void onPrevious() {
    pager.setCurrentItem(pager.getCurrentItem() - 1);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    presenter.takeView(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    presenter.dropView(this);
  }

  public void showRecipe(Recipe recipe) {}

  public void numberOfSteps(int number_of_steps) {
    step_pager_strip.numberOfPages(number_of_steps);
  }

  public void nextPage() {
    pager.setCurrentItem(pager.getCurrentItem() + 1);
  }

  public void currentPage(int page) {
    pager.setCurrentItem(page);
  }

  public void adapter(CreationScreen.Presenter.WizardAdapter adapter) {
    pager.setAdapter(adapter);
  }

  public void toast(String text) {
    final Optional<Context> context = Optional.fromNullable(getContext());
    if (context.isPresent()) Toast.makeText(context.get(), text, Toast.LENGTH_SHORT).show();
  }

  public ConfirmationPopup confirmationPopup() {
    return _confirmation_popup;
  }

  private final ConfirmationPopup _confirmation_popup;
}
