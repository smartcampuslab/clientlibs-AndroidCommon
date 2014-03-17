package eu.trentorise.smartcampus.android.share;

//Autore: Digilio Mattia 

public class Attach {

	String filePath;
	String mimeType; // video/* or image/*

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
