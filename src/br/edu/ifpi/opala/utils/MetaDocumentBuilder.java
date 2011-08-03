package br.edu.ifpi.opala.utils;

public class MetaDocumentBuilder {
	
	public MetaDocument metadoc = new MetaDocument();
	
	public MetaDocumentBuilder id(String id){
		metadoc.setId(id);
		return this;
	}
	
	public MetaDocumentBuilder author(String author){
		metadoc.setAuthor(author);
		return this;
	}
	
	public MetaDocumentBuilder title(String title){
		metadoc.setTitle(title);
		return this;
	}
	
	public MetaDocumentBuilder format(String format){
		metadoc.setFormat(format);
		return this;
	}
	
	public MetaDocumentBuilder keywords(String keywords){
		metadoc.setKeywords(keywords);
		return this;
	}
	
	public MetaDocumentBuilder publicationDate(String publicationDate){
		metadoc.setPublicationDate(publicationDate);
		return this;
	}
	
	public MetaDocumentBuilder type(String type){
		metadoc.setType(type);
		return this;
	}
	
	public MetaDocumentBuilder field(String fieldName, String fieldValue){
		metadoc.setField(fieldName, fieldValue);
		return this;
	}
	
	public MetaDocument build(){
		return metadoc;
	}
	
}
