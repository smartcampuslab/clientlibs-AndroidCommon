/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.android.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AppHelper {

	public static void startActivityForApp(Intent intent, Activity ctx) {
		try {
			ctx.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(AppHelper.class.getName(), "" + e.getMessage());
			Toast.makeText(
					ctx,
					ctx.getString(eu.trentorise.smartcampus.android.common.R.string.app_not_installed),
					Toast.LENGTH_LONG).show();
		}
	}

	public static void startActivityForResultForApp(Intent intent,
			Activity ctx, int requestCode) {
		try {
			ctx.startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException e) {
			Log.e(AppHelper.class.getName(), "" + e.getMessage());
			Toast.makeText(
					ctx,
					ctx.getString(eu.trentorise.smartcampus.android.common.R.string.app_not_installed),
					Toast.LENGTH_LONG).show();
		}
	}

	public static void startActivityForResultForApp(Intent intent,
			android.support.v4.app.Fragment ctx, int requestCode) {
		try {
			ctx.startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException e) {
			Log.e(AppHelper.class.getName(), "" + e.getMessage());
			Toast.makeText(
					ctx.getActivity(),
					ctx.getString(eu.trentorise.smartcampus.android.common.R.string.app_not_installed),
					Toast.LENGTH_LONG).show();
		}
	}

}
