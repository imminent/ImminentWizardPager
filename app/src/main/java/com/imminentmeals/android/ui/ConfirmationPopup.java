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
package com.imminentmeals.android.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.imminentmeals.android.models.Confirmation;
import mortar.Popup;
import mortar.PopupPresenter;

public class ConfirmationPopup implements Popup<Confirmation, Boolean> {

  public ConfirmationPopup(Context context) {
    this._context = context;
  }

  @Override public Context getContext() {
    return _context;
  }

  @Override public void show(Confirmation info, boolean _,
                             final PopupPresenter<Confirmation, Boolean> presenter) {
    if (_dialog != null) throw new IllegalStateException("Already showing, can't show " + info);

    _dialog = new AlertDialog.Builder(_context).setTitle(info.title.orNull())
                                               .setMessage(info.body)
                                               .setPositiveButton(info.confirm,
                                                                  new DialogInterface.OnClickListener() {

                                                                    @Override
                                                                    public void onClick(DialogInterface _,
                                                                                        int __) {
                                                                      _dialog = null;
                                                                      presenter.onDismissed(true);
                                                                    }
                                                                  })
                                               .setNegativeButton(info.cancel,
                                                                  new DialogInterface.OnClickListener() {

                                                                    @Override
                                                                    public void onClick(DialogInterface _,
                                                                                        int __) {
                                                                      _dialog = null;
                                                                      presenter.onDismissed(false);
                                                                    }
                                                                  })
                                               .setCancelable(true)
                                               .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                                 @Override public void onCancel(DialogInterface d) {
                                                   _dialog = null;
                                                   presenter.onDismissed(false);
                                                 }
                                               })
                                               .show();
  }

  @Override public boolean isShowing() {
    return _dialog != null;
  }

  @Override public void dismiss(boolean _) {
    _dialog.dismiss();
    _dialog = null;
  }

  private final Context     _context;
  private       AlertDialog _dialog;
}
