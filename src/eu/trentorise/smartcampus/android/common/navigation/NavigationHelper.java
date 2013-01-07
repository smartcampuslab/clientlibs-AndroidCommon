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
package eu.trentorise.smartcampus.android.common.navigation;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import eu.trentorise.smartcampus.android.common.AppHelper;

public class NavigationHelper {

	public static void bringMeThere(Activity ctx, Address from, Address to) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.navigate_intent_action));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.navigate_arg_from), from);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.navigate_arg_to), to);
		AppHelper.startActivityForApp(intent, ctx);
	}

}
