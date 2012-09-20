package eu.trentorise.smartcampus.android.common.follow;

import android.app.Activity;
import android.content.Intent;
import eu.trentorise.smartcampus.android.common.AppHelper;

public class FollowHelper {

	public static void follow(Activity ctx, FollowEntityObject obj) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.follow_intent_action));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.follow_entity_arg_entity), obj);
		AppHelper.startActivityForApp(intent, ctx);
	}
}
