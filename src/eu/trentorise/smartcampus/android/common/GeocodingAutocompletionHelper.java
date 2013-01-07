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
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;

public class GeocodingAutocompletionHelper implements TextWatcher, OnItemClickListener {

	public static interface OnAddressSelectedListener {
		public void onAddressSelected(Address address);
	}

	private static final int MESSAGE_TEXT_CHANGED = 0;
	private static final int THRESHOLD = 5;

	private ArrayAdapter<String> autoCompleteAdapter = null;

	List<Address> autoCompleteSuggestionAddresses = null;

	private MyMessageHandler messageHandler = null;
	private OnAddressSelectedListener listener;
	private String region = null, country = null, administrativeArea = null;
	private Context context;

	public GeocodingAutocompletionHelper(Context context, AutoCompleteTextView autoComplete, String region, String country,
			String administrativeArea) {
		this(context, autoComplete);
		this.region = region;
		this.administrativeArea = administrativeArea;
		this.country = country;
	}

	public GeocodingAutocompletionHelper(Context context, AutoCompleteTextView autoComplete) {
		super();
		this.context = context;
		autoCompleteAdapter = new ArrayAdapterNoFilter(context, R.layout.dd_list, R.id.dd_textview);
		autoCompleteAdapter.setNotifyOnChange(false);

		autoComplete.addTextChangedListener(this);
		autoComplete.setOnItemClickListener(this);
		autoComplete.setThreshold(THRESHOLD);
		autoComplete.setAdapter(autoCompleteAdapter);

		messageHandler = new MyMessageHandler();
	}

	public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position < autoCompleteSuggestionAddresses.size()) {
			listener.onAddressSelected(autoCompleteSuggestionAddresses.get(position));
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		messageHandler.removeMessages(MESSAGE_TEXT_CHANGED);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String value = s.toString();
		if (!"".equals(value) && value.length() >= THRESHOLD) {
			Message msg = Message.obtain(messageHandler, MESSAGE_TEXT_CHANGED, value);
			messageHandler.sendMessageDelayed(msg, 500);
		} else {
			autoCompleteAdapter.clear();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	private void notifyResult(List<Address> suggestions) {
		autoCompleteSuggestionAddresses = suggestions;
		autoCompleteAdapter.clear();
		for (Address a : suggestions) {
			String s = "";
			for (int i = 0; i <= a.getMaxAddressLineIndex(); i++) {
				s += a.getAddressLine(i) + " ";
			}
			autoCompleteAdapter.add(s.trim());
		}
		autoCompleteAdapter.notifyDataSetChanged();
	}

	private class MyMessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MESSAGE_TEXT_CHANGED) {
				String enteredText = (String) msg.obj;
				new AddressAsyncTask().execute(enteredText);
			}
		}
	}

	private class AddressAsyncTask extends AsyncTask<String, Void, List<Address>> {
		@Override
		protected List<Address> doInBackground(String... params) {
			try {
				return new SCGeocoder(context).getFromLocationNameSC(params[0], region, country, administrativeArea, true);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Address> response) {
			if (response != null && !response.isEmpty()) {
				notifyResult(response);
			}
		}

	}

	public static class ArrayAdapterNoFilter extends ArrayAdapter<String> {

		public ArrayAdapterNoFilter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		private static final NoFilter NO_FILTER = new NoFilter();

		@Override
		public Filter getFilter() {
			return NO_FILTER;
		}

		private static class NoFilter extends Filter {
			protected FilterResults performFiltering(CharSequence prefix) {
				return new FilterResults();
			}

			protected void publishResults(CharSequence constraint, FilterResults results) {
				// Do nothing
			}
		}
	}
}
