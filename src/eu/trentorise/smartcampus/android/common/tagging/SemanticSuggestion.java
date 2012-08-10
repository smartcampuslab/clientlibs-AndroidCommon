package eu.trentorise.smartcampus.android.common.tagging;

public class SemanticSuggestion {

	public enum TYPE {SEMANTIC, KEYWORD, ENTITY};
	
	private String description;
	private String id;
	private String word;
	private TYPE type = TYPE.SEMANTIC;
	
	public SemanticSuggestion(String word, String description) {
		super();
		this.description = description;
		this.word = word;
	}

	
	public SemanticSuggestion() {
		super();
	}


	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
	
	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return getWord();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
