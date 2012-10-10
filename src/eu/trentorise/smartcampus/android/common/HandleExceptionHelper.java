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
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public static void connectivityFailure(final Activity activity){
		Toast.makeText(activity, activity.getResources().getString(R.string.dialog_connectivity_message),Toast.LENGTH_LONG).show();
	}
}
