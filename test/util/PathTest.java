package util;


public enum PathTest {
	
	RESOURCES_RESPOSITORY ("./WebContent/resources/"),
	IMAGE_REPOSITORY_TEST ("./WebContent/resources/image/"),
	TEXT_REPOSITORY_TEST  ("./WebContent/resources/text/"),
	XML_REPOSITORY_TEST   ("./WebContent/resources/xml/");

	private final String value;

	/**
	 * Construtor da enum
	 * @param value - caminho do diret�rio
	 */
	PathTest(String value) {
		this.value = value;
	}
	
	/**
	 * Retorna o caminho do Path
	 * @return caminho do Path
	 */
	public String getValue(){
		return this.value;
	}
	
	/**
	 * O mesmo que getValue()
	 */
	public String toString(){
		return getValue();
	}
}
