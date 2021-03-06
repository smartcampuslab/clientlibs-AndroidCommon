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
import java.util.Collection;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import eu.trentorise.smartcampus.android.common.R;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion.TYPE;

public class TaggingDialog extends Dialog {
	
	protected static final int THRESHOLD = 3;

	private OnTagsSelectedListener listener;
	
	private MultiAutoCompleteTextView auto;
	private TagListAdapter tagListAdapter;
	private SemanticSuggestionsAdapter suggestionAdapter;
	private TagProvider provider;

	private ArrayList<SemanticSuggestion> init;
	
	public interface OnTagsSelectedListener {
		public void onTagsSelected(Collection<SemanticSuggestion> suggestions);
	}
	public interface TagProvider {
		public List<SemanticSuggestion> getTags(CharSequence text);
	}

	public TaggingDialog(Context context, OnTagsSelectedListener listener, TagProvider provider) {
		super(context);
		this.listener = listener;
		this.provider = provider;
	}
	public TaggingDialog(Context context, OnTagsSelectedListener listener, TagProvider provider, ArrayList<SemanticSuggestion> init) {
		super(context);
		this.listener = listener;
		this.provider = provider;
		this.init = init;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.tag_dialog_layout);
		setTitle(R.string.tag_dialog_title);

		tagListAdapter = new TagListAdapter(getContext(), R.layout.added_semantic_tag_list_row_layout, init);
		ListView list = (ListView)findViewById(R.id.tag_list);
		list.setAdapter(tagListAdapter);
		
		if (init != null) {
			tagListAdapter.notifyDataSetChanged(); 
		}
		
		
		auto = (MultiAutoCompleteTextView) findViewById(R.id.tags_tv);
		suggestionAdapter = new SemanticSuggestionsAdapter(getContext(), R.layout.semantic_tag_list_row_layout, provider);
		auto.setAdapter(suggestionAdapter);
		auto.setThreshold(THRESHOLD);
		auto.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
//		auto.addTextChangedListener(textWatcher);
		auto.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SemanticSuggestion s = (SemanticSuggestion) (auto.getAdapter().getItem(position));
				tagListAdapter.add(s);
				tagListAdapter.notifyDataSetChanged();
				auto.setText("");
			}
		});

		Button cancel = (Button) findViewById(R.id.btn_tags_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();

			}
		});
		
		Button addTag = (Button) findViewById(R.id.addTagButton);
		addTag.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String txt = auto.getText().toString();
				if (txt != null && txt.trim().length() > 0) {
					SemanticSuggestion ss = new SemanticSuggestion();
					ss.setName(txt.trim());
					ss.setType(TYPE.KEYWORD);
					tagListAdapter.add(ss);
					tagListAdapter.notifyDataSetChanged();
					auto.setText("");
				}
			}
		});

		Button ok = (Button) findViewById(R.id.btn_tags_ok);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<SemanticSuggestion> list = new ArrayList<SemanticSuggestion>();
				if (!tagListAdapter.isEmpty()) {
					for (int i = 0; i < tagListAdapter.getCount(); i++) {
						list.add(tagListAdapter.getItem(i));
					}
				}
				listener.onTagsSelected(list);
				dismiss();
			}
		});
	}

//	private final TextWatcher textWatcher = new TextWatcher() {
//		
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			if (s != null && s.length() > THRESHOLD) {
//				suggestionAdapter.clear();
//				List<SemanticSuggestion> list = provider.getTags(s);
//				if (list != null) {
//					for (SemanticSuggestion ss : list) suggestionAdapter.add(ss);
//				}
//				suggestionAdapter.notifyDataSetChanged();
//			}
//		}
//		
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//		
//		@Override
//		public void afterTextChanged(Editable s) {}
//	};
}
