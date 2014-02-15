package com.imminentmeals.android;

import android.app.Application;
import com.imminentmeals.android.core.ApplicationModule;
import dagger.ObjectGraph;
import javax.annotation.Nonnull;
import mortar.Mortar;
import mortar.MortarScope;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

import static com.imminentmeals.android.utilities.MortarUtilities.MortarApplication;

public class ImminentMealsApplication extends Application implements MortarApplication {

  @Override public void onCreate() {
    super.onCreate();

    // So that exceptions thrown in RxJava onError methods don't have their stack traces swallowed.
    RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {

      @Override public void handleError(Throwable e) {
        throw new RuntimeException(e);
      }
    });

    _root_scope =
        Mortar.createRootScope(BuildConfig.DEBUG, ObjectGraph.create(new ApplicationModule()));
  }

  @Override @Nonnull public MortarScope rootScope() {
    return _root_scope;
  }

  private MortarScope _root_scope;
}
