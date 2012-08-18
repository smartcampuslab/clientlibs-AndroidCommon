package eu.trentorise.smartcampus.android.common.tagging;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import eu.trentorise.smartcampus.android.common.Utils;
import eu.trentorise.smartcampus.protocolcarrier.ProtocolCarrier;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class SuggestionHelper {

	public static final String SERVICE = "/smartcampus.vas.community-manager.web";
	
	public static List<SemanticSuggestion> getSuggestions(CharSequence suggest, Context ctx, String host, String authToken, String appToken) throws ConnectionException, ProtocolException, SecurityException {
		MessageRequest request = new MessageRequest(host, SERVICE + "/search/conceptSuggest");
		request.setMethod(Method.GET);
		request.setQuery("q=" + suggest);
		MessageResponse response = new ProtocolCarrier(ctx, appToken).invokeSync(request, appToken, authToken);
		String body = response.getBody();

		if (body == null || body.trim().length() == 0) {
			return Collections.emptyList();
		}
		return Utils.convertJSONToObjects(body, SemanticSuggestion.class);

	}
}
