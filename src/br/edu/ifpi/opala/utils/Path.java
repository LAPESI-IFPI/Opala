package br.edu.ifpi.opala.utils;

/**
 * Prover o caminho(path) dos resources e do índice.
 * 
 * @author <a href="mailto:paduafil@gmail.com">Pádua Furtado</a>
 */

public enum Path {

	TEXT_INDEX		(Configuration.getInstance().getPropertyValue(Property.OPALA_TEXT_INDEX_ADDRESS)),
	IMAGE_INDEX		(Configuration.getInstance().getPropertyValue(Property.OPALA_IMAGE_INDEX_ADDRESS)),
	
	IMAGE_BACKUP	(Configuration.getInstance().getPropertyValue(Property.OPALA_IMAGE_INDEX_BACKUP_ADDRESS)),
	TEXT_BACKUP		(Configuration.getInstance().getPropertyValue(Property.OPALA_TEXT_INDEX_BACKUP_ADDRESS)),
	
	OPTIMIZE_LOG	(Configuration.getInstance().getPropertyValue(Property.OPALA_LOG_OPTIMIZE_ADDRESS)),
	BACKUP_LOG		(Configuration.getInstance().getPropertyValue(Property.OPALA_LOG_BACKUP_ADDRESS)),

	BACKUP_FREQUENCY	(Configuration.getInstance().getPropertyValue(Property.OPALA_BACKUP_FREQUENCY)),
	OPTIMIZE_FREQUENCY	(Configuration.getInstance().getPropertyValue(Property.OPALA_OPTIMIZE_FREQUENCY)),
	
	NUMBER_IMAGE_BACKUP (Configuration.getInstance().getPropertyValue(Property.OPALA_NUMMBER_OF_IMAGE_INDEX_BACKUPS)),
	NUMBER_TEXT_BACKUP (Configuration.getInstance().getPropertyValue(Property.OPALA_NUMBER_OF_TEXT_INDEX_BACKUPS)),
	
	TEXT_INDEX_AUTO_RESTORE (Configuration.getInstance().getPropertyValue(Property.OPALA_TEXT_INDEX_AUTO_RESTORE)),
	IMAGE_INDEX_AUTO_RESTORE (Configuration.getInstance().getPropertyValue(Property.OPALA_IMAGE_INDEX_AUTO_RESTORE));
	
	
	private final String value;

	/**
	 * Construtor da enum
	 * @param value - caminho do diretório
	 */
	Path(String value) {
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
