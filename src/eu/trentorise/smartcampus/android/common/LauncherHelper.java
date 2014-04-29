package eu.trentorise.smartcampus.android.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class LauncherHelper {
	
	public static boolean isLauncherInstalled(final Activity act, boolean wantDialog){
		final String pn = "eu.trentorise.smartcampus.launcher";
		ApplicationInfo info = null;
		PackageManager pm = act.getPackageManager();
		try {
			info = pm.getApplicationInfo(pn, 0);
			return true;
		} catch (NameNotFoundException e) {
			AlertDialog.Builder build = new AlertDialog.Builder(act);
			build.setTitle(act.getString(R.string.launcher_needed_title))
				.setMessage(act.getString(R.string.launcher_needed_msg))
				.setNeutralButton(act.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						act.finish();
					}
				})
				.setPositiveButton(act.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
						    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pn)));
						} catch (android.content.ActivityNotFoundException anfe) {
						    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pn)));
						}
					}
				});
			build.create().show();
		}
		// Retrieves null or the info referred to a particular application
		return false;
	}

}
