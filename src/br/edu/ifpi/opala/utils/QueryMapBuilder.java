package br.edu.ifpi.opala.utils;

import java.util.HashMap;
import java.util.Map;

public class QueryMapBuilder {

	Map<String, String> fields = new HashMap<String, String>();

	public QueryMapBuilder field(Metadata field, String value) {
		fields.put(field.getValue(), value);
		return this;
	}

	public QueryMapBuilder author(String value) {
		fields.put(Metadata.AUTHOR.getValue(), value);
		return this;
	}

	public QueryMapBuilder title(String value) {
		fields.put(Metadata.TITLE.getValue(), value);
		return this;
	}

	public QueryMapBuilder content(String value) {
		fields.put(Metadata.CONTENT.getValue(), value);
		return this;
	}

	public QueryMapBuilder id(String value) {
		fields.put(Metadata.ID.getValue(), value);
		return this;
	}

	public QueryMapBuilder keywords(String value) {
		fields.put(Metadata.KEYWORDS.getValue(), value);
		return this;
	}

	public QueryMapBuilder publicationDate(String value) {
		fields.put(Metadata.PUBLICATION_DATE.getValue(), value);
		return this;
	}

	public QueryMapBuilder format(String value) {
		fields.put(Metadata.FORMAT.getValue(), value);
		return this;
	}

	public QueryMapBuilder type(String value) {
		fields.put(Metadata.TYPE.getValue(), value);
		return this;
	}

	public Map<String, String> build() {
		return fields;
	}
}