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
