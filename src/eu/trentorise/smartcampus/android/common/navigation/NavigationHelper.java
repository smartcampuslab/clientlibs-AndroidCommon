package eu.trentorise.smartcampus.android.common.navigation;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;

public class NavigationHelper {

	public static void bringMeThere(Activity ctx, Address from, Address to) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.navigate_intent_action));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.navigate_arg_from), from);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.navigate_arg_to), to);
		ctx.startActivity(intent);
	}

}
