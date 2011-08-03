package br.edu.ifpi.opala.backup;

import java.io.IOException;

import br.edu.ifpi.opala.searching.MetadataSearch;
import br.edu.ifpi.opala.searching.TextSearcherImpl;
import br.edu.ifpi.opala.utils.Path;

public class TextBackuper extends Backuper{

	public TextBackuper() throws IOException {
		super(Path.TEXT_BACKUP, Path.TEXT_INDEX);
	}

	public void updateBackup() throws IOException {
		super.update();
	}

	@Override
	public void beforeRestoreBackup() {
		TextSearcherImpl.setMetadataSearch(new MetadataSearch(Path.TEXT_BACKUP.getValue()));
	}

	@Override
	public void afterRestoreBackup() {
		TextSearcherImpl.setMetadataSearch(new MetadataSearch(Path.TEXT_INDEX.getValue()));
	}

}
