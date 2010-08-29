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

package com.android.CPForAndroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class AboutDialog {
	@SuppressWarnings("unused")
	private static final String tag = "AboutDialog";
	private final Context mCtx;

	public AboutDialog(Context ctx) {
		super();
		this.mCtx = ctx;
	}

	public void show() {
		final LayoutInflater factory = LayoutInflater.from(mCtx);

		View dialogView = factory.inflate(R.layout.about, null);

		innerUpdate(dialogView);

		AlertDialog.Builder adBuilder = new AlertDialog.Builder(mCtx).setTitle(
				R.string.about).setIcon(android.R.drawable.ic_dialog_info)
				.setView(dialogView);
		adBuilder.show();

	}

	private void innerUpdate(View dialogView) {
		TextView appName = (TextView) dialogView.findViewById(R.id.app_name);
		TextView author = (TextView) dialogView.findViewById(R.id.author);
		TextView graphics_byline = (TextView) dialogView.findViewById(R.id.graphics_byline);
		TextView license = (TextView) dialogView.findViewById(R.id.license);
		TextView whatIsThis = (TextView) dialogView.findViewById(R.id.what_is_this);
		TextView website = (TextView) dialogView.findViewById(R.id.website);
		TextView email = (TextView) dialogView.findViewById(R.id.email);

		// app name & version
		String appText = Utils.getAppName(mCtx, mCtx.getPackageName()) + " v"
				+ Utils.getAppVersionName(mCtx, mCtx.getPackageName());
		appName.setText(appText);

		// author
		author.setText(R.string.by_jps);

		// license
		license.setText(R.string.license);

		// text
		whatIsThis.setText(R.string.about_text);


		// website
		website.setText(mCtx.getString(R.string.website) + "\n\n"
				+ mCtx.getString(R.string.website_url));

		// email
		email.setText(mCtx.getString(R.string.contact) + "\n\n"
				+ mCtx.getString(R.string.email_addr));
	}

}
