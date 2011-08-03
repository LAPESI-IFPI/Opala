package br.edu.ifpi.opala.utils;

public enum Property {

	OPALA_TEXT_INDEX_ADDRESS("opala.text.index.address"),
	OPALA_IMAGE_INDEX_ADDRESS("opala.image.index.address"),

	OPALA_TEXT_INDEX_BACKUP_ADDRESS("opala.text.index.backup.address"),
	OPALA_IMAGE_INDEX_BACKUP_ADDRESS("opala.image.index.backup.address"),
	
	OPALA_LOG_OPTIMIZE_ADDRESS("opala.log.optimize.address"),
	OPALA_LOG_BACKUP_ADDRESS("opala.log.backup.address"),
	
	OPALA_BACKUP_FREQUENCY("opala.backup.frequency"),
	OPALA_OPTIMIZE_FREQUENCY("opala.optimize.frequency"),
	
	OPALA_NUMMBER_OF_IMAGE_INDEX_BACKUPS("opala.image.index.backup.number"),
	OPALA_NUMBER_OF_TEXT_INDEX_BACKUPS("opala.text.index.backup.number"),
	
	OPALA_TEXT_INDEX_AUTO_RESTORE("opala.text.index.backup.autorestore"),
	OPALA_IMAGE_INDEX_AUTO_RESTORE("opala.image.index.backup.autorestore");
	
	String propertyName;

	Property(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

}
