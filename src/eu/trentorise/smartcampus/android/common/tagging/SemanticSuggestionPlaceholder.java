package eu.trentorise.smartcampus.android.common.tagging;

import android.widget.TextView;

public class SemanticSuggestionPlaceholder {
	private SemanticSuggestion object;
	private TextView title;

	private TextView description;

	public SemanticSuggestion getObject() {
		return object;
	}

	public void setObject(SemanticSuggestion object) {
		this.object = object;
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public TextView getDescription() {
		return description;
	}

	public void setDescription(TextView description) {
		this.description = description;
	}

}
