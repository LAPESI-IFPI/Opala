package br.edu.ifpi.opala.statistic;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import br.edu.ifpi.opala.utils.Path;
/**
 * Classe que informa a estatística do índice de texto e imagem
 * 
 * @author <a href="mailto:paduafil@gmail.com">Pádua Furtado</a>
 *
 */
public class Statistic {

	/**
	 * Método que calcula a quantidade de documentos
	 * @param indexPath - caminho do indice para obter quantidade de documentos
	 * @return Quantidade de documentos
	 */
	private static long numDocuments(File indexPath) {

		try {
			FSDirectory indexDirectory = new SimpleFSDirectory(indexPath);
			if (IndexReader.indexExists(indexDirectory)) {
				IndexReader reader = IndexReader.open(indexDirectory, false);
				int numDoc = reader.numDocs(); 
				reader.close();
				return numDoc;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Método que calcula a quantidade de documentos do repositório padrão de texto. 
	 * @return Número de documentos de texto
	 */
	public static long numTextDocs(){
		return numDocuments(new File(Path.TEXT_INDEX.getValue()));
	}
	
	/**
	 * Método que calcula a quantidade de documentos do repositório de backup de texto. 
	 * @return Número de documentos de texto
	 */
	public static long numTextBackupDocs(){
		return numDocuments(new File(Path.TEXT_BACKUP.getValue()));
	}
	
	/**
	 * Método que calcula a quantidade de documentos do repositório padrão de imagem. 
	 * @return Número de documentos de imagens
	 */
	public static long numImageDocs(){
		return numDocuments(new File(Path.IMAGE_INDEX.getValue()));
	}
	
	/**
	 * Método que calcula a quantidade de documentos do repositório de backup de imagem. 
	 * @return Número de documentos de imagens
	 */
	public static long numImageBackupDocs(){
		return numDocuments(new File(Path.IMAGE_BACKUP.getValue()));
	}

	/**
	 * Método que calcula o tamanho do espaço ocupado pelo índice no disco. 
	 * @param  indexPath - caminho do indice para obter o tamanho do índice
	 * @return tamanho do espaço ocupado pelo diretório
	 */
	private static long indexSize(File indexPath) {
		long size = 0;
		for (File f : indexPath.listFiles()) {
			if (f.isDirectory()) {
				size += indexSize(f);
			} else {
				size += f.length();
			}
		}
		return size;
	}

	/**
	 * Método que calcula o tamanho do espaço ocupado pelo índice de Imagem no disco. 
	 * @return Tamanho do espaço ocupado pelo índice de imagem
	 */
	public static long imageIndexSize() {
		long size = 0;
		size = indexSize(new File(Path.IMAGE_INDEX.getValue()));
		return size;
	}
	
	/**
	 * Método que calcula o tamanho do espaço ocuaado pelo índice de Texto no disco. 
	 * @return Tamanho do espaço ocupado pelo índice de texto
	 */
	public static long textIndexSize() {
		long size = 0;
		size = indexSize(new File(Path.TEXT_INDEX.getValue()));
		return size;
	}
	
	/**
	 * Método que calcula o tamanho do espaço ocupado pelo índice de Backup de Imagem no disco. 
	 * @return Tamanho do espaço ocupado pelo índice de imagem
	 */
	public static long imageIndexBackupSize() {
		long size = 0;
		size = indexSize(new File(Path.IMAGE_BACKUP.getValue()));
		return size;
	}
	
	/**
	 * Método que calcula o tamanho do espaço ocuaado pelo índice de Backup de Texto no disco. 
	 * @return Tamanho do espaço ocupado pelo índice de texto
	 */
	public static long textIndexBackupSize() {
		long size = 0;
		size = indexSize(new File(Path.TEXT_BACKUP.getValue()));
		return size;
	}

	/**
	 * Método que deve retornar a quantidade de buscas realizadas
	 */
	public static long numSearch() {
		return 1;
	}
}
