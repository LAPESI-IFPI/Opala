package br.edu.ifpi.opala.searching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.edu.ifpi.opala.utils.Metadata;
/**
 * Classe respons�vel pelo teste da classe MetadataCompare
 * @author Lidijanne e Múnica
 *
 */
public class MetadataCompareTest {
	static ResultItem resultItem1 = new ResultItem();
	static Map<String, String> fields1 = new HashMap<String, String>();;
	static Map<String, String> fields2 = new HashMap<String, String>();;
	static ResultItem resultItem2 = new ResultItem();

	/**
	 *Prepara os searchResults para cada Método
	 */
	@Before
	public void setUp(){
		fields1.put(Metadata.PUBLICATION_DATE.getValue(), "22/08/2010");
		fields1.put(Metadata.AUTHOR.getValue(), "Borges, Eduardo");
		fields1.put(Metadata.TITLE.getValue(), "Uma Abordagem Efetiva para verificação de documentos duplicados");
		resultItem1.setFields(fields1);
		
		fields2.put(Metadata.PUBLICATION_DATE.getValue(), "22/08/2010");
		fields2.put(Metadata.AUTHOR.getValue(), "Borges, Eduardo");
		fields2.put(Metadata.TITLE.getValue(), "Uma Abordagem Efetiva para verificação de documentos duplicados");
//		fields2.put(Metadata.TITLE.getValue(), "Uma Abordagem Efetiva de documentos duplicados");
		resultItem2.setFields(fields2); 
	}

	/**
	 * Teste que verifica se dois searchResults iguais retornam True.
	 */
	@Test
	public void testIdenticalResusltItem() {
		assertTrue(MetadataCompare.metadataMatch(resultItem1,resultItem1));
	}

	/**
	 * Testa se a comparação entre um searchResult com campos e um valor null retorna false
	 */
	@Test
	public void testResultItemNull(){
		assertFalse(MetadataCompare.metadataMatch(resultItem1, null));
	}

	/**
	 * Teste que verifica se o método metadataMatch da classe MetadataCompare retorna false caso a diferença entre as datas 
	 * de publicationDate seja maior que um ano.
	 */
	@Test
	public void testResultItemWithPublicationDateDiffGreaterThanOneYear(){
		fields2.put(Metadata.PUBLICATION_DATE.getValue(), "22/09/2008");
		assertFalse(MetadataCompare.metadataMatch(resultItem1, resultItem2));

	}

	/**
	 * Teste que verifica se o método metadataMatch da classe MetadataCompare retorna true caso a diferença entre as datas 
	 * de publicationDate seja menor que um ano.
	 */
	@Test
	public void testResultItemWithPublicationDateDiffLesserThanOneYear(){
		fields2.put(Metadata.PUBLICATION_DATE.getValue(), "23/08/2009");
		assertTrue(MetadataCompare.metadataMatch(resultItem1, resultItem2));

	}

	/**
	 * Teste que verifica se o método metadataMatch da classe MetadaCompare retorna false caso a comparação com apenas
	 * uma única palavra.
	 */
	@Test
	public void testResultItemOneWordNameAuthorEqualsInicials(){
		fields2.put(Metadata.AUTHOR.getValue(), "   Eduardo   ");
		assertFalse(MetadataCompare.metadataMatch(resultItem1, resultItem2));

	}

	/**
	 * Teste que verifica se o método metadataMatch retorna true com nome completo do autor escrito em ordem diferente.
	 * Verifica se funciona com espaços em brancos entre os nomes. 
	 */
	@Test
	public void testResultItemAuthorEqualsInicials(){
		fields2.put(Metadata.AUTHOR.getValue(), "   Eduardo  Nunes       Borges");
		assertTrue(MetadataCompare.metadataMatch(resultItem1, resultItem2));

	}

	/**
	 * Teste que verifica se o método metadataMatch retorna false com mesma quantidade de nomes mas nome diferentes.
	 */
	@Test
	public void testResultItemAuthorDifferentInicials(){
		fields2.put(Metadata.AUTHOR.getValue(), "Eduardo Nunes");
		assertFalse(MetadataCompare.metadataMatch(resultItem1, resultItem2));
	}
	
	/**
	 * Teste que verifica se o método metadataMatch retorna true o das iniciais
	 * dos nomes dos autores nos dois documentos correspondem em mais de 60%.
	 */
	@Test
	public void testResultItemNameAuthorsEqualsInicials(){
		fields1.put(Metadata.AUTHOR.getValue(), "Fernandes, Elaine ; Borges Andre ");
		fields2.put(Metadata.AUTHOR.getValue(), "Andre Borges ; Carlos Daniel ; Elaine Fernandes ");
		assertTrue(MetadataCompare.metadataMatch(resultItem1, resultItem2));
	}

	/**
	 * Teste que verifica se o método metadataMatch da classe MetadaCompare retorna true caso a comparação dos títulos 
	 * nos dois documentos corresponda entre 75% a 100%.
	 */
	@Test
	public void testLevenshteinTrue(){
		fields1.put(Metadata.TITLE.getValue(), "Uma Abordagem Efetiva para verificação de documentos duplicados ");
		fields2.put(Metadata.TITLE.getValue(), "UMA   ABORDA EFETIVA VERIFICAÇÃO DE DOCUMENTOS DUPLICADOS");
		assertTrue(MetadataCompare.metadataMatch(resultItem1, resultItem2));
	}
	
	/**
	 * Teste que verifica se o método metadataMatch da classe MetadaCompare retorna false caso a comparação dos títulos 
	 * nos dois documentos correspondem em menos que 75% e mais que 100%.
	 */
	@Test
	public void testLevenshteinFalse(){
		fields1.put(Metadata.TITLE.getValue(), " Uma Abordagem Efetiva para verificação de documentos duplicados ");
		fields2.put(Metadata.TITLE.getValue(), "Como calcular  função de similaridade para strings");
		assertFalse(MetadataCompare.metadataMatch(resultItem1, resultItem2));
	}



}
