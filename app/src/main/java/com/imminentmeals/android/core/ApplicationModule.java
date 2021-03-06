/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imminentmeals.android.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imminentmeals.android.annotations.MainThread;
import dagger.Module;
import dagger.Provides;
import flow.Parcer;
import javax.inject.Singleton;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

@Module(library = true)
public class ApplicationModule {

  @Provides @Singleton @MainThread Scheduler provideMainThread() {
    return AndroidSchedulers.mainThread();
  }

  @Provides @Singleton Gson provideGson() {
    return new GsonBuilder().create();
  }

  @Provides @Singleton Parcer<Object> provideParcer(Gson gson) {
    return new _Parcer<>(gson);
  }
}
