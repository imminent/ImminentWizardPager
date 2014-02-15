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

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import flow.Parcer;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
/* package */class _Parcer<T> implements Parcer<T> {

  public _Parcer(Gson gson) {
    this._gson = gson;
  }

  @Override public Parcelable wrap(T instance) {
    try {
      final String json = encode(instance);
      return new Wrapper(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override public T unwrap(Parcelable parcelable) {
    final Wrapper wrapper = (Wrapper) parcelable;
    try {
      return decode(wrapper.json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String encode(T instance) throws IOException {
    final StringWriter string_writer = new StringWriter();
    final JsonWriter writer = new JsonWriter(string_writer);

    //noinspection TryFinallyCanBeTryWithResources
    try {
      final Class<?> type = instance.getClass();

      writer.beginObject();
      writer.name(type.getName());
      _gson.toJson(instance, type, writer);
      writer.endObject();

      return string_writer.toString();
    } finally {
      writer.close();
    }
  }

  private T decode(String json) throws IOException {
    JsonReader reader = new JsonReader(new StringReader(json));

    //noinspection TryFinallyCanBeTryWithResources
    try {
      reader.beginObject();

      final Class<?> type = Class.forName(reader.nextName());
      return _gson.fromJson(reader, type);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      reader.close();
    }
  }

  @ParametersAreNonnullByDefault
  private static class Wrapper implements Parcelable {
    /* package */final String json;

    /* package */Wrapper(String json) {
      this.json = json;
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      out.writeString(json);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final Parcelable.Creator<Wrapper> CREATOR = new Parcelable.Creator<Wrapper>() {

      @Override public Wrapper createFromParcel(Parcel in) {
        final String json = in.readString();
        return new Wrapper(json);
      }

      @Override public Wrapper[] newArray(int size) {
        return new Wrapper[size];
      }
    };
  }

  private final Gson _gson;
}
