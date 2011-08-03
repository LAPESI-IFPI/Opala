package br.edu.ifpi.opala.backup;

import java.io.IOException;

import br.edu.ifpi.opala.searching.ImageSearch;
import br.edu.ifpi.opala.searching.MetadataSearch;
import br.edu.ifpi.opala.searching.SearcherImageImpl;
import br.edu.ifpi.opala.utils.Path;

public class ImageBackuper extends Backuper{

	public ImageBackuper() throws IOException {
		super(Path.IMAGE_BACKUP, Path.IMAGE_INDEX);
	}

	public void updateBackup() throws IOException {
		super.update();
	}
	
	@Override
	public void beforeRestoreBackup() {
		SearcherImageImpl.setImageSearch(new ImageSearch(Path.IMAGE_BACKUP.getValue()));
		SearcherImageImpl.setMetadataSearch(new MetadataSearch(Path.IMAGE_BACKUP.getValue()));
	}
	
	@Override
	public void afterRestoreBackup() {
		SearcherImageImpl.setImageSearch(new ImageSearch(Path.IMAGE_INDEX.getValue()));
		SearcherImageImpl.setMetadataSearch(new MetadataSearch(Path.IMAGE_INDEX.getValue()));
	}
	
}
