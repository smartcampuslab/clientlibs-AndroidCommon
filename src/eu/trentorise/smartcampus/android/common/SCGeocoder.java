package eu.trentorise.smartcampus.android.common;

import java.io.IOException;
import java.io.InputStream;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class SCGeocoder {

	private Context mContext;
	private Locale mLocale = Locale.getDefault();
	private Geocoder mGeocoder;

	private String ENC = "UTF-8";
	private String url = "https://maps.googleapis.com/maps/api/geocode/";
	private String output = "json";

	public SCGeocoder(Context context) {
		mContext = context;
		mGeocoder = new Geocoder(mContext, mLocale);
	}
	
	public SCGeocoder(Context context, Locale locale) {
		mContext = context;
		mLocale = locale;
		mGeocoder = new Geocoder(mContext, mLocale);
	}

	public List<Address> getFromLocation(double latitude, double longitude, int maxResults) throws IOException {
		return mGeocoder.getFromLocation(latitude, longitude, maxResults);
	}

	public List<Address> getFromLocationName(String locationName, int maxResults) throws IOException {
		return mGeocoder.getFromLocationName(locationName, maxResults);
	}

	public List<Address> getFromLocationName(String locationName, int maxResults, double lowerLeftLatitude, double lowerLeftLongitude,
			double upperRightLatitude, double upperRightLongitude) throws IOException {
		return mGeocoder.getFromLocationName(locationName, maxResults, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude,
				upperRightLongitude);
	}

	public List<Address> getFromLocationNameSC(String address, String region, String country, String administrativeArea, boolean sensor)
			throws IOException {
		List<Address> addrs = new ArrayList<Address>();

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

			SCGeocoderAsyncTask scgat = new SCGeocoderAsyncTask();
			JSONObject jsonObject = scgat.execute(sb.toString()).get();

			addrs = jsonObject2addressList(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

		return addrs;
	}

	public List<Address> getFromLocationSC(double lat, double lng, boolean sensor) throws IOException {
		List<Address> addrs = new ArrayList<Address>();

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(url + output);
			sb.append("?");
			sb.append("latlng=" + lat + "," + lng);
			sb.append("&");
			sb.append("sensor=" + sensor);

			SCGeocoderAsyncTask scgat = new SCGeocoderAsyncTask();
			JSONObject jsonObject;
			jsonObject = scgat.execute(sb.toString()).get();

			addrs = jsonObject2addressList(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
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

	private class SCGeocoderAsyncTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... strings) {
			HttpGet httpGet = new HttpGet(strings[0]);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
			} catch (ClientProtocolException e) {
				// TODO
				e.printStackTrace();
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = new JSONObject(stringBuilder.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return jsonObject;
		}
	}
}
