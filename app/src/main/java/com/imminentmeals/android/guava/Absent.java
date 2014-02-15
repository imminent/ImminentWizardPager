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

/**
 * Implementation of an {@link Optional} not containing a reference.
 */
@ParametersAreNonnullByDefault
final class Absent extends Optional<Object> {
  /* package */static final Absent INSTANCE = new Absent();

  @Override public boolean isPresent() {
    return false;
  }

  @Override public Object get() {
    throw new IllegalStateException("value is absent");
  }

  @Override public Object or(Object default_value) {
    return Preconditions.checkNotNull(default_value, "use orNull() instead of or(null)");
  }

  @SuppressWarnings("unchecked") // safe covariant cast
  @Override public Optional<Object> or(Optional<?> second_choice) {
    return (Optional) Preconditions.checkNotNull(second_choice);
  }

  @Override public Object or(Supplier<?> supplier) {
    return Preconditions.checkNotNull(supplier.get(),
                                      "use orNull() instead of a Supplier that returns null");
  }

  @Override @Nullable public Object orNull() {
    return null;
  }

  @Override public Set<Object> asSet() {
    return Collections.emptySet();
  }

  @Override public boolean equals(@Nullable Object object) {
    return object == this;
  }

  @Override public int hashCode() {
    return 0x598df91c;
  }

  @Override public String toString() {
    return "Optional.absent()";
  }

  private Object readResolve() {
    return INSTANCE;
  }

  private static final long serialVersionUID = 0;
}
