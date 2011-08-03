package br.edu.ifpi.opala.utils;

/**
 * Define os campos usados no projeto e algumas
 * outras constantes relacionadas.
 * 
 * @author Mônica
 * 
 */
public enum Metadata {
	
	PARENT("document"),
	CONTENT("content"),
	
	AUTHOR("author"),
	PUBLICATION_DATE("publication-date"),
	FORMAT("format"),
	ID("id"),
	TITLE("title"),
	TYPE("type"),
	KEYWORDS("keywords"),
	FIELD("field"),
	FIELD_NAME("name");
	
	private final String value;

	/**
	 * Construtor da Enum
	 * 
	 * @param value - valor do campo
	 */
	Metadata(String value) {
		this.value = value;
	}
	
	/**
	 * Retorna o nome do campo armazenado na Enum
	 * @return campo
	 */
	public String getValue(){
		return this.value;
		
	}
	
	/**
	 * O mesmo que getValue();
	 */
	public String toString(){
		return getValue();
	}

}
