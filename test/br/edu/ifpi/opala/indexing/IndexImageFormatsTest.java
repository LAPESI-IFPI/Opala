package br.edu.ifpi.opala.indexing;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;
import br.edu.ifpi.opala.utils.BaseFilter;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * T
 * @author Pádua
 *
 */
public class IndexImageFormatsTest {

	/**
	 * Antes de rodar o teste deve-se deletar o índice 
	 */
	@BeforeClass
	public static void setup(){
		Util.deleteDir(new File (Path.IMAGE_INDEX.getValue()));
	}

	@Test
	public void jpgTest(){
		indexFormat(".jpg");
	}
	
	@Test
	public void bmpTest(){
		indexFormat(".bmp");
	}
	
	@Test
	public void gifTest(){
		indexFormat(".gif");
	}
	
	@Test
	public void tifTest(){
		indexFormat(".tiff");
	}
	
	@Test
	public void pngTest(){
		indexFormat(".png");
	}
	
	private static void indexFormat(String format){
		File[] fileNames = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()).listFiles(new BaseFilter(format));
		ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();
		for (int i = 0; i < fileNames.length; i++) {
			MetaDocument metaDocument = new MetaDocument();
			metaDocument.setId(fileNames[i].getName());
			ReturnMessage result = null;
			try {
				BufferedImage buf = ImageIO.read(fileNames[i]);
				if (buf != null)
				result = indexer.addImage(metaDocument, buf);
			} catch (Exception e) {
				System.out.println(fileNames[i].getName()+" ERROR " + result);
				e.printStackTrace();
			}

			Assert.assertEquals(ReturnMessage.SUCCESS, result);
		}
	}
}
