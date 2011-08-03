package br.edu.ifpi.opala.indexing;

import java.awt.image.BufferedImage;
import java.util.Map;

import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.ReturnMessage;

/**
 * Interface que especifica como fazer alteração no índice de 
 * imagem como:
 * 		<ul> 
 * 		<li>Adicionar Imagem.</li> 
 * 		<li>Deletar Imagem.</li>
 * 		<li>Otimizar o índice.</li>
 * 		<li>Fazer Backup do índice.</li>
 * 		</ul>
 * @author Pádua
 * @author Mônica
 */
public interface ImageIndexer {


	/** Assinatura para o método que realiza a adição da imagem no índice.
	 * 
	 * @param metaDocument metadados associados à imagem. 
	 * @param image conteúdo da imagem a ser indexada
	 * @return {@link ReturnMessage#SUCCESS} se a imagem foi adicionada
	 */
	public ReturnMessage addImage(MetaDocument metaDocument, BufferedImage image);
	
	/**
	 * Assinatura para o método que remove a imagem do índice.
	 * 
	 * @param id identificador único da imagem no índice.            
	 * @return {@link ReturnMessage#SUCCESS} se a imagem foi removida
	 * 
	 */
	public ReturnMessage delImage(String id);

	/**
	 * Assinatura para o método que recupera o índice com o backup mais novo
	 */
	public void restoreIndex();
	
	/**
	 * Assinatura para o método que atualiza uma imagem no índice
	 * @return {@link ReturnMessage#SUCCESS} se a imagem foi atualizada
	 */
	public ReturnMessage updateImage(String id, Map<String, String> metaDocument);
}
