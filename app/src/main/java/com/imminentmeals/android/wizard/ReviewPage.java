package com.imminentmeals.android.wizard;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.imminentmeals.android.R;
import com.imminentmeals.android.wizard.ui.ReviewView;
import flow.Layout;
import flow.Layouts;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import mortar.Blueprint;
import mortar.Mortar;
import mortar.ViewPresenter;

/**
 * Created by dandre on 2/11/14.
 */
@ParametersAreNonnullByDefault @Layout(R.layout.wizard_review)
public class ReviewPage implements Blueprint {

  public static View createView(Context context) {
    context = Mortar.getScope(context).requireChild(new ReviewPage()).createContext(context);
    return Layouts.createView(context, ReviewPage.class);
  }

  @Override public String getMortarScopeName() {
    return getClass().getName();
  }

  @Override public Object getDaggerModule() {
    return new Module();
  }

  @dagger.Module(injects = ReviewView.class, complete = false)
  /* package */final static class Module {}

  @ParametersAreNonnullByDefault @Singleton
  public static class Presenter extends ViewPresenter<ReviewView> implements WizardListener {

    @Inject /* package */Presenter(ReviewCallback callback) {
      _review_item_comparator = new Comparator<ReviewItem>() {

        @Override public int compare(ReviewItem a, ReviewItem b) {
          return a.weight() > b.weight()? +1 : a.weight() < b.weight()? -1 : 0;
        }
      };
      _adapter = new ReviewAdapter();
      _review_callback = callback;
      _wizard_model = callback.onGetModel();
      onPageTreeChanged();
    }

    @ParametersAreNonnullByDefault
    public interface ReviewCallback {

      WizardModel onGetModel();

      void onEditScreenAfterReview(String page_key);
    }

    public void onItemSelected(int position) {
      _review_callback.onEditScreenAfterReview(_current_review_items.get(position).pageKey());
    }

    @Override public void onLoad(Bundle icicle) {
      updateView();
    }

    @Override public void dropView(ReviewView view) {
      super.dropView(view);
      _wizard_model.unregisterListener(this);
    }

    @Override public void onSave(Bundle icicle) { }

    @Override public void onPageTreeChanged() {
      onPageDataChanged(null);
    }

    @Override public void onPageDataChanged(Page changedPage) {
      final ArrayList<ReviewItem> review_items = new ArrayList<>();
      for (Page page : _wizard_model.getCurrentPageSequence()) {
        page.addReviewItemsToList(review_items);
      }
      Collections.sort(review_items, _review_item_comparator);
      _current_review_items = review_items;

      _adapter.notifyDataSetInvalidated();
    }

    private void updateView() {
      final ReviewView view = getView();
      _wizard_model.registerListener(this);
      view.reviewAdapter(_adapter);
    }

    private class ReviewAdapter extends BaseAdapter {

      @Override public boolean hasStableIds() {
        return true;
      }

      @Override public int getItemViewType(int position) {
        return 0;
      }

      @Override public int getViewTypeCount() {
        return 1;
      }

      @Override public boolean areAllItemsEnabled() {
        return true;
      }

      @Override public Object getItem(int position) {
        return _current_review_items.get(position);
      }

      @Override public long getItemId(int position) {
        return _current_review_items.get(position).hashCode();
      }

      @Override public View getView(int position, View view, ViewGroup container) {
        assert Presenter.this.getView() != null && Presenter.this.getView().getContext() != null;
        //noinspection ConstantConditions
        final LayoutInflater inflater = LayoutInflater.from(Presenter.this.getView().getContext());
        final View root_view = inflater.inflate(R.layout.list_item_review, container, false);

        final ReviewItem review_item = _current_review_items.get(position);
        final String value = review_item.displayValue();
        assert root_view != null;
        ((TextView) root_view.findViewById(android.R.id.text1)).setText(review_item.title());
        if (TextUtils.isEmpty(value)) {
          ((TextView) root_view.findViewById(android.R.id.text2)).setText(R.string.none);
        } else {
          ((TextView) root_view.findViewById(android.R.id.text2)).setText(value);
        }
        return root_view;
      }

      @Override public int getCount() {
        return _current_review_items.size();
      }
    }

    private Comparator<ReviewItem> _review_item_comparator;
    private ReviewCallback         _review_callback;
    private List<ReviewItem> _current_review_items;
    private ReviewAdapter    _adapter;
    /*package */ WizardModel _wizard_model;
  }
}
