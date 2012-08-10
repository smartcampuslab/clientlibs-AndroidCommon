package eu.trentorise.smartcampus.android.common.tagging;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.trentorise.smartcampus.android.common.R;

public class SemanticSuggestionsAdapter extends ArrayAdapter<SemanticSuggestion> {
	private Context context;
	private int layoutResourceId;

	public SemanticSuggestionsAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
	}
	public SemanticSuggestionsAdapter(Context context, int layoutResourceId, List<SemanticSuggestion> database) {
		super(context, layoutResourceId, database);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
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
				word.setText(s.getWord());
			TextView descr = (TextView) row.findViewById(R.id.semanticTagDescr);
			if (descr != null)
				descr.setText(s.getDescription());
		}
		return row;
	}
}