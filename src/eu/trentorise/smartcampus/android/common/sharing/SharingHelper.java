package eu.trentorise.smartcampus.android.common.sharing;

import android.app.Activity;
import android.content.Intent;
import eu.trentorise.smartcampus.android.common.AppHelper;

public class SharingHelper {

	public static void follow(Activity ctx, ShareEntityObject obj) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.share_intent_action));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.share_entity_arg_entity), obj);
		AppHelper.startActivityForApp(intent, ctx);
	}
}
