package eu.trentorise.smartcampus.android.share;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import eu.trentorise.smartcampus.android.common.R;

public class Share {
	public static final String TYPE_TEXT = "text/plain";
	
	public static void share(Activity a, List<Attach> att) {
		share(a, null, null, null, null, att);
	}

	public static void share(Activity a, String subj) {
		share(a, subj, null, null, null, null);
	}
	

	public static void share(Activity a, String subj, List<Attach> att) {
		share(a, subj, null, null, null, att);
	}

	public static void share(Activity a, String subj, String text) {
		share(a, subj, text, null, null, null);
	}

	public static void share(Activity a, String subj, String text,
			List<Attach> att) {
		share(a, subj, text, null, null, att);
	}

	public static void share(Activity a, String subj, String text, String add) {
		share(a, subj, text, add, null, null);
	}

	public static void share(Activity a, String subj, String text, String add,
			String date) {
		share(a, subj, text, add, date, null);
	}

	public static void share(Activity a, String subj, String text, String add,
			String date, List<Attach> att) {

		Intent i = new Intent();
		if (!(att == null)) { // se att non è nullo
			if (att.size() > 1) { // se att contiene più di un media
				i.setAction(Intent.ACTION_SEND_MULTIPLE);
			} else if (att.size() == 1) { // se att contiene 1 media
				i.setAction(Intent.ACTION_SEND);
			} else if (att.size() > 10) { // se att contiene più di 10 media
				throw new IllegalArgumentException(
						"Troppi media condivisi contemporaneamente");
			}
			if (!sametype(att)) { // se att non ha tutti media dello stesso
									// formato
				throw new IllegalArgumentException(
						"Argomenti di formato differenti");
			}
		} else { // se att è null (non c'è )
			i.setAction(Intent.ACTION_SEND);
		}

		if (subj == null && text == null && add == null && date == null
				&& att != null) { // att

			i.setType(att.get(0).mimeType); 
			ArrayList<Uri> files = new ArrayList<Uri>();
			for (int x = 0; x < att.size(); x++) {
				String filepath = att.get(x).filePath;
				Uri uri = Uri.parse(filepath);
				files.add(uri);
			}
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		}
		
		if (subj == null && text != null && add == null && date == null
				&& att == null) { // subj

			String doc = text;
			i.setType(TYPE_TEXT);
			i.putExtra(Intent.EXTRA_TEXT, doc);
		}
		if (subj != null && text != null && add == null && date == null
				&& att == null) { // subj text

			String doc = text;
			i.setType(TYPE_TEXT);
			i.putExtra(Intent.EXTRA_SUBJECT, subj);
			i.putExtra(Intent.EXTRA_TEXT, doc);
		}
		if (subj != null && text != null && add == null && date == null
				&& att != null) { // subj text att

			String doc = text;
			i.putExtra(Intent.EXTRA_SUBJECT, subj);
			i.putExtra(Intent.EXTRA_TEXT, doc);
			i.setType(att.get(0).mimeType);
			ArrayList<Uri> files = new ArrayList<Uri>();
			for (int x = 0; x < att.size(); x++) {
				String filepath = att.get(x).filePath;
				Uri uri = Uri.parse(filepath);
				files.add(uri);
			}
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		}
		if (subj != null && text != null && add != null && date == null
				&& att == null) { // subj text add

			String doc = subj + "\n" + text + "\n" + add;
			i.setType(TYPE_TEXT);
			i.putExtra(Intent.EXTRA_SUBJECT, subj);
			i.putExtra(Intent.EXTRA_TEXT, doc);
		}
		if (subj != null && text != null && add != null && date != null
				&& att == null) { // subj text add date

			String doc = text + "\n" + add + "\n" + date;
			i.setType(TYPE_TEXT);
			i.putExtra(Intent.EXTRA_SUBJECT, subj);
			i.putExtra(Intent.EXTRA_TEXT, doc);
		}
		if (subj != null && text != null && add != null && date != null
				&& att != null) { // subj text add date att

			String doc = text + "\n" + add + "\n" + date;
			i.setType(att.get(0).mimeType);
			ArrayList<Uri> files = new ArrayList<Uri>();
			for (int x = 0; x < att.size(); x++) {
				String filepath = att.get(x).filePath;
				Uri uri = Uri.parse(filepath);
				files.add(uri);
			}
			i.putExtra(Intent.EXTRA_SUBJECT, subj);
			i.putExtra(Intent.EXTRA_TEXT, doc);
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		}
		if (subj != null && text == null && add == null && date == null
				&& att != null) { // subj att

			i.setType(att.get(0).mimeType);
			ArrayList<Uri> files = new ArrayList<Uri>();
			for (int x = 0; x < att.size(); x++) {
				String filepath = att.get(x).filePath;
				Uri uri = Uri.parse(filepath);
				files.add(uri);
			}
			i.putExtra(Intent.EXTRA_SUBJECT, subj);
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		}
		if (subj == null && text != null && add == null && date == null
				&& att != null) { // subj att

			String doc = text;
			i.setType(att.get(0).mimeType);
			ArrayList<Uri> files = new ArrayList<Uri>();
			for (int x = 0; x < att.size(); x++) {
				String filepath = att.get(x).filePath;
				Uri uri = Uri.parse(filepath);
				files.add(uri);
			}
			i.putExtra(Intent.EXTRA_TEXT, doc);
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		}
		a.startActivity(Intent.createChooser(i, a.getString(R.string.app_name)));
	}

	public static boolean sametype(List<Attach> att) {
		boolean x = true;
		String campione = att.get(0).mimeType;
		for (int i = 1; i < att.size(); i++) {
			if (!(att.get(i).mimeType.equals(campione))) {
				x = false;
				i = att.size();
			}
		}
		return x;
	}

	public static void configura(List<Attach> att, List<String> type,
			List<String> filePath) {
		if (type.size() != filePath.size()) {
			throw new IllegalArgumentException(
					"La lista type e la lista filepath non hanno la stessa dimensione");
		}
		for (int i = 0; i < type.size(); i++) {
			Attach test = new Attach();
			test.setMimeType(type.get(i));
			test.setFilePath(filePath.get(i));
			att.add(test);
		}
	}

}