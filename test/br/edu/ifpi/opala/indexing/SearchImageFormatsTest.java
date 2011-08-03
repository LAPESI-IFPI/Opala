package br.edu.ifpi.opala.indexing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;
import br.edu.ifpi.opala.searching.SearchResult;
import br.edu.ifpi.opala.searching.SearcherImageImpl;
import br.edu.ifpi.opala.utils.BaseFilter;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * Classe que testa a possibilidade de indexação de imagem nos formatos de
 * "jpg, gif, png, bmp e tiff". Tenta indexar as imagens contidas no reposit�rio
 * 
 * @author Dannylvan, Padua
 * 
 */

public class SearchImageFormatsTest {

	@BeforeClass
	public static void setup() throws IOException {
		assertTrue(Util.deleteDir(new File(Path.IMAGE_INDEX.getValue())));

		String[] formats = { ".jpg", ".gif", ".png", ".bmp", "tiff" };
		for (int i = 0; i < formats.length; i++) {

			File[] fileNames = new File(PathTest.IMAGE_REPOSITORY_TEST
					.getValue()).listFiles(new BaseFilter(formats[i]));
			ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();
			for (int j = 0; j < fileNames.length; j++) {
				MetaDocument metaDocument = new MetaDocument();
				metaDocument.setId(fileNames[j].getName());
				BufferedImage buf = ImageIO.read(fileNames[j]);
				if (buf != null) {
					ReturnMessage result = indexer.addImage(metaDocument, buf);
					assertEquals(ReturnMessage.SUCCESS, result);
				}
			}
		}

	}

	@Test
	public void jpgTest() {
		searchFormat(".jpg");
	}

	@Test
	public void bmpTest() {
		searchFormat(".bmp");
	}

	@Test
	public void gifTest() {
		searchFormat(".gif");
	}

	@Test
	public void tifTest() {
		searchFormat(".tiff");
	}

	@Test
	public void pngTest() {
		searchFormat(".png");
	}

	private static void searchFormat(String format) {

		File[] fileNames = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue())
				.listFiles(new BaseFilter(format));

		for (int i = 0; i < fileNames.length; i++) {
			MetaDocument metaDocument = new MetaDocument();
			metaDocument.setId(fileNames[i].getName());
			SearchResult result = null;
			try {
				BufferedImage buf = ImageIO.read(fileNames[i]);
				if (buf != null)
					result = new SearcherImageImpl().search(buf,
							fileNames.length);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}

			assertEquals(ReturnMessage.SUCCESS, result.getCodigo());
			assertEquals(fileNames[i].getName(), result.getItem(0).getId());
		}

	}
}
