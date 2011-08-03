package br.edu.ifpi.opala.searching;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;

/**
 * Classse que implementa os metodos de busca de imagens por similaridade a uma
 * determinada imagem passada como exemplo.
 * 
 * @author <a href="mailto:paduafil@gmail.com">Pádua Furtado</a>
 */

public class SearcherImageImpl implements
		SearcherImage {

	private static ImageSearch imageSearch = new ImageSearch(Path.IMAGE_INDEX.getValue());
	private static MetadataSearch metadataSearch = new MetadataSearch(Path.IMAGE_INDEX.getValue());
	
	
	public static ImageSearch getImageSearch() {
		return imageSearch;
	}


	public static void setImageSearch(ImageSearch imageSearch) {
		SearcherImageImpl.imageSearch = imageSearch;
	}


	public static MetadataSearch getMetadataSearch() {
		return metadataSearch;
	}


	public static void setMetadataSearch(MetadataSearch metadataSearch) {
		SearcherImageImpl.metadataSearch = metadataSearch;
	}


	/**
	 * Método reponsável pela busca por conteúdo de uma determinada imagem no
	 * índice.
	 * 
	 * @param image <code>BufferedImage</code> exemplo para a busca
	 * @param limit quantidade de imagens retornadas
	 * @return SearchResult objeto que contém um ReturnMessage que indica o
	 *         resultado da operação e uma lista de ItemResult.
	 */
	public SearchResult search(BufferedImage image, int limit) {
		SearchResult sr = imageSearch.search(image, limit);
		if (sr.getCodigo().equals(ReturnMessage.UNEXPECTED_INDEX_ERROR))
			ImageIndexerImpl.getImageIndexerImpl().restoreIndex();
		return sr;
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
	public SearchResult search(Map<String, String> fields,
			List<String> returnedFields, int batchStart, int batchSize,
			String sortOn, boolean reverse) {
		SearchResult sr = metadataSearch.search(fields, returnedFields, batchStart, batchSize, sortOn, reverse);
		if (sr.getCodigo().equals(ReturnMessage.UNEXPECTED_INDEX_ERROR)) {
			ImageIndexerImpl.getImageIndexerImpl().restoreIndex();
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
	public SearchResult search(Map<String, String> fields,
			List<String> returnedFields, int batchStart, int batchSize,
			String sortOn) {
		return search(fields, returnedFields, batchStart, batchSize, sortOn,
				false);
	}
	
}
