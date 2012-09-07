package eu.trentorise.smartcampus.android.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class SCAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private ProgressDialog progress = null;

	private Activity activity;
	private SCAsyncTaskProcessor<Params,Result> processor;
	private enum STATUS {OK, SECURITY, FAILURE};
	private Exception error;
	private STATUS status = STATUS.OK;
	
	public SCAsyncTask(Activity activity, SCAsyncTaskProcessor<Params,Result> processor) {
		super();
		this.activity = activity;
		this.processor = processor;
	}

	@Override
	protected Result doInBackground(Params... params) {
		try {
			return processor.performAction(params);
		} catch (eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException e) {
			status = STATUS.SECURITY;
		} catch (Exception e) {
			error = e;
			status = STATUS.FAILURE;
		}
		
		return null;
	}
	
	@Override
	protected final void onPostExecute(Result result) {
		if (progress != null) progress.dismiss();
		if (status == STATUS.OK) {
			handleSuccess(result);
		}
		else if (status == STATUS.SECURITY) {
			handleSecurityError();
		}
		else {
			handleFailure();
		}
	}

	protected void handleFailure() {
		processor.handleFailure(error);
	}

	protected void handleSecurityError() {
		processor.handleSecurityError();
	}

	protected void handleSuccess(Result result) {
		processor.handleResult(result);
	}

	@Override
	protected void onPreExecute() {
		progress  = ProgressDialog.show(activity, "", "Loading. Please wait...", true);
		super.onPreExecute();
	}

	public interface SCAsyncTaskProcessor<Params,Result> {
		Result performAction(Params ...params) throws eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException, Exception;
		void handleResult(Result result);
		void handleFailure(Exception e);
		void handleSecurityError();
	}

}
