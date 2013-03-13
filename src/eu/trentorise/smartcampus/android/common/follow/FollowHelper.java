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
package eu.trentorise.smartcampus.android.common.follow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import eu.trentorise.smartcampus.android.common.AppHelper;
import eu.trentorise.smartcampus.android.common.GlobalConfig;
import eu.trentorise.smartcampus.android.common.Utils;
import eu.trentorise.smartcampus.android.common.follow.model.Concept;
import eu.trentorise.smartcampus.android.common.follow.model.Topic;
import eu.trentorise.smartcampus.protocolcarrier.ProtocolCarrier;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants.Method;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageRequest;
import eu.trentorise.smartcampus.protocolcarrier.custom.MessageResponse;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class FollowHelper {

	private static final String SERVICE_TOPIC_PATH = "/smartcampus.vas.community-manager.web/eu.trentorise.smartcampus.cm.model.Topic";
	private static final List<String> CONTENT_TYPES = Arrays.asList(new String[] { "location", "event", "narrative" });

	public static void follow(Activity ctx, FollowEntityObject obj) {
		Intent intent = new Intent();
		intent.setAction(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.follow_intent_action));
		intent.putExtra(ctx.getString(eu.trentorise.smartcampus.android.common.R.string.follow_entity_arg_entity), obj);
		AppHelper.startActivityForApp(intent, ctx);
	}

	public static Topic follow(Context ctx, String appToken, String authToken, FollowEntityObject obj)
			throws ConnectionException, ProtocolException, SecurityException {
		Topic topic = convertFEOtoTopic(obj);
		topic = follow(ctx, appToken, authToken, topic);
		return topic;
	}

	private static Topic follow(Context ctx, String appToken, String authToken, Topic input) throws ConnectionException,
			ProtocolException, SecurityException {
		String json = Utils.convertToJSON(input);
		MessageRequest req = new MessageRequest(GlobalConfig.getAppUrl(ctx), SERVICE_TOPIC_PATH);
		req.setMethod(Method.POST);
		req.setBody(json);
		ProtocolCarrier mProtocolCarrier = new ProtocolCarrier(ctx, appToken);
		MessageResponse res = mProtocolCarrier.invokeSync(req, appToken, authToken);
		Topic object = Utils.convertJSONToObject(res.getBody(), input.getClass());
		if (object == null) {
			throw new ProtocolException("Cannot parse remotely created object: " + res.getBody());
		}
		return object;
	}

	private static Topic convertFEOtoTopic(FollowEntityObject follow) {
		Topic topic = new Topic();

		if (follow != null) {
			topic.setEntities(new ArrayList<Concept>());
			Concept c = new Concept();
			c.setId(follow.getEntityId());
			c.setName(follow.getTitle());
			topic.getEntities().add(c);
			topic.setName(follow.getTitle());

			topic.setAllCommunities(true);
			topic.setAllKnownCommunities(true);
			topic.setAllUsers(true);
			topic.setAllKnownUsers(true);
			topic.setContentTypes(CONTENT_TYPES);
		}

		return topic;
	}
}
