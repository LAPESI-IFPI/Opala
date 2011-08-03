package br.edu.ifpi.opala.searching;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import br.edu.ifpi.opala.utils.Metadata;

/**
 * Classe criada para verificar a duplicação de documentos através da
 * comparação de metadados retornados na busca.
 * 
 * @author Lidijanne e Mônica
 * 
 */

public class MetadataCompare {

	/**
	 * Método responsável por calcular a diferença de ano dos campos de
	 * {@link Metadata#PUBLICATION_DATE} entre dois {@link ResultItem}.
	 * 
	 * @param resultItem1
	 * @param resultItem2
	 * @return true se a diferença for menor que um ano
	 */
	private static boolean yearSim(ResultItem resultItem1,
			ResultItem resultItem2) {

		boolean resultado = false;
		Date date1, date2;

		try {
			date1 = new SimpleDateFormat("dd/MM/yyyy").parse(resultItem1
					.getField(Metadata.PUBLICATION_DATE.getValue()));
			date2 = new SimpleDateFormat("dd/MM/yyyy").parse(resultItem2
					.getField(Metadata.PUBLICATION_DATE.getValue()));

			Calendar publicationDate1 = Calendar.getInstance();
			publicationDate1.setTime(date1);
			Calendar publicationDate2 = Calendar.getInstance();
			publicationDate2.setTime(date2);

			long diffMillis = publicationDate2.getTimeInMillis()
					- publicationDate1.getTimeInMillis();
			long diffDays = diffMillis / (24 * 60 * 60 * 1000);
			if (publicationDate2.before(publicationDate1)) {
				diffDays *= -1;
			}
			resultado = diffDays < 365;

		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return resultado;

	}

	/**
	 * Método responsável por calcular a similaridade entre as iniciais de
	 * duas Strings .
	 * 
	 * @param iniciais1
	 * @param iniciais2
	 * @return true se duas iniciais corresponderem mesmo em posições
	 *         diferentes
	 */
	private static boolean IniSim(String iniciais1, String iniciais2) {
		char fim1, fim2;

		fim1 = iniciais1.charAt(iniciais1.length() - 1);
		fim2 = iniciais2.charAt(iniciais2.length() - 1);

		if (iniciais1.charAt(0) == iniciais2.charAt(0) && fim1 == fim2)
			return true;
		else if (iniciais1.length() > 1 && iniciais1.charAt(0) == fim2
				&& iniciais1.charAt(1) == iniciais2.charAt(0))
			return true;
		else if (iniciais2.length() > 1) {
			if (iniciais1.charAt(0) == iniciais2.charAt(1)
					&& fim1 == iniciais2.charAt(0))
				return true;
			else if (iniciais1.length() > 1
					&& iniciais1.charAt(0) == iniciais2.charAt(0)
					&& iniciais1.charAt(1) == iniciais2.charAt(1))
				return true;
		}

		return false;

	}

	/**
	 * Método utilizado para pegar as iniciais das palavras de um nome.
	 * 
	 * @param name
	 * @return iniciais dos nomes na String
	 */
	private static String getInitials(String name) {
		name = name.trim();
		String initials = Character.toString(name.charAt(0));
		while (name.indexOf(' ') != -1) {
			name = name.substring(name.indexOf(' ') + 1).trim();
			initials += Character.toString(name.charAt(0));
		}

		return initials;
	}

	/**
	 * Método utilizado para fazer a comparação entre os metadados de dois
	 * objetos {@link ResultItem}. No presente momento compara autores,
	 * data de publicação e título.
	 * 
	 * @param resultItem1
	 * @param resultItem2
	 * @return true se os documentos forem duplicados e false caso contrário
	 */
	public static boolean metadataMatch(ResultItem resultItem1,
			ResultItem resultItem2) {
		List<String> inicials1 = new ArrayList<String>();
		List<String> inicials2 = new ArrayList<String>();
		String author1, author2;

		if (isComparable(resultItem1) && isComparable(resultItem2)) {
			author1 = resultItem1.getField(Metadata.AUTHOR.getValue());
			author2 = resultItem2.getField(Metadata.AUTHOR.getValue());

			if (yearSim(resultItem1, resultItem2)) {
				inicials1 = separaAuthor(author1);
				inicials2 = separaAuthor(author2);

				return nameMatch(inicials1, inicials2)
						&& titleMatch(resultItem1.getField(Metadata.TITLE
								.getValue()), resultItem2
								.getField(Metadata.TITLE.getValue()));
			}
		}
		return false;

	}

	/**
	 * Método responsável por verificar a similaridade entre duas listas de
	 * iniciais de autores de documentos diferentes.
	 * 
	 * @param iniciais1
	 * @param iniciais2
	 * @return
	 */
	private static boolean nameMatch(List<String> iniciais1,
			List<String> iniciais2) {
		// FIXME Não esquecer de mudar o tipo dos valores e refatorar o
		// código
		float tamList1 = iniciais1.size();
		float tamList2 = iniciais2.size();
		float contador = 0;
		for (int i = 0; i < tamList1; i++) {
			for (int j = 0; j < tamList2; j++) {
				if (IniSim(iniciais1.get(i), iniciais2.get(j))) {
					contador++;
				}

			}
		}
		if (tamList1 > tamList2) {
			if ((contador / tamList1) < 0.6)
				return false;
			else
				return true;
		} else {
			if ((contador / tamList2) < 0.6)
				return false;
			else
				return true;
		}
	}

	/**
	 * Método que verifica se um {@link ResultItem} tem os metadados
	 * obrigatórios: {@link Metadata#PUBLICATION_DATE},
	 * {@link Metadata#AUTHOR} e {@link Metadata#TITLE} para poder ser
	 * comparado.
	 * 
	 * @param resultItem
	 * @return true se tiver os campos forem diferentes de null
	 */
	private static boolean isComparable(ResultItem resultItem) {
		return resultItem != null
				&& resultItem
						.getField(Metadata.PUBLICATION_DATE.getValue()) != null
				&& resultItem.getField(Metadata.AUTHOR.getValue()) != null
				&& resultItem.getField(Metadata.TITLE.getValue()) != null;
	}

	/**
	 * Metódo utilizado para separar o nome dos autores que vem no metadado
	 * {@link Metadata#AUTHOR} e devolve uma lista de iniciais dos autores
	 * contidos no campo.
	 * 
	 * @param names
	 * @return lista com cada autor
	 */
	private static List<String> separaAuthor(String names) {

		List<String> inicialsList = new ArrayList<String>();
		StringTokenizer nomes = new StringTokenizer(names, ";");

		while (nomes.hasMoreTokens()) {
			inicialsList.add(getInitials(nomes.nextToken()));
		}

		return inicialsList;
	}

	/**
	 * Metódo que calcula a similaridade entre títulos.
	 * 
	 * @return true se forem semelhantes na faixa de 75% a 100%
	 */
	private static boolean titleMatch(String string1, String string2) {
		//TODO Verificar como diminuir o tamanho da string depois de tirar os espaços em branco com o trim()
		//Trata as string antes de mandar para o metodo de Levenshtein
		string1 = string1.toLowerCase().trim();
		string2 = string2.toLowerCase().trim();
		
		int lev = StringUtils.getLevenshteinDistance(string1, string2);
		int max = string1.length() > string2.length() ? string1.length(): string2.length();
		float levDist = 1 - (float)lev / max;
		
		return (levDist >= 0.75 && levDist<=1);
	}
}