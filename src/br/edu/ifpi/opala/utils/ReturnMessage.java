package br.edu.ifpi.opala.utils;

/**
 * Retornos de métodos de indexação e busca
 * @author Dannylvan
 */
public enum ReturnMessage {
	
	SUCCESS("success", 100),
//	SUCCESS_UPDATE("success no update", 101),
//	SUCCESS_ADD("success na adição de novo campo do documento", 102),
	
	EMPTY_SEARCHER("Nenhum item encontrado", 200),
	ID_NOT_FOUND("Não foi possível encontrar um documento com o id informado", 201),
	DUPLICATED_ID("ERROR: id duplicado - já existe objeto com este identificador no índice.", 202),
	PARAMETER_INVALID("ERROR: Parâmetro Inválido!", 203),
	EMPTY_ID("ID vazio", 204),
	EMPTY_CONTENT("Conteúdo vazio", 205),
	
	FORMAT_EXCEPTION("Formato Inválido",300),
//	CORRUPT_IMAGE("Imagem Corrompida",301),
	INVALID_QUERY("Busca inválida", 301),
	UNSORTABLE_FIELD("Não foi possível ordenar a busca pelo campo informado", 302),
	XML_PARSE_EXCEPTION("Não foi possível interpretar o XML enviado", 303),
	
	EMPTY_INDEX("índice Vazio", 400),	
	INDEX_LOCK("índice Bloqueado",401),
	CORRUPT_INDEX("índice corrompido",402),
	INDEX_NOT_FOUND("índice Inexistente", 403),
	
	UNEXPECTED_ERROR("Erro inesperado",500),
	UNEXPECTED_SEARCH_ERROR("Um erro inesperado ocorreu durante o processo de busca",501),
	UNEXPECTED_SORT_ERROR("Um erro inesperado ocorreu durante o processo de ordenação",502),
	UNEXPECTED_INDEX_ERROR("Um erro inesperado ocorreu durante o acesso ao índice e ele foi recuperado",503),
	UNEXPECTED_BACKUP_ERROR("Um erro inesperado ocorreu durante o acesso ao backup e ele foi recuperado",504),
	OUTDATED("O indice estava desatualizado, medidas necessarias foram tomadas",505);

	public final String message;
	public final int code;

	/**
	 * Construtor da enum
	 * @param message - Mensagem de retorno
	 * @param codigo - Código da mensagem
	 */
	ReturnMessage(String message, int codigo) {
		this.message = message;		
		this.code = codigo;
	}

	/**
	 * Retorna a mensagem de retorno da Enum
	 * @return mensagem da enum
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Retorna o código de retorno da Enum
	 * @return código do ReturnMessage
	 */
	public int getCode() {
		return code;
	}
	/**
	 * Devolve a Enum equivalente ao código recebido.
	 * 
	 * @param code
	 * @return ReturnMessage
	 */
	public static ReturnMessage getReturnMessage(int code) {
		ReturnMessage[] enums = ReturnMessage.values();
		for (int i = 0; i < enums.length; i++) {
			if(enums[i].getCode() == code) {
				return enums[i];
			}
		}
		return null;
	}
}
