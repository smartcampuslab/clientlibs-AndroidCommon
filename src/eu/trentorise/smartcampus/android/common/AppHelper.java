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
			Log.e(AppHelper.class.getName(),""+e.getMessage());
			Toast.makeText(ctx, ctx.getString(eu.trentorise.smartcampus.android.common.R.string.app_not_installed), Toast.LENGTH_LONG).show();
		}

	}
}
