/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imminentmeals.android.guava;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.imminentmeals.android.guava.Preconditions.checkNotNull;

/**
 * Implementation of an {@link Optional} containing a reference.
 */
@ParametersAreNonnullByDefault
final class Present<T> extends Optional<T> {

  /* package */Present(T reference) {
    _reference = reference;
  }

  @Override public boolean isPresent() {
    return true;
  }

  @Override public T get() {
    return _reference;
  }

  @Override public T or(T default_value) {
    checkNotNull(default_value, "use Optional.orNull() instead of Optional.or(null)");
    return _reference;
  }

  @Override public Optional<T> or(Optional<? extends T> second_choice) {
    checkNotNull(second_choice);
    return this;
  }

  @Override public T or(Supplier<? extends T> supplier) {
    checkNotNull(supplier);
    return _reference;
  }

  @Override public T orNull() {
    return _reference;
  }

  @Override public Set<T> asSet() {
    return Collections.singleton(_reference);
  }

  @Override public boolean equals(@Nullable Object object) {
    if (object instanceof Present) {
      final Present<?> other = (Present<?>) object;
      return _reference.equals(other._reference);
    }
    return false;
  }

  @Override public int hashCode() {
    return 0x598df91c + _reference.hashCode();
  }

  @Override public String toString() {
    return "Optional.of(" + _reference + ")";
  }

  private final T _reference;
  private static final long serialVersionUID = 0;
}
