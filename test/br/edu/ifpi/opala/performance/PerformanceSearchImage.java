package br.edu.ifpi.opala.performance;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.junit.Assert;

import util.PathTest;
import br.edu.ifpi.opala.indexing.ImageIndexer;
import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.searching.SearcherImageImpl;
import br.edu.ifpi.opala.utils.BaseFilter;
import br.edu.ifpi.opala.utils.MetaDocument;

public class PerformanceSearchImage {

	
	
	public static void main(String[] args) {
//		PerformanceSearchImage.jpgTest();
//		PerformanceSearchImage.pngTest();
//		PerformanceSearchImage.gifTest();
		PerformanceSearchImage.bmpTest();
	}
	
	
	public void indexa() {
		String[] formats = { ".jpg", ".gif", ".png", ".bmp", "tiff" };
		for (int i = 0; i < formats.length; i++) {

			File[] fileNames = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue())
					.listFiles(new BaseFilter(formats[i]));
			ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();
			for (int j = 0; j < fileNames.length; j++) {
				MetaDocument metaDocument = new MetaDocument();
				metaDocument.setId(fileNames[j].getName());
				try {

					BufferedImage buf = ImageIO.read(fileNames[j]);
					if (buf != null)
						indexer.addImage(metaDocument, buf);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("falhou");
					Assert.fail();
				}
			}
		}
	}


	public static void jpgTest() {
		searchFormat(".jpg");
	}

	public static void bmpTest() {
		searchFormat(".bmp");
	}

	public static void gifTest() {
		searchFormat(".gif");
	}

	public static void pngTest() {
		searchFormat(".png");
	}

	private static void searchFormat(String format) {

		File[] fileNames = new File("C:\\Imagens\\")
				.listFiles(new BaseFilter(format));
		long timeIn = 0,timeOut = 0;
		ArrayList<Double> conjunto = new ArrayList<Double>();
		long soma = 0;
		for (int i = 0; i < fileNames.length; i++) {
//			MetaDocument metaDocument = new MetaDocument();
//			metaDocument.setId(fileNames[i].getName());
			try {
				BufferedImage buf = ImageIO.read(fileNames[i]);
				if (buf != null){
					timeIn = System.currentTimeMillis();
					new SearcherImageImpl().search(buf,
							fileNames.length);
					timeOut = System.currentTimeMillis();
					conjunto.add((double)timeOut-timeIn);
					soma += timeOut-timeIn;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
		new Grafico("Busca de "+fileNames.length+" Imagem "+format,conjunto,new BigDecimal(new Double(soma/(double)fileNames.length)).setScale(2,5).toString(),"ms");
	}
}
