/**
 *  This file is part of CPForAndroid.
 *
 *  CPForAndroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CPForAndroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CPForAndroid.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  See http://CPForAndroid.googlecode.com/ for the latest version.
 */

package org.android.CPForAndroidPlusPlus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class PopUpDialog {
	@SuppressWarnings("unused")
	private static final String tag = "PopUp";
	private final Context mCtx;
	private String _title = "";
	private String _msg = "";

	public PopUpDialog(Context ctx, String title, String msg) {
		super();
		this.mCtx = ctx;
		this._msg = msg;
		this._title = title;
	}
	
    DialogInterface.OnClickListener doneListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

	public void show() {
		final LayoutInflater factory = LayoutInflater.from(mCtx);

		View dialogView = factory.inflate(R.layout.popup, null);

		innerUpdate(dialogView);

		AlertDialog.Builder adBuilder = new AlertDialog.Builder(mCtx).setTitle(
				this._title).setIcon(android.R.drawable.ic_dialog_info)
				.setView(dialogView).setPositiveButton(android.R.string.ok, doneListener);
		adBuilder.show();

	}

	private void innerUpdate(View dialogView) {
		TextView msg_text = (TextView) dialogView.findViewById(R.id.popup_text);
		// msg
		msg_text.setText(this._msg);
		msg_text.setGravity(Gravity.CENTER);
	}

}
