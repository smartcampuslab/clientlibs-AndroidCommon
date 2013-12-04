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
package eu.trentorise.smartcampus.android.common.geo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URLEncoder;
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
import android.location.Geocoder;
import android.media.RemoteControlClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;

import eu.trentorise.smartcampus.network.RemoteConnector;
import eu.trentorise.smartcampus.network.RemoteException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;

public class OSMGeocoder {

	private Context mContext;
	private Locale mLocale = Locale.getDefault();

	private String ENC = "UTF-8";
	private static final String PATH = "/core.geocoder/collection1/select";
	private String serverUrl;

	private static double rad(double p) {
		return p*Math.PI/180;
	}
	
	public OSMGeocoder(Context context, String serverUrl) {
		this(context, serverUrl, Locale.getDefault());
	}

	public OSMGeocoder(Context context, String serverUrl, Locale locale) {
		mContext = context;
		mLocale = locale;
		this.serverUrl = serverUrl;
	}

	private boolean isConnected() {
		NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	public List<Address> findAddressesAsync(final GeoPoint p) {
		try {
			return new AsyncTask<Void, Void, List<Address>>() {
				@Override
				protected List<Address> doInBackground(Void... params) {
					try {
						List<Address> addresses = getFromLocationSC(p.getLatitudeE6() / 1E6, p.getLongitudeE6() / 1E6, true, null);
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

	public List<OSMAddress> getFromLocationName(String address, double[] referenceLocation, Double radius, String token) throws ConnectionException, SecurityException, RemoteException, ProtocolException {
		List<OSMAddress> addrs = null;

		if (!isConnected())
			throw new ConnectionException("No connection");

		StringBuilder sb = new StringBuilder();
		sb.append(PATH);
		sb.append("?");

		try {
			String components = parseAddress(address);
			sb.append("q=" + components);
			sb.append("&wt=json&spatial=true&sfield=coordinate&sort=geodist()+asc");
			sb.append("&ft="+ URLEncoder.encode("{!geofilt}",ENC));
			if (referenceLocation != null) {
				sb.append("&pt="+URLEncoder.encode(referenceLocation[0]+","+referenceLocation[1],ENC));
			}
			if (radius != null) {
				sb.append("d="+radius);
			}
			
			JSONObject jsonObject = execute(serverUrl, sb.toString(), token);

			addrs = jsonObject2addressList(jsonObject);
		} catch (UnsupportedEncodingException e) {
			throw new ProtocolException(e.getMessage());
		} catch (IOException e) {
			throw new ProtocolException(e.getMessage());
		} catch (JSONException e) {
			throw new ProtocolException(e.getMessage());
		}

		return addrs;
	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String parseAddress(String address) throws UnsupportedEncodingException {
		String q = "";
		String[] tokens = address.split(",");
		String[] subtokens = tokens[0].split(" ");
		for (String subtoken : subtokens) {
			q += "+name:"+subtoken.trim()+" ";
		}
		if (tokens.length >= 3 && tokens[1] != null && tokens[1].trim().length() > 0) {
			q += "+housenumber:"+tokens[1].trim()+" ";
		} 
		if (tokens.length == 2 && tokens[1] != null && tokens[1].trim().length() > 0) {
			q += "+city:"+tokens[1].trim()+" ";
		}
		if (tokens.length >= 3 && tokens[2] != null && tokens[2].trim().length() > 0) {
			q += "+city:"+tokens[2].trim()+" ";
		} 
		return URLEncoder.encode(q, ENC);
	}

	public List<Address> getFromLocation(double lat, double lng, double[] referenceLocation) throws IOException {
		List<Address> addrs = new ArrayList<Address>();

		if (!isConnected())
			throw new IOException("No connection");

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(url + output);
			sb.append("?");
			sb.append("latlng=" + lat + "," + lng);
			sb.append("&");
			sb.append("sensor=true");

			JSONObject jsonObject = execute(sb.toString());

			addrs = jsonObject2addressList(jsonObject, null, null, null, filterTraversible, referenceLocation);
		} catch (Exception e) {
			// e.printStackTrace();
			// throw new IOException(e.getMessage());
			mGeocoder = new Geocoder(mContext, mLocale);
			return mGeocoder.getFromLocation(lat, lng, 10);
		}

		return addrs;
	}

	private List<OSMAddress> jsonObject2addressList(JSONObject in) throws IOException, JSONException {
		List<OSMAddress> addrs = new ArrayList<OSMAddress>();

		JSONObject jsonObject = in.getJSONObject("response");
		
		JSONArray results = jsonObject.getJSONArray("docs");
		if (results == null || results.length() < 1) {
			return addrs;
		}

		for (int i = 0; i < results.length(); i++) {
			JSONObject a = results.getJSONObject(i);
			OSMAddress address = new OSMAddress(mLocale, a);
			JSONArray aComponents = a.getJSONArray("address_components");
			addrs.add(address);
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

	private JSONObject execute(String server, String query, String token) throws SecurityException, RemoteException {
		String result = RemoteConnector.getJSON(server, query, token);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;

	}
	
	private class DistanceComparator implements Comparator<Address> {
		private double[] referenceLocation = new double[]{45.891143,11.04018};
		
		public DistanceComparator(double[] referenceLocation) {
			super();
			this.referenceLocation = referenceLocation;
		}
		@Override
		public int compare(Address lhs, Address rhs) {
			return Double.compare(distance(new double[]{lhs.getLatitude(),lhs.getLongitude()},referenceLocation), distance(new double[]{rhs.getLatitude(),rhs.getLongitude()},referenceLocation));
		}
		private double distance(double[] from, double[] to) {
			double x = (rad(to[1])-rad(from[1]))*Math.cos(rad(to[0])-rad(from[0]));
			double y = rad(to[0])-rad(from[0]);
			return 6731*Math.sqrt(x*x+y*y);
		}
		
	};

}
