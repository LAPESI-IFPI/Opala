package br.edu.ifpi.opala.searching;

import java.util.List;
import java.util.Map;

import br.edu.ifpi.opala.indexing.TextIndexerImpl;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;

/**
 * Implementação da interface TextSearcher
 * 
 * @author Mônica
 * 
 */
public class TextSearcherImpl implements TextSearcher {

	
	private static MetadataSearch metadataSearch = new MetadataSearch(Path.TEXT_INDEX.getValue());
	
	public static MetadataSearch getMetadataSearch() {
		return metadataSearch;
	}

	public static void setMetadataSearch(MetadataSearch metadataSearch) {
		TextSearcherImpl.metadataSearch = metadataSearch;
	}

	/**
	 * Faz uma busca no índice nos campos informados, retornando um
	 * {@link SearchResult}. <code>batchStart</code> e <code>batchSize</code>
	 * informam a partir de que resultado e até quantos serão retornados,
	 * respectivamente. O <code>sortOn</code> indica que campo os resultados
	 * serão ordenados (em caso de nulo, os resultados serão ordenados por
	 * relevência). O <code>reverse</code> diz se os resultados estarão
	 * invertidos ou não.
	 * 
	 * @param fields
	 *            campos e valores a serem buscados
	 * @param returnedFields
	 *            campos que serão retornados
	 * @param batchStart
	 *            a partir de que documento será retornado
	 * @param batchSize
	 *            quantos documentos serão retornados
	 * @param sortOn
	 *            campo em que a busca será ordenada
	 * @param reverse
	 *            se os resultados virão na ordem inversa
	 * @return um objeto <code>SearchResult</code>
	 * 
	 * @see SearchResult
	 */
	public SearchResult search(	Map<String, String> fields,
								List<String> returnedFields,
								int batchStart,
								int batchSize,
								String sortOn,
								boolean reverse) {
		
		SearchResult sr = metadataSearch.search(fields, returnedFields, batchStart, batchSize, sortOn, reverse);
		if (sr.getCodigo().equals(ReturnMessage.UNEXPECTED_INDEX_ERROR)) {
		//	TextIndexerImpl.getTextIndexerImpl().restoreIndex();
		}
		return sr;
	}
	
	/**
	 * Faz uma busca no índice nos campos informados, retornando um
	 * {@link SearchResult}. <code>batchStart</code> e <code>batchSize</code>
	 * informam a partir de que resultado e até quantos serão retornados,
	 * respectivamente. O <code>sortOn</code> indica que campo os resultados
	 * serão ordenados (em caso de nulo, os resultados serão ordenados por
	 * relevância).
	 * 
	 * @param fields
	 *            campos e valores a serem buscados
	 * @param returnedFields
	 *            campos que serão retornados
	 * @param batchStart
	 *            a partir de que documento será retornado
	 * @param batchSize
	 *            quantos documentos serão retornados
	 * @param sortOn
	 *            campo em que a busca será ordenada
	 * @return um objeto <code>SearchResult</code>
	 * 
	 * @see SearchResult
	 */
	public SearchResult search(	Map<String, String> fields,
								List<String> returnedFields,
								int batchStart,
								int batchSize,
								String sortOn) {
		
		return search(fields, returnedFields, batchStart, batchSize, sortOn, false);
		
	}
}
