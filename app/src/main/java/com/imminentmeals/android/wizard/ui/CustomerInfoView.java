package com.imminentmeals.android.wizard.ui;

import android.app.Service;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.imminentmeals.android.R;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.wizard.CustomerInfoPage;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import mortar.Mortar;

/**
 * Created by dandre on 2/10/14.
 */
@ParametersAreNonnullByDefault
public class CustomerInfoView extends FrameLayout {
  @Inject /* package */                         CustomerInfoPage.Presenter presenter;
  @InjectView(android.R.id.title) /* package */ TextView                   title;
  @InjectView(R.id.your_name) /* package */     TextView                   name;
  @InjectView(R.id.your_email) /* package */    TextView                   email;

  public CustomerInfoView(Context context, AttributeSet attributes) {
    super(context, attributes);
    Mortar.inject(context, this);
    final LayoutInflater layout_inflater =
        (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    layout_inflater.inflate(R.layout.wizard_view_customer_info, this, true);
    ButterKnife.inject(this, this);
  }

  public View withKey(String key) {
    presenter.key(key);
    // Defers setting the listeners until the Presenter has been prepared
    name.addTextChangedListener(new AfterTextChangedListener() {

      @Override public void afterTextChanged(Optional<Editable> text) {
        presenter.onNameSet(text.isPresent()? Optional.of(text.get().toString())
                                            : Optional.<String>absent());
      }
    });
    email.addTextChangedListener(new AfterTextChangedListener() {

      @Override public void afterTextChanged(Optional<Editable> text) {
        presenter.onEmailSet(text.isPresent()? Optional.of(text.get().toString())
                                             : Optional.<String>absent());
      }
    });
    return this;
  }

  public void title(String title) {
    this.title.setText(title);
  }

  public void name(@Nullable String name) {
    this.name.setText(name);
  }

  public void email(@Nullable String email) {
    this.email.setText(email);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    presenter.takeView(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    presenter.dropView(this);
  }

  private static abstract class AfterTextChangedListener implements TextWatcher {

    abstract void afterTextChanged(Optional<Editable> text);

    @Override public final void beforeTextChanged(CharSequence _, int __, int ___, int ____) { }

    @Override public final void onTextChanged(CharSequence _, int __, int ___, int ____) { }

    @Override public final void afterTextChanged(Editable text) {
      afterTextChanged(Optional.fromNullable(text));
    }
  }
}
