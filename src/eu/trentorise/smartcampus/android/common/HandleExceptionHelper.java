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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class HandleExceptionHelper {
	
	public static void showFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id),
				Toast.LENGTH_LONG).show();
	}
	
	public static void showDialogConnectivity(final Activity activity){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.dialog_connectivity_message)
		       .setTitle(R.string.dialog_connectivity_title);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           //if OK open settings activity
		        	   Intent i = new Intent( android.provider.Settings.ACTION_SETTINGS);
		        	   activity.startActivity(i);
		           }
		       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
			           //if OK open settings activity
		        	   dialog.dismiss();
		        	   
		           }
		       });

		// Create the AlertDialog
		if (!activity.isFinishing()) {
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
	
	public static void connectivityFailure(final Activity activity){
		if (!activity.isFinishing()) {
			Toast.makeText(activity, activity.getResources().getString(R.string.dialog_connectivity_message),Toast.LENGTH_LONG).show();
		}
	}
}
