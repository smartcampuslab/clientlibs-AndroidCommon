package eu.trentorise.smartcampus.android.common.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ViewHelper {

	public static void viewInApp(Activity ctx, String type, long entityId, Bundle parameters) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_action));
		intent.setData(Uri.parse("eu.trentorise.smartcampus."+type+"://"+entityId));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_entity_id), entityId);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_entity_type), type);
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.view_intent_arg_data), parameters);
		ctx.startActivity(intent);
	}

}