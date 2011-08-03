package br.edu.ifpi.opala.indexing;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;

import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.ReturnMessage;

/**
 * <code>TextIndexer</code> Interface que faz alterações no índice de 
 * texto como: 
 * 		Adicionar Texto. 
 * 		Deletar Texto.
 * 		Otimizar o Indice.
 * 		Fazer Backup do Indice.
 * 
 * @author Monica
 *
 */
public interface TextIndexer {
	
	/**
	 * Assinatura para o método que realiza a adição da Texto no índice.
	 * 
	 * @param metaDocument - os metadados associados ao texto.
	 * @param content - o conteúdo do texto a ser indexado
	 * @return SUCCESS se o texto foi adicionado, <<Messagem de erro>> se
	 *  o id for duplicado ou ocorrer algum erro durante a operação.
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws CorruptIndexException 
	 */
	public ReturnMessage addText(MetaDocument metaDocument, String content);
	
	/**
	 * Assinatura para o método que remove o texto do índice.
	 * 
	 * @param identifier - O identificador único do texto no índice.            
	 * @return ReturnMessage.SUCCESS se o texto removido, <<Messagem de erro>> se o texto não for
	 *         encontrado ou ocorrer algum erro durante a operação.
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * 
	 * 
	 */
	public ReturnMessage delText(String identifier) ;
	
		
	/**
	 * Assinatura para o método que atualiza um documento no índice
	 * @return SUCCESS se o documento for atualizado, <<Messagem de erro>> se o texto não for
	 *         encontrado ou ocorrer algum erro durante a operação.
	 * @throws CorruptIndexException, IOException 
	 */
	public ReturnMessage updateText(String id, Map<String, String> metaDocument);

}
