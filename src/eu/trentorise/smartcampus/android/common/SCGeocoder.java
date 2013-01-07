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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;

public class SCGeocoder {

	private Context mContext;
	private Locale mLocale = Locale.getDefault();
	private Geocoder mGeocoder;

	private String ENC = "UTF-8";
	private String url = "https://maps.googleapis.com/maps/api/geocode/";
	private String output = "json";

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

	public List<Address> findAddressesAsync(final GeoPoint p) {
		try {
			return new AsyncTask<Void, Void, List<Address>>() {
				@Override
				protected List<Address> doInBackground(Void... params) {
					try {
						List<Address> addresses = getFromLocationSC(p.getLatitudeE6() / 1E6, p.getLongitudeE6() / 1E6, true);
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
			boolean sensor) throws IOException {
		List<Address> addrs = new ArrayList<Address>();

		if (!isConnected())
			throw new IOException("No connection");

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(url + output);
			sb.append("?");

			sb.append("address=" + URLEncoder.encode(address, ENC));

			if (isValid(region)) {
				sb.append("&");
				sb.append("region=" + region);
			}

			if (isValid(country) || isValid(administrativeArea)) {
				sb.append("&");
				sb.append("components=");
				if (isValid(country)) {
					sb.append("country:" + country);
				}
				if (isValid(administrativeArea)) {
					if (isValid(country)) {
						sb.append(URLEncoder.encode("|", ENC));
					}
					sb.append("administrative_area:" + administrativeArea);
				}
			}

			sb.append("&");
			sb.append("sensor=" + sensor);

			JSONObject jsonObject = execute(sb.toString());

			addrs = jsonObject2addressList(jsonObject);
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new IOException(e.getMessage());
			mGeocoder = new Geocoder(mContext, mLocale);
			return mGeocoder.getFromLocationName(address, 10);
		}

		return addrs;
	}

	public List<Address> getFromLocationSC(double lat, double lng, boolean sensor) throws IOException {
		List<Address> addrs = new ArrayList<Address>();

		if (!isConnected())
			throw new IOException("No connection");

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(url + output);
			sb.append("?");
			sb.append("latlng=" + lat + "," + lng);
			sb.append("&");
			sb.append("sensor=" + sensor);

			JSONObject jsonObject = execute(sb.toString());

			addrs = jsonObject2addressList(jsonObject);
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new IOException(e.getMessage());
			mGeocoder = new Geocoder(mContext, mLocale);
			return mGeocoder.getFromLocation(lat, lng, 10);
		}

		return addrs;
	}

	private List<Address> jsonObject2addressList(JSONObject jsonObject) throws IOException, JSONException {
		List<Address> addrs = new ArrayList<Address>();

		if (!jsonObject.getString("status").equalsIgnoreCase("ok")) {
			return addrs;
		}

		JSONArray results = jsonObject.getJSONArray("results");
		if (results == null || results.length() < 1) {
			return addrs;
		}

		for (int i = 0; i < results.length(); i++) {
			Address address = new Address(mLocale);
			JSONObject a = results.getJSONObject(i);
			address.setAddressLine(0, a.getString("formatted_address"));

			JSONArray aComponents = a.getJSONArray("address_components");
			for (int j = 0; j < aComponents.length(); j++) {
				JSONObject comp = aComponents.getJSONObject(j);

				if (comp.getJSONArray("types").toString().contains("locality")) {
					address.setLocality(comp.getString("long_name"));
				} else if (comp.getJSONArray("types").toString().contains("administrative_area_level_2")) {
					address.setSubAdminArea(comp.getString("long_name"));
				} else if (comp.getJSONArray("types").toString().contains("administrative_area_level_1")) {
					address.setAdminArea(comp.getString("long_name"));
				} else if (comp.getJSONArray("types").toString().contains("country")) {
					address.setCountryName(comp.getString("long_name"));
					address.setCountryCode(comp.getString("short_name"));
				} else if (comp.getJSONArray("types").toString().contains("postal_code")) {
					address.setPostalCode(comp.getString("long_name"));
				}
			}

			String lat = a.getJSONObject("geometry").getJSONObject("location").getString("lat");
			address.setLatitude(Double.parseDouble(lat));
			String lng = a.getJSONObject("geometry").getJSONObject("location").getString("lng");
			address.setLongitude(Double.parseDouble(lng));

			addrs.add(address);
		}

		return addrs;
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
			// TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO
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
}
