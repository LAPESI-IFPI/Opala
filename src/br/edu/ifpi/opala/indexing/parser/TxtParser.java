package br.edu.ifpi.opala.indexing.parser;

import java.io.IOException;
import java.io.InputStream;

import br.edu.ifpi.opala.utils.Conversor;
/**
 * Parser de arquivos .txt
 * @author Mônica
 */
public class TxtParser {

	/**
	 * Extrai o conteúdo de um InputStream de .txt
	 * @param inputStream - arquivo texto
	 * @return String do conteúdo
	 * @throws IOException
	 */
	public String getContent(InputStream inputStream) throws IOException {
		return  Conversor.InputStreamToString(inputStream);
	}
}
