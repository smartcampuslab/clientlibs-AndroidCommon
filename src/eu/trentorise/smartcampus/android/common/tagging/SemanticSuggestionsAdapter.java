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
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import eu.trentorise.smartcampus.android.common.R;
import eu.trentorise.smartcampus.android.common.tagging.TaggingDialog.TagProvider;

public class SemanticSuggestionsAdapter extends ArrayAdapter<SemanticSuggestion> {
	private Context context;
	private int layoutResourceId;

    private List<SemanticSuggestion> mSubData = new ArrayList<SemanticSuggestion>();
	private TagProvider provider;
    static int counter=0;

	public SemanticSuggestionsAdapter(Context context, int layoutResourceId, TagProvider provider) {
		super(context, layoutResourceId);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.provider = provider;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
		}

		SemanticSuggestion s = getItem(position);
		if (s != null) {
			TextView word = (TextView) row.findViewById(R.id.semanticTagWord);
			if (word != null)
				word.setText(s.getName());
			TextView descr = (TextView) row.findViewById(R.id.semanticTagDescr);
			if (descr != null)
				descr.setText(s.getDescription());
		}
		return row;
	}
	
	
	@Override
	public Filter getFilter() {
		return filter;
	}

	@Override
	public int getCount() {
		return mSubData.size();
	}
	
	@Override
	public SemanticSuggestion getItem(int index) {
	    return mSubData.get(index);
	}

	final Filter filter = new Filter() {
        private int c = ++counter;
        private List<SemanticSuggestion> mData = new ArrayList<SemanticSuggestion>();

        @Override
		protected FilterResults performFiltering(CharSequence constraint) {
            mData.clear();

            FilterResults filterResults = new FilterResults();
            if(constraint != null) {
              try {
            	  mData.addAll(provider.getTags(constraint));
              }
              catch(Exception e) {
              }

              filterResults.values = mData;
              filterResults.count = mData.size();
            }
            return filterResults;		
        }

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if(c == counter) {
	            mSubData.clear();
	              if(results != null && results.count > 0) {
	                ArrayList<SemanticSuggestion> objects = (ArrayList<SemanticSuggestion>)results.values;
	                for (SemanticSuggestion v : objects) mSubData.add(v);
	                notifyDataSetChanged();
	              }
	              else {
	                notifyDataSetInvalidated();
	              }
	          }
		}
		
	};
}
