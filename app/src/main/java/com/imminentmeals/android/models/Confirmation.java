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
package com.imminentmeals.android.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.imminentmeals.android.guava.Optional;
import com.imminentmeals.android.ui.ConfirmationPopup;
import javax.annotation.ParametersAreNonnullByDefault;

/** Messages displayed by a {@link ConfirmationPopup}. */
@ParametersAreNonnullByDefault
public class Confirmation implements Parcelable {
  public final Optional<String> title;
  public final String           body;
  public final String           confirm;
  public final String           cancel;

  public Confirmation(String title, String body, String confirm, String cancel) {
    this.title = Optional.of(title);
    this.body = body;
    this.confirm = confirm;
    this.cancel = cancel;
  }

  public Confirmation(String body, String confirm, String cancel) {
    this.title = Optional.absent();
    this.body = body;
    this.confirm = confirm;
    this.cancel = cancel;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || ((Object) this).getClass() != o.getClass()) return false;

    final Confirmation that = (Confirmation) o;

    return body.equals(that.body)
               && cancel.equals(that.cancel)
               && confirm.equals(that.confirm)
               && ((Object) title).equals(that.title);
  }

  @Override public int hashCode() {
    int result = title.isPresent()? title.get().hashCode() : 0;
    result = _HASH_PRIME * result + body.hashCode();
    result = _HASH_PRIME * result + confirm.hashCode();
    result = _HASH_PRIME * result + cancel.hashCode();
    return result;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int _) {
    parcel.writeString(title.orNull());
    parcel.writeString(body);
    parcel.writeString(confirm);
    parcel.writeString(cancel);
  }

  @SuppressWarnings("UnusedDeclaration")
  public static final Creator<Confirmation> CREATOR = new Creator<Confirmation>() {

    @Override public Confirmation createFromParcel(Parcel parcel) {
      return new Confirmation(parcel.readString(), parcel.readString(), parcel.readString(),
                              parcel.readString());
    }

    @Override public Confirmation[] newArray(int size) {
      return new Confirmation[size];
    }
  };

  private static final int _HASH_PRIME = 31;
}
