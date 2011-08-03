package br.edu.ifpi.opala.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import util.PathTest;
import br.edu.ifpi.opala.indexing.parser.TxtParser;

public class TxtParserTest {

	@Test
	public void testParser() {
		TxtParser parser = new TxtParser();
		String content = null;
		try {
			FileInputStream is = new FileInputStream(new File(PathTest.TEXT_REPOSITORY_TEST
					.getValue()
					+ "Americana_M.txt"));
			content = parser.getContent(is);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(content.length()>0);
	}

}
