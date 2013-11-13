package eu.trentorise.smartcampus.android.map;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import eu.trentorise.smartcampus.android.common.R;


public class InfoDialog extends DialogFragment {
	private Address address;
	private Activity activity;
	public final static int RESULT_SELECTED = 10;
	
	public InfoDialog(Activity activity, Address o) {
		this.address = o;
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(R.string.selected_address);
		return inflater.inflate(R.layout.infoaddressdialog, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		TextView msg = (TextView) getDialog().findViewById(R.id.mapdialog_msg);
		if (address != null)
			msg.setText(address.getAddressLine(0));
		msg.setMovementMethod(new ScrollingMovementMethod());
		Button b = (Button) getDialog().findViewById(R.id.mapdialog_cancel);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
		b = (Button) getDialog().findViewById(R.id.mapdialog_ok);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("address", address);
				data.putExtra("field", activity.getIntent().getExtras().getString("field"));
				activity.setResult(RESULT_SELECTED, data);
				activity.finish();
				getDialog().dismiss();
			}
		});

	}
}
