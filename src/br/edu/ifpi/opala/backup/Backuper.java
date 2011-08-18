package br.edu.ifpi.opala.backup;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.SimpleFSDirectory;

import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.Util;

public abstract class Backuper {
	
	private static final int BUFFER_SIZE = 65536;
	private static int bufferSize = BUFFER_SIZE;
	private File backupFolder;
	private File sourceFolder;
	private Directory index;
	private Directory backup;
	
	/**
	 * Construtor da classe que recebe um endereco da pasta de
	 * backup e o endereco do índice.
	 * 
	 * @param backupFolder
	 * @param sourceFolder
	 * @throws IOException 
	 */
	public Backuper(Path backupFolder, Path sourceFolder) throws IOException {
		super();
		this.backupFolder = new File(backupFolder.getValue());
		this.sourceFolder = new File(sourceFolder.getValue());
		this.backup = new SimpleFSDirectory(this.backupFolder);
		this.index = new SimpleFSDirectory(this.sourceFolder);
	}
	
	public void update() throws IOException {
		System.out.println("Iniciando atualização do backup...");
		if(backupFolder.canRead()){
	        for (String name : backup.listAll()) {
	        	updateBackup(name, index, backup);
	        }
		}
        for (String name : index.listAll()) {
            copyFile(name, index, backup, bufferSize);
        }
        System.out.println("Terminada.");
    }
	
	public void updateBackup(String name, Directory src, Directory dest) throws IOException {
		try{
	        if (src.fileExists(name) && dest.fileExists(name)) {
	        	if ((src.fileLength(name) == dest.fileLength(name))
						&& !(name.equals("segments.gen"))) {
					return;
	            } else {
	                dest.deleteFile(name);
	            }
	        }
	
	        if (!src.fileExists(name) && dest.fileExists(name)) {
	            dest.deleteFile(name);
	            return;
	        }
		}catch(IOException e){
			e.getStackTrace();
		}
    }
    
    public void copyFile(String name, Directory src, Directory dest, int bufferSize) throws IOException {
		try{	
	    	if (!name.equals("write.lock")) {
				
				IndexOutput os = null;
				IndexInput is = null;
				byte[] buf = null;
				long len = 0;
				try {
					
					os = dest.createOutput(name);
					is = src.openInput(name);
					
					len = is.length();
					buf = new byte[bufferSize];
					
					long readCount = 0;
					while (readCount < len) {
						int toRead = readCount + bufferSize > len ? (int) (len - readCount)
								: bufferSize;
						is.readBytes(buf, 0, toRead);
						os.writeBytes(buf, toRead);
						readCount += toRead;
					}
				} finally {
					
					try {
						if (os != null)
							os.close();
					} finally {
						if (is != null)
							is.close();
					}
				}
				
			}
	    	
	    }catch(IOException e){
			e.getStackTrace();
		}
    	
	}
    
    /**
	 * Apaga o conteúdo do diretório do índice corrompido e restaura o
	 * último backup realizado para o diretório do índice.
	 * 
	 * @throws IOException
	 */
	public void restoreIndex() throws IOException{
		this.beforeRestoreBackup();
		System.out.println("iniciada restauração do indice");
		Util.deleteDir(sourceFolder);
		Util.copyIndex(backupFolder, sourceFolder);
		System.out.println("terminada");
		this.afterRestoreBackup();		
	}
    
    /**
	 * Sempre é chamado antes de restaurar o backup. Deve ser implementado 
	 * pela subclasses concretas.
	 */
	public abstract void beforeRestoreBackup();
	
	/**
	 * Sempre é chamado depois da restauração do backup. Deve ser implementado 
	 * pela subclasses concretas.
	 */
	public abstract void afterRestoreBackup();

	
}