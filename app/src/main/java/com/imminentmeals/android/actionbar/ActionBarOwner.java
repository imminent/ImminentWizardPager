package com.imminentmeals.android.actionbar;

import android.app.ActionBar;
import android.os.Bundle;
import com.imminentmeals.android.guava.Optional;
import javax.annotation.Nonnegative;
import javax.annotation.ParametersAreNonnullByDefault;
import mortar.MortarContext;
import mortar.MortarScope;
import mortar.Presenter;
import rx.util.functions.Action0;

/** Allows shared configuration of the Android ActionBar. */
@ParametersAreNonnullByDefault
public class ActionBarOwner extends Presenter<ActionBarOwner.View> {

  /** {@link ActionBar} View interface, used for setting properties of the ActionBar display */
  @ParametersAreNonnullByDefault
  public interface View extends MortarContext {

    /** Shows and enables the home icon in the {@link ActionBar} */
    void showHomeIcon();

    /** Hides and disables the home icon in the {@link ActionBar} */
    void hideHomeIcon();

    /** Shows the Up navigation button in the {@link ActionBar} */
    void showUpButton();

    /** Hides the Up navigation button in the {@link ActionBar} */
    void hideUpButton();

    /** Sets the {@link ActionBar} title */
    void title(Optional<CharSequence> title);

    /** Sets the {@link ActionBar} title */
    void title(@Nonnegative int title);

    /** Sets the menu action */
    void menu(Optional<MenuAction> action);
  }

  @SuppressWarnings("UnusedDeclaration") @ParametersAreNonnullByDefault
  public static class Config {
    public final boolean                is_home_icon_shown;
    public final boolean                is_up_button_shown;
    public final Optional<CharSequence> title;
    public final Optional<MenuAction>   action;

    public Config(boolean show_home_icon, boolean show_up_button, CharSequence title,
                  MenuAction action) {
      this(show_home_icon, show_up_button, Optional.of(title), Optional.of(action));
    }

    public Config(boolean show_home_icon, boolean show_up_button, CharSequence title) {
      this(show_home_icon, show_up_button, Optional.of(title), Optional.<MenuAction>absent());
    }

    public Config(boolean show_home_icon, boolean show_up_button, MenuAction action) {
      this(show_home_icon, show_up_button, Optional.<CharSequence>absent(), Optional.of(action));
    }

    public Config(boolean show_home_icon, boolean show_up_button) {
      this(show_home_icon, show_up_button, Optional.<CharSequence>absent(),
           Optional.<MenuAction>absent());
    }

    public Config withAction(MenuAction action) {
      return new Config(is_home_icon_shown, is_up_button_shown, title, Optional.of(action));
    }

    public Config withNoAction() {
      return new Config(is_home_icon_shown, is_up_button_shown, title,
                        Optional.<MenuAction>absent());
    }

    private Config(boolean show_home_icon, boolean show_up_button, Optional<CharSequence> title,
                   Optional<MenuAction> action) {
      this.is_home_icon_shown = show_home_icon;
      this.is_up_button_shown = show_up_button;
      this.title = title;
      this.action = action;
    }
  }

  @ParametersAreNonnullByDefault
  public static class MenuAction {
    public final CharSequence title;
    public final Action0      action;

    public MenuAction(CharSequence title, Action0 action) {
      this.title = title;
      this.action = action;
    }
  }

  /* package */ActionBarOwner() {
    _config = Optional.absent();
  }

  @Override public void onLoad(Bundle icicle) {
    super.onLoad(icicle);
    if (_config.isPresent()) update();
  }

  @SuppressWarnings("UnusedDeclaration") public void config(Config config) {
    _config = Optional.of(config);
    update();
  }

  @SuppressWarnings("UnusedDeclaration") public Config config() {
    return _config.get();
  }

  @Override protected MortarScope extractScope(View view) {
    return view.getMortarScope();
  }

  private void update() {
    assert _config.isPresent();
    final Optional<View> view = Optional.fromNullable(getView());
    if (!view.isPresent()) return;

    if (_config.get().is_home_icon_shown) { view.get().showHomeIcon(); } else {
      view.get().hideHomeIcon();
    }

    if (_config.get().is_up_button_shown) { view.get().showUpButton(); } else {
      view.get().hideUpButton();
    }

    view.get().title(_config.get().title);
    view.get().menu(_config.get().action);
  }

  private Optional<Config> _config;
}
