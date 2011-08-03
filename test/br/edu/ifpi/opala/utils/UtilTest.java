package br.edu.ifpi.opala.utils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;

import util.PathTest;
import util.UtilForTest;


/**
 * A classe testa os Métodos da classe Util do pacote utils
 * @author Pádua
 */
public class UtilTest {

	/**
	 * Método que testa a indexação deTesta se o Método em questão "indexTextDirOrFileTest" está indexando 
	 * O Método testa se o diret�rio existe 
	 * Testa se o diret�rio é nulo 
	 * Testa a execução do Método, isto �, se indexa sem retornar nulo. 
	 * não está sendo testado com nenhuma busca.
	 */
	@Test
	public void indexTextDirOrFileTest() {
		File textIndex = new File(Path.TEXT_INDEX.getValue());
		assertTrue(Util.deleteDir(textIndex));

		assertTrue(new File(PathTest.TEXT_REPOSITORY_TEST.getValue())
		.exists());
		assertFalse(PathTest.TEXT_REPOSITORY_TEST.getValue().isEmpty());
		
		File fileOfDiretorio = new File(PathTest.TEXT_REPOSITORY_TEST.getValue());
		
		assertTrue(UtilForTest.indexTextDirOrFile(fileOfDiretorio));
	}
	
	
	/**
	 * Método que testa a cópia de uma pasta recebida como 
	 * parâmetro para outra pasta informada.
	 * @throws IOException 
	 */
	@Test
	public void copyIndexTest() throws IOException {
		File inIndexDir = new File(Path.TEXT_INDEX.getValue());
		File outIndexDir = new File(Path.TEXT_BACKUP.getValue()+"/teste");
		assertTrue(Util.deleteDir(outIndexDir));

		Util.copyIndex(inIndexDir, outIndexDir);

		File[] indexFiles = inIndexDir.listFiles();
		File[] backupFiles = outIndexDir.listFiles();

		assertEquals(indexFiles.length, backupFiles.length);
		
		//Às vezes ocorre um erro em que um arquivo é copiado com 
		//tamanho diferente do original
		for (int i = 0; i < indexFiles.length; i++) {
			assertEquals(indexFiles[i].getName(), backupFiles[i].getName());
			assertEquals(indexFiles[i].length(), backupFiles[i].length());
		}
		assertTrue(Util.deleteDir(outIndexDir));
	}

	/**
	 * Método que testa apagar o arquivo ou o diretório informado recursivamente
	 */
	@Test
	public void deleteDirTest() {
		Path indexDirPathTest = Path.TEXT_INDEX;
		File indexDir = new File(indexDirPathTest.getValue());
		
		assertTrue(Util.deleteDir(indexDir));
	}

	
}

