package br.edu.ifpi.opala.utils;

import java.io.Serializable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Classe em que os objetos representam os metadados do DublinCore.
 * @author Mônica Regina da Silva
 */

public class MetaDocument implements Serializable {

	private static final long serialVersionUID = 1L;
	private Document metaDocument;

	/**
	 * Construtor da classe. Inicializa o objeto criando todos os campos
	 * DublinCore no Document do Lucene com valorez vazios.
	 */
	public MetaDocument() {
		this.metaDocument = new Document();
		this.setId("");
		this.setAuthor("");
		this.setTitle("");
		this.setPublicationDate("");
		this.setFormat("");
		this.setType("");
		this.setKeywords("");
	}
	public MetaDocument(Document metaDocument){
		this.metaDocument = metaDocument;
	}

	/**
	 * Retorna o valor do campo AUTHOR
	 * 
	 * @return author
	 */
	public String getAuthor() {
		return metaDocument.get(Metadata.AUTHOR.getValue());
	}

	/**
	 * Atualiza o AUTHOR do documento
	 * 
	 * @param author
	 */
	public void setAuthor(String author) {
		if (metaDocument.get(Metadata.AUTHOR.getValue()) != null)
			metaDocument.removeField(Metadata.AUTHOR.getValue());
		metaDocument.add(new Field(Metadata.AUTHOR.getValue(), author, Field.Store.YES, Field.Index.ANALYZED));
	}

	/**
	 * Retorna o valor do campo PUBLICATION-DATE
	 * 
	 * @return publication date
	 */
	public String getPublicationDate() {
		return metaDocument.get(Metadata.PUBLICATION_DATE.getValue());
	}
	
	/**
	 * Atualiza o PUBLICATION-DATE do documento
	 * 
	 * @param publicationDate
	 */
	public void setPublicationDate(String publicationDate) {
		if (metaDocument.get(Metadata.PUBLICATION_DATE.getValue()) != null)
			metaDocument.removeField(Metadata.PUBLICATION_DATE.getValue());
		metaDocument.add(new Field(Metadata.PUBLICATION_DATE.getValue(), publicationDate, Field.Store.YES, Field.Index.ANALYZED));
	}

	/**
	 * Retorna o valor do campo FORMAT
	 * 
	 * @return format
	 */
	public String getFormat() {
		return metaDocument.get(Metadata.FORMAT.getValue());
	}

	/**
	 * Atualiza o FORMAT do documento
	 * 
	 * @param format
	 */
	public void setFormat(String format) {
		if (metaDocument.get(Metadata.FORMAT.getValue()) != null)
			metaDocument.removeField(Metadata.FORMAT.getValue());
		metaDocument.add(new Field(Metadata.FORMAT.getValue(), format, Field.Store.YES, Field.Index.ANALYZED));
	}

	/**
	 * Retorna o valor do campo ID
	 * 
	 * @return id
	 */
	public String getId() {
		return metaDocument.get(Metadata.ID.getValue());
	}

	/**
	 * Atualiza o ID do documento
	 * 
	 * @param id
	 */
	public void setId(String id) {
		if (metaDocument.get(Metadata.ID.getValue()) != null)
			metaDocument.removeField(Metadata.ID.getValue());
		metaDocument.add(new Field(Metadata.ID.getValue(), id, Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	/**
	 * Retorna o valor do campo KEYWORDS
	 * 
	 * @return keywords
	 */
	public String getKeywords() {
		return metaDocument.get(Metadata.KEYWORDS.getValue());
	}
	
	/**
	 * Adiciona palavras-chaves do documento.
	 * 
	 * @param keywords
	 */
	public void setKeywords(String keywords) {
		if (metaDocument.get(Metadata.KEYWORDS.getValue()) != null)
			metaDocument.removeField(Metadata.KEYWORDS.getValue());
		metaDocument.add(new Field(Metadata.KEYWORDS.getValue(), keywords, Field.Store.YES, Field.Index.ANALYZED));
	}

	/**
	 * Retorna o valor do campo TITLE
	 * 
	 * @return title
	 */
	public String getTitle() {
		return metaDocument.get(Metadata.TITLE.getValue());
	}

	/**
	 * Atualiza o TITLE do documento
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		if (metaDocument.get(Metadata.TITLE.getValue()) != null)
			metaDocument.removeField(Metadata.TITLE.getValue());
		metaDocument.add(new Field(Metadata.TITLE.getValue(), title, Field.Store.YES, Field.Index.ANALYZED));
	}

	/**
	 * Retorna o valor do campo TYPE
	 * 
	 * @return type
	 */
	public String getType() {
		return metaDocument.get(Metadata.TYPE.getValue());
	}

	/**
	 * Atualiza o TYPE do documento
	 * 
	 * @param type
	 */
	public void setType(String type) {
		if (metaDocument.get(Metadata.TYPE.getValue()) != null)
			metaDocument.removeField(Metadata.TYPE.getValue());
		metaDocument.add(new Field(Metadata.TYPE.getValue(), type, Field.Store.YES, Field.Index.ANALYZED));
	}
	
	public void setField(String fieldName, String fieldValue){
		setField(fieldName, fieldValue, true);
	}
	
	public void setField(String fieldName, String fieldValue, boolean indexed) {
		if (metaDocument.get(fieldName) != null)
			metaDocument.removeField(fieldName);
		if (indexed)
			metaDocument.add(new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.ANALYZED));
		else
			metaDocument.add(new Field(fieldName, fieldValue, Field.Store.YES, Field.Index.NOT_ANALYZED));
	}
	
	public String getField(String fieldName){
		return metaDocument.get(fieldName);
	}

	/**
	 * Retorna o Document do Lucene com os campos e seus valores
	 * @return document do Lucene
	 */	
	public Document getDocument() {
		return metaDocument;
	}
}
