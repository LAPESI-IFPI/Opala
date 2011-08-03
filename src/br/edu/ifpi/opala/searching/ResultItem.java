package br.edu.ifpi.opala.searching;

import java.io.Serializable;
import java.util.Map;

/**
 * Classe que representa um item de resultado de busca.
 * 
 * @author Dannylvan
 */
@SuppressWarnings("serial")
public class ResultItem implements Serializable {

	private String id;
	private String score;
	private boolean duplicated;

	private Map<String, String> fields;
	
	public ResultItem(){
	}
	
	public ResultItem(String id, String score, boolean duplicated) {
		this.id = id;
		this.score = score;
		this.duplicated = duplicated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public boolean isDuplicated() {
		return duplicated;
	}

	public void setDuplicated(boolean duplicated) {
		this.duplicated = duplicated;
	}

	public String getField(String key) {
		return fields.get(key);
	}

}
