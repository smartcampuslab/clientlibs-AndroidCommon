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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class SCAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private ProgressDialog progress = null;

	private Activity activity;
	private SCAsyncTaskProcessor<Params,Result> processor;
	private enum STATUS {OK, SECURITY, CONNECTION, FAILURE};
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
		} catch (eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException e) {
		status = STATUS.CONNECTION;
	}catch (Exception e) {
			error = e;
			status = STATUS.FAILURE;
		}
		
		return null;
	}
	
	@Override
	protected final void onPostExecute(Result result) {
		if (progress != null) {
			try {
				progress.cancel();
			} catch (Exception e) {
				Log.w(getClass().getName(),"Problem closing progress dialog: "+e.getMessage());
			}
		}
		if (status == STATUS.OK) {
			handleSuccess(result);
		}
		else if (status == STATUS.SECURITY) {
			handleSecurityError();
		}
		else if (status == STATUS.CONNECTION) {
			handleConnectionError();
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
	
	protected void handleConnectionError() {
		processor.handleConnectionError();
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
		Result performAction(Params ...params) throws eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException, eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException, Exception;
		void handleResult(Result result);
		void handleFailure(Exception e);
		void handleSecurityError();
		void handleConnectionError();
	}

}
