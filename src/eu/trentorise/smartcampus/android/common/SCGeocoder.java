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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import eu.trentorise.smartcampus.android.common.geo.OSMAddress;
import eu.trentorise.smartcampus.android.common.geo.OSMGeocoder;

public class SCGeocoder {

	private Context mContext;
	private Locale mLocale = Locale.getDefault();

	private String url = "https://geo.smartcommunitylab.it";

	private static final Set<String> ADMIN_AREA_TYPES = new HashSet<String>();
	static {
		ADMIN_AREA_TYPES.add("administrative_area_level_2");
		ADMIN_AREA_TYPES.add("political");
	}

	private static double rad(double p) {
		return p * Math.PI / 180;
	}

	private static Set<String> nonTraversibleTypes = new HashSet<String>(Arrays.asList(new String[] { "establishment",
			"transit_station", "train_station", "bus_station" }));

	public SCGeocoder(Context context) {
		mContext = context;
	}

	public SCGeocoder(Context context, Locale locale) {
		mContext = context;
		mLocale = locale;
	}

	private boolean isConnected() {
		NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	// public List<Address> getFromLocation(double latitude, double longitude,
	// int maxResults) throws IOException {
	// return mGeocoder.getFromLocation(latitude, longitude, maxResults);
	// }

	// public List<Address> getFromLocationName(String locationName, int
	// maxResults) throws IOException {
	// return mGeocoder.getFromLocationName(locationName, maxResults);
	// }

	// public List<Address> getFromLocationName(String locationName, int
	// maxResults, double lowerLeftLatitude, double lowerLeftLongitude,
	// double upperRightLatitude, double upperRightLongitude) throws IOException
	// {
	// return mGeocoder.getFromLocationName(locationName, maxResults,
	// lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude,
	// upperRightLongitude);
	// }

	public List<Address> findAddressesAsync(final Location l) {
		try {
			return new AsyncTask<Void, Void, List<Address>>() {
				@Override
				protected List<Address> doInBackground(Void... params) {
					try {
						List<Address> addresses = getFromLocationSC(l.getLatitude(), l.getLongitude(), true, null);
						return addresses;
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}
			}.execute().get();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Address> getFromLocationNameSC(String address, String region, String country, String administrativeArea,
			boolean filterTraversible, double[] referenceLocation) throws IOException {
		List<Address> addrs = new ArrayList<Address>();

		if (!isConnected())
			throw new IOException("No connection");

		try {
			List<OSMAddress> list = new OSMGeocoder(mContext, url).getFromLocationName(address, referenceLocation, 25d, null);
			for (OSMAddress a : list) {
				addrs.add(a.toAddress());
			}
		} catch (Exception e) {
			Log.e(getClass().getName(), "" + e.getMessage());
			return Collections.emptyList();
		}
		return addrs;
	}

	public List<Address> getFromLocationSC(double lat, double lng, boolean filterTraversible, double[] referenceLocation)
			throws IOException {
		List<Address> addrs = new ArrayList<Address>();

		if (!isConnected())
			throw new IOException("No connection");

		try {
			List<OSMAddress> list = new OSMGeocoder(mContext, url).getFromLocation(lat, lng, null);
			for (OSMAddress a : list) {
				addrs.add(a.toAddress());
			}
		} catch (Exception e) {
			Log.e(getClass().getName(), "" + e.getMessage());
			return Collections.emptyList();
		}

		return addrs;
	}

	private List<Address> jsonObject2addressList(JSONObject jsonObject, String region, String country,
			String administrativeArea, boolean filterTraversible, double[] referenceLocation) throws IOException, JSONException {
		List<Address> addrs = new ArrayList<Address>();

		if (!jsonObject.getString("status").equalsIgnoreCase("ok")) {
			return addrs;
		}

		JSONArray results = jsonObject.getJSONArray("results");
		if (results == null || results.length() < 1) {
			return addrs;
		}

		Set<String> typeSet = new HashSet<String>();
		if (results.length() == 1 && administrativeArea != null) {
			fillTypesSet(typeSet, results.getJSONObject(0));
			// the only result corresponding to the area itself, not relevant
			if (ADMIN_AREA_TYPES.containsAll(typeSet)) {
				return Collections.emptyList();
			}
		}

		for (int i = 0; i < results.length(); i++) {
			Address address = new Address(mLocale);
			JSONObject a = results.getJSONObject(i);
			address.setAddressLine(0, a.getString("formatted_address"));

			if (filterTraversible) {
				fillTypesSet(typeSet, a);
				if (nonTraversibleTypes.containsAll(typeSet)) {
					continue;
				}
			}

			JSONArray aComponents = a.getJSONArray("address_components");

			for (int j = 0; j < aComponents.length(); j++) {
				JSONObject comp = aComponents.getJSONObject(j);
				String compTypeString = comp.getJSONArray("types").toString();
				if (compTypeString.contains("locality")) {
					address.setLocality(comp.getString("long_name"));
				} else if (compTypeString.contains("administrative_area_level_2")) {
					address.setSubAdminArea(comp.getString("long_name"));
				} else if (compTypeString.contains("administrative_area_level_1")) {
					address.setAdminArea(comp.getString("long_name"));
				} else if (compTypeString.contains("country")) {
					address.setCountryName(comp.getString("long_name"));
					address.setCountryCode(comp.getString("short_name"));
				} else if (compTypeString.contains("postal_code")) {
					address.setPostalCode(comp.getString("long_name"));
				}
			}

			String lat = a.getJSONObject("geometry").getJSONObject("location").getString("lat");
			address.setLatitude(Double.parseDouble(lat));
			String lng = a.getJSONObject("geometry").getJSONObject("location").getString("lng");
			address.setLongitude(Double.parseDouble(lng));

			addrs.add(address);
		}

		if (referenceLocation != null) {
			Collections.sort(addrs, new DistanceComparator(referenceLocation));
		}

		return addrs;
	}

	/**
	 * @param typeSet
	 * @param typesArray
	 * @throws JSONException
	 */
	protected void fillTypesSet(Set<String> typeSet, JSONObject a) throws JSONException {
		JSONArray typesArray = a.getJSONArray("types");
		typeSet.clear();
		if (typesArray != null) {
			for (int k = 0; k < typesArray.length(); k++) {
				typeSet.add(typesArray.getString(k));
			}
		}
	}

	private boolean isValid(String s) {
		return (s != null && s.length() > 0);
	}

	private JSONObject execute(String query) {
		HttpGet httpGet = new HttpGet(query);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		String json = "";

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			json = EntityUtils.toString(entity, HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			// TODO
			e.printStackTrace();
		}

		return jsonObject;

	}

	private class DistanceComparator implements Comparator<Address> {
		private double[] referenceLocation = new double[] { 45.891143, 11.04018 };

		public DistanceComparator(double[] referenceLocation) {
			super();
			this.referenceLocation = referenceLocation;
		}

		@Override
		public int compare(Address lhs, Address rhs) {
			return Double.compare(distance(new double[] { lhs.getLatitude(), lhs.getLongitude() }, referenceLocation),
					distance(new double[] { rhs.getLatitude(), rhs.getLongitude() }, referenceLocation));
		}

		private double distance(double[] from, double[] to) {
			double x = (rad(to[1]) - rad(from[1])) * Math.cos(rad(to[0]) - rad(from[0]));
			double y = rad(to[0]) - rad(from[0]);
			return 6731 * Math.sqrt(x * x + y * y);
		}

	};

}
