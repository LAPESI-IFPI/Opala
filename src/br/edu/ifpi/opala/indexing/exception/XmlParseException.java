package br.edu.ifpi.opala.indexing.exception;


/**
 * Classe que encapsula erros gerados na leitura de um arquivo XML
 *@author Ricardo Erikson
 * 
 */

public class XmlParseException extends Exception {

	private static final long serialVersionUID = -1230333445901024019L;

	/**
	 * Chama o construtor da classe mãe
	 * @param message
	 */
	public XmlParseException(String message) {
		super(message);

	}

	/**
	 * Chama o construtor da classe mãe
	 * @param message
	 * @param cause
	 */
	public XmlParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
