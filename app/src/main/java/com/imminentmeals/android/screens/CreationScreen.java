package com.imminentmeals.android.screens;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.imminentmeals.android.R;
import com.imminentmeals.android.SandwichWizardModel;
import com.imminentmeals.android.actionbar.ActionBarOwner;
import com.imminentmeals.android.core.Main;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.models.Confirmation;
import com.imminentmeals.android.models.Recipe;
import com.imminentmeals.android.ui.CreationView;
import com.imminentmeals.android.wizard.CustomerInfoPage;
import com.imminentmeals.android.wizard.MultipleFixedChoicePage;
import com.imminentmeals.android.wizard.Page;
import com.imminentmeals.android.wizard.ReviewPage;
import com.imminentmeals.android.wizard.SingleFixedChoicePage;
import com.imminentmeals.android.wizard.WizardListener;
import com.imminentmeals.android.wizard.WizardModel;
import com.imminentmeals.android.wizard.ui.PageCallback;
import dagger.Provides;
import flow.Flow;
import flow.Layout;
import java.util.Arrays;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import mortar.Blueprint;
import mortar.PopupPresenter;
import mortar.ViewPresenter;

import static com.imminentmeals.android.wizard.ReviewPage.Presenter.ReviewCallback;

@Layout(R.layout.screen_creation) //
public class CreationScreen implements Blueprint {

  @Override public String getMortarScopeName() {
    return getClass().getName();
  }

  @Override public Object getDaggerModule() {
    return Arrays.asList(new Module(), new SingleFixedChoicePage.Module(),
                         new MultipleFixedChoicePage.Module(), new CustomerInfoPage.Module(),
                         new ReviewPage.Module());
  }

  @dagger.Module(injects = CreationView.class, addsTo = Main.Module.class, library = true)
  static class Module {

    @Provides @Singleton Recipe providesRecipe() {
      return new Recipe();
    }

    @Provides @Singleton PageCallback providesPageCallback(Presenter page_callback) {
      return page_callback;
    }

    @Provides @Singleton ReviewCallback providesReviewCallback(Presenter review_callback) {
      return review_callback;
    }
  }

  @Singleton @ParametersAreNonnullByDefault
  public static class Presenter extends ViewPresenter<CreationView>
      implements PageCallback, ReviewCallback, WizardListener {

    @Inject Presenter(Flow flow, ActionBarOwner action_bar, Recipe recipe) {
      _flow = flow;
      _action_bar = action_bar;
      _recipe = recipe;
      _wizard_model = Optional.absent();
      _current_page_sequence = Optional.absent();
      _adapter = Optional.absent();
      _confirmation_presenter = new PopupPresenter<Confirmation, Boolean>() {

        @Override protected void onPopupResult(Boolean confirmed) {
          if (confirmed) Presenter.this.getView().toast("Haven't implemented that, friend.");
        }
      };
      _popup_message = Optional.absent();
      _popup_confirm_button = Optional.absent();
      _popup_cancel_button = Optional.absent();
    }

    public void onPageSelected() {
      if (_should_consume_page_selected_event) {
        _should_consume_page_selected_event = false;
        return;
      }

      _is_editing_after_review = false;
      getView().updateButtonBar(_current_page_sequence.get().size(), _is_editing_after_review);
    }

    public void onNext(int current_page) {
      assert _current_page_sequence.isPresent();
      if (current_page == _current_page_sequence.get().size()) {
        _confirmation_presenter.show(new Confirmation(_popup_message.get(),
                                                      _popup_confirm_button.get(),
                                                      _popup_cancel_button.get()));
      } else {
        assert _adapter.isPresent();
        if (_is_editing_after_review) {
          getView().currentPage(_adapter.get().getCount() - 1);
        } else {
          getView().nextPage();
        }
      }
    }

    @Override public void onLoad(Bundle icicle) {
      final CreationView view = getView();
      final ActionBarOwner.Config action_bar_config = _action_bar.config();
      final Optional<Resources> res = Optional.of(view.getResources());
      if (!_popup_message.isPresent()) {
        _popup_message = Optional.of(res.get().getString(R.string.submit_confirm_message));
      }
      if (!_popup_confirm_button.isPresent()) {
        _popup_confirm_button = Optional.of(res.get().getString(R.string.submit_confirm_button));
      }
      if (!_popup_cancel_button.isPresent()) {
        _popup_cancel_button = Optional.of(res.get().getString(android.R.string.cancel));
      }
      _action_bar.config(new ActionBarOwner.Config(true, //
                                                   action_bar_config.is_up_button_shown, //
                                                   res.get().getString(R.string.title_creation)));

      if (!_wizard_model.isPresent()) {
        _wizard_model = Optional.of((WizardModel) new SandwichWizardModel(view.getContext()));
      }
      if (Optional.fromNullable(icicle).isPresent()) {
        _wizard_model.get().load(icicle.getBundle("model"));
      }
      _wizard_model.get().registerListener(this);
      view.showRecipe(_recipe);
      //noinspection unchecked
      _adapter = Optional.of(new WizardAdapter(view.getContext()));
      view.adapter(_adapter.get());
      onPageTreeChanged();
      _confirmation_presenter.takeView(view.confirmationPopup());
    }

    @Override protected void onSave(Bundle icicle) {
      if (_wizard_model.isPresent()) icicle.putBundle("model", _wizard_model.get().save());
    }

    @Override public void dropView(CreationView view) {
      super.dropView(view);
      if (_wizard_model.isPresent()) _wizard_model.get().unregisterListener(this);
      _confirmation_presenter.dropView(view.confirmationPopup());
    }

    @Override public <T extends Page> Optional<T> retrievePage(String key) {
      return _wizard_model.get().findByKey(key);
    }

    @Override public WizardModel onGetModel() {
      return _wizard_model.get();
    }

    @Override public void onEditScreenAfterReview(String page_key) {
      if (!_current_page_sequence.isPresent()) return;
      for (int i = _current_page_sequence.get().size() - 1; i >= 0; i--) {
        if (_current_page_sequence.get().get(i).key().equals(page_key)) {
          _should_consume_page_selected_event = true;
          _is_editing_after_review = true;
          getView().currentPage(i);
          getView().updateButtonBar(_current_page_sequence.get().size(), _is_editing_after_review);
          break;
        }
      }
    }

    @Override public void onPageDataChanged(Page page) {
      if (page.isRequired()) {
        if (_current_page_sequence.isPresent() && recalculateCutOffPage()) {
          getView().updateButtonBar(_current_page_sequence.get().size(), _is_editing_after_review);
        }
      }
    }

    @Override public void onPageTreeChanged() {
      if (_wizard_model.isPresent()) {
        _current_page_sequence = Optional.of(_wizard_model.get().getCurrentPageSequence());
        recalculateCutOffPage();
        getView().numberOfSteps(_current_page_sequence.get().size() + 1); // + 1 = review step
        getView().updateButtonBar(_current_page_sequence.get().size(), _is_editing_after_review);
      }
    }

    private boolean recalculateCutOffPage() {
      assert _current_page_sequence.isPresent();
      // Cut off the pager adapter at first required page that isn't completed
      final int count = _current_page_sequence.get().size();
      int cutoff_page = count + 1;
      for (int i = 0; i < count; i++) {
        final Page page = _current_page_sequence.get().get(i);
        if (page.isRequired() && !page.isCompleted()) {
          cutoff_page = i;
          break;
        }
      }

      if (_adapter.isPresent()) {
        if (_adapter.get().cutoffPage() != cutoff_page) _adapter.get().cutoffPage(cutoff_page);
        _adapter.get().notifyDataSetChanged();
        return true;
      }

      return false;
    }

    public class WizardAdapter extends PagerAdapter {
      private       int     _cutoff_page;
      private       View    _primary_item;
      private final Context _context;

      /* package */WizardAdapter(Context context) {
        super();
        _context = context;
      }

      @Override public Object instantiateItem(ViewGroup container, int position) {
        assert _current_page_sequence.isPresent();
        final View view =
            (position >= _current_page_sequence.get().size())? ReviewPage.createView(_context)
                                                             : _current_page_sequence.get()
                                                                                     .get(position)
                                                                                     .createView(_context);
        container.addView(view);
        return view;
      }

      @Override public void destroyItem(ViewGroup container, int _, Object object) {
        final View view = (View) object;
        container.removeView(view);
      }

      @Override public int getItemPosition(Object object) {
        // TODO: be smarter about this
        if (object == _primary_item) {
          // Re-use the current view (its position never changes)
          return POSITION_UNCHANGED;
        }

        return POSITION_NONE;
      }

      @Override public boolean isViewFromObject(View view, Object o) {
        return view == o;
      }

      @Override public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        _primary_item = (View) object;
      }

      @Override public int getCount() {
        return !_current_page_sequence.isPresent()? 0 : Math.min(_cutoff_page + 1,
                                                                 _current_page_sequence.get().size()
                                                                     + 1);
      }

      /* package */void cutoffPage(int page) {
        _cutoff_page = page < 0? Integer.MAX_VALUE : page;
      }

      public int cutoffPage() {
        return _cutoff_page;
      }
    }

    private final Flow                    _flow;
    private final Recipe                  _recipe;
    private       Optional<WizardModel>   _wizard_model;
    private       Optional<WizardAdapter> _adapter;
    /* package */ Optional<List<Page>> _current_page_sequence;
    /* package */ Optional<String>     _popup_message;
    /* package */ Optional<String>     _popup_confirm_button;
    /* package */ Optional<String>     _popup_cancel_button;
    private       boolean                               _should_consume_page_selected_event;
    private       boolean                               _is_editing_after_review;
    private       PopupPresenter<Confirmation, Boolean> _confirmation_presenter;
    private final ActionBarOwner                        _action_bar;
  }
}
