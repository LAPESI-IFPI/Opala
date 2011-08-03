package br.edu.ifpi.opala.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * Métodos utilitários
 * 
 * @author Pádua
 * 
 */
public class Util {

	
	/**
	 * Copia uma diretório de índice de srcFile para destFile.
	 * 
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyIndex(File srcFile, File destFile) throws IOException{
		Directory srcDir = new SimpleFSDirectory(srcFile);
		Directory destDir = new SimpleFSDirectory(destFile);
		Directory.copy(srcDir, destDir, true);
	}

	/**
	 * Apaga o arquivo ou o diretório informado recursivamente.
	 * 
	 * @param dir Arquivo ou diretório a ser apagado.
	 * @return falso se não conseguiu apagar ao menos um arquivo
	 */
	public static synchronized boolean deleteDir(File dir) {
		if (!dir.exists())
			return true;
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (File child : children) {
				boolean success = deleteDir(child);
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}
