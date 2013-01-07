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
package eu.trentorise.smartcampus.android.common.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import eu.trentorise.smartcampus.android.common.AppHelper;

public class ViewHelper {

	public static void viewInApp(Activity ctx, String type, long entityId, Bundle parameters) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_action));
		intent.setData(Uri.parse("eu.trentorise.smartcampus."+type+"://"+entityId));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_entity_id), entityId);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_entity_type), type);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_data), parameters);
		AppHelper.startActivityForApp(intent, ctx);
	}
	
	public static void viewInApp(Activity ctx, String type, String objectId, Bundle parameters) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_action));
		intent.setData(Uri.parse("eu.trentorise.smartcampus."+type+"://"+objectId));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_object_id), objectId);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_entity_type), type);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_data), parameters);
		AppHelper.startActivityForApp(intent, ctx);
	}

}
