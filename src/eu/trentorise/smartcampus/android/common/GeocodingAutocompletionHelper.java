package eu.trentorise.smartcampus.android.common;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

	public GeocodingAutocompletionHelper(Context context, AutoCompleteTextView autoComplete,String region, String country, String administrativeArea) {
		this(context, autoComplete);
		this.region = region;
		this.administrativeArea = administrativeArea;
		this.country = country;
	}

	public GeocodingAutocompletionHelper(Context context, AutoCompleteTextView autoComplete) {
		super();
		autoCompleteAdapter = new ArrayAdapterNoFilter(context, R.layout.dd_list);
		autoCompleteAdapter.setNotifyOnChange(false);

		autoComplete.addTextChangedListener(this);
		autoComplete.setOnItemClickListener(this);
		autoComplete.setThreshold(THRESHOLD);
		autoComplete.setAdapter(autoCompleteAdapter);

		messageHandler = new MyMessageHandler(context);
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

		private Context context;

		public MyMessageHandler(Context context) {
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MESSAGE_TEXT_CHANGED) {
				String enteredText = (String) msg.obj;

				try {
					List<Address> response = null;
					response = new SCGeocoder(context).getFromLocationNameSC(enteredText, region, country, administrativeArea, true);
					if (response != null && !response.isEmpty()) {
						notifyResult(response);
						return;
					}
				} catch (IOException ex) {
					Log.e(getClass().getName(), "Failed to get autocomplete suggestions", ex);
				}
			}
		}
	}

	public static class ArrayAdapterNoFilter extends ArrayAdapter<String> {

		public ArrayAdapterNoFilter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
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
