package com.imminentmeals.android.utilities;

import android.app.Activity;
import javax.annotation.Nonnull;
import mortar.MortarContext;
import mortar.MortarScope;

/**
 * Collection of utilities to ease the use of Mortar.
 */
public final class MortarUtilities {

  public interface MortarApplication {

    @Nonnull MortarScope rootScope();
  }

  public static <T extends Activity & MortarContext> MortarScope rootScope(T activity) {
    assert activity.getApplication() instanceof MortarApplication;
    return ((MortarApplication) activity.getApplication()).rootScope();
  }

  public static <T extends Activity & MortarContext> boolean destroyActivityScope(T activity) {
    final MortarScope scope = activity.getMortarScope();
    if (activity.isFinishing() && scope != null) {
      scope.destroy();
      return true;
    }
    return false;
  }
}
