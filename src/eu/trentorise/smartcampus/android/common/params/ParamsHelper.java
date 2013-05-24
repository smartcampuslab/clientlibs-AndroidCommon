package eu.trentorise.smartcampus.android.common.params;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import eu.trentorise.smartcampus.android.common.Utils;

public  class ParamsHelper {

//private static final String FILE_NAME = "params.json";
private Map<Object,Object> paramsAsset;
private static Context mContext;
private static ParamsHelper instance = null;
private static String filename;


public static void init(Context mContext, String file_name) {
	if (instance == null){
		instance = new ParamsHelper(mContext,file_name);
		filename = file_name;
	}
}
protected ParamsHelper(Context mContext, String file_name) {
	super();
	this.mContext = mContext;
	loadParamsFromFile(file_name);
}

private void loadParamsFromFile(String file_name) {
	AssetManager assetManager = mContext.getResources().getAssets();
	try {
		InputStream in = assetManager.open(file_name);
		String jsonParams = getStringFromInputStream(in);
		this.paramsAsset = Utils.convertJSONToObject(jsonParams, Map.class);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

private static String getStringFromInputStream(InputStream is) {
	 
	BufferedReader br = null;
	StringBuilder sb = new StringBuilder();

	String line;
	try {

		br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	return sb.toString();

}
private static ParamsHelper getInstance()  {
	if (instance == null)
		new ParamsHelper(mContext,filename);
	return instance;
}

public static Map<Object, Object> getParamsAsset() {
		return getInstance().paramsAsset;
	
}
public static void setParamsAsset(Map<Object, Object> paramsAsset) {
		getInstance().paramsAsset = paramsAsset;

}


}
