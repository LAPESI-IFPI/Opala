package br.edu.ifpi.opala.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import br.edu.ifpi.opala.backup.BackuperImageTest;
import br.edu.ifpi.opala.backup.BackuperTextTest;
import br.edu.ifpi.opala.indexing.ImageIndexerTest;
import br.edu.ifpi.opala.indexing.IndexImageFormatsTest;
import br.edu.ifpi.opala.indexing.SearchImageFormatsTest;
import br.edu.ifpi.opala.indexing.TxtParserTest;
import br.edu.ifpi.opala.searching.MetadataCompareTest;
import br.edu.ifpi.opala.searching.NearRealTimeTextSearcherTestTest;
import br.edu.ifpi.opala.searching.SearcherImageTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	BackuperImageTest.class,
	
	BackuperTextTest.class,
	
	ImageIndexerTest.class,
	
	IndexImageFormatsTest.class,
	
	SearchImageFormatsTest.class,
	
	TxtParserTest.class,

	MetadataCompareTest.class,
	
	NearRealTimeTextSearcherTestTest.class,
	
	SearcherImageTest.class,
	
})

/**
 * Esta classe executa todos os métodos de testes contidos nas classes de teste 
 * definida acima devendo ser executado como "JUnit Test"
 *  
 * @author Tavares, Mônica 
 *
 */
public class SuiteOpalaTest {
	
}
