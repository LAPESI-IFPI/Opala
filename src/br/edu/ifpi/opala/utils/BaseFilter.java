package br.edu.ifpi.opala.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
/**
 * Filtra o nome dos arquivos que estão em um diretório que tem
 * a extensão informada no construtor.
 * @author Pádua
 *
 */
public class BaseFilter implements FilenameFilter {

	private List<String> fileTypes = new ArrayList<String>();

	/**
	 * Construtor que recebe uma extensão, que será usada nos outros métodos da classe.
	 * @param fileType extensão dos arquivos desejados
	 */
	public BaseFilter(String fileType) {
		this.fileTypes.add(fileType);
	}
	
	/**
	 * Construtor que recebe um array de extensões, que serão usadas nos outros métodos da classe.
	 * @param fileTypes - Extensões dos arquivos desejados
	 */
	public BaseFilter(String[] fileTypes) {
		for (int i = 0; i < fileTypes.length; i++) {
			this.fileTypes.add(fileTypes[i]);
		}
	}

	/**
	 * Implementação do método da interface {@link FilenameFilter}
	 * 
	 * @param dir diretório em que o arquivo está
	 * @param name nome do arquivo
	 * @return <code>true</code> se o arquivo tiver a mesma extensão que a informada no construtor
	 */
	public boolean accept(File dir, String name) {
		int position = name.lastIndexOf(".");
		String ext = name.substring(position).toUpperCase();
		for(String fileType:fileTypes){			
			if (ext.equals(fileType.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Encontra todos os arquivos da(s) extensão(ões) informada(s) no construtor que estão
	 * no diretório recebido como parâmetro
	 * @param dir diretório em que os arquivos serão buscados.
	 * @return lista com os nomes dos arquivos da extensão desejada
	 */
	public ArrayList<String> getFilteredList(File dir) {
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i<dir.list().length; i++ ){
			String fileName = dir.list()[i];
			if (accept(dir,fileName)){
				list.add(fileName);
			}
		}
		return list;

	}

}
