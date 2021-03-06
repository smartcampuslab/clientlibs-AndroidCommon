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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.android.common.R;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion.TYPE;

public class TagListAdapter extends ArrayAdapter<SemanticSuggestion> {

	Context context;
	int layoutResourceId;

	public TagListAdapter(Context context, int layoutResourceId, ArrayList<SemanticSuggestion> initial) {
		super(context, layoutResourceId, initial);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DataHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new DataHolder();
			holder.content_word = (TextView) row.findViewById(R.id.semanticTagWord);
			holder.content_descr = (TextView) row.findViewById(R.id.semanticTagDescr);
			holder.content_action = (TextView) row.findViewById(R.id.semanticTagButton);

			row.setTag(holder);
		} else {
			holder = (DataHolder) row.getTag();
		}

		SemanticSuggestion content = getItem(position);
		holder.content_action.setTag(content);
		holder.content_word.setText(content.getName());
		if (content.getType() != TYPE.KEYWORD && content.getDescription() != null) {
			holder.content_descr.setText(content.getDescription());
		} else {
			holder.content_descr.setText("");
		}

		holder.content_action.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				remove((SemanticSuggestion)v.getTag());
				notifyDataSetChanged();
			}
		});
		
		return row;
	}

	static class DataHolder {
		TextView content_word;
		TextView content_descr;
		TextView content_action;
		SemanticSuggestion tag;
	}
}
