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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;

public class GlobalConfig {

//	public static final String APP_HOST = "https://vas.smartcampuslab.it";
	
	private static final String P_APP_BASE_URL= "APP_BASE_URL";
	// Shared package path
	private static final String SHARED_PACKAGE = "eu.trentorise.smartcampus.launcher";
	// Name for preferences
	private static final String COMMON_PREF = "COMMON_PREF";
	// Access mode (private to application and other ones with same Shared UID)
	private static final int ACCESS = Context.MODE_PRIVATE|Context.CONTEXT_RESTRICTED;

	private static final String DEF_APP_HOST = "https://vas.smartcampuslab.it";
	private static String appUrl = null;
	
	public static void setAppUrl(Context ctx, String url) throws NameNotFoundException {
		assert url != null;
		SharedPreferences prefs = getPrefs(ctx);
		Editor edit = prefs.edit();
		edit.putString(P_APP_BASE_URL, url);
		edit.commit();
		appUrl = null;
	}
	
	public static String getAppUrl(Context ctx) throws ProtocolException {
		if (appUrl == null) {
			SharedPreferences prefs;
			try {
				prefs = getPrefs(ctx);
				appUrl = prefs.getString(P_APP_BASE_URL, null);
				// tihs is temporary: the base url should always be set
				if (appUrl == null) {
					setAppUrl(ctx, DEF_APP_HOST);
					prefs = getPrefs(ctx);
					appUrl = prefs.getString(P_APP_BASE_URL, null);
				}
			} catch (NameNotFoundException e) {
				throw new ProtocolException("No app url provided");
			}

		}
		return appUrl;
	}

	private static SharedPreferences getPrefs(Context context) throws NameNotFoundException {
		Context sharedContext = context.createPackageContext(SHARED_PACKAGE, ACCESS);
		return sharedContext.getSharedPreferences(COMMON_PREF, ACCESS);
	}

}
