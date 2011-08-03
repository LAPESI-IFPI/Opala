package br.edu.ifpi.opala.performance;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import br.edu.ifpi.opala.indexing.ImageIndexer;
import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.utils.BaseFilter;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * 
 * @author Pádua
 *
 */
public class PerformanceIndexacaoImagem {

	/**
	 * Antes de rodar o teste deve-se deletar o índice 
	 */

	public static void main(String[] args) {
		
		Util.deleteDir(new File (Path.IMAGE_INDEX.getValue()));
		PerformanceIndexacaoImagem.bmpTest();
		PerformanceIndexacaoImagem.jpgTest();
		PerformanceIndexacaoImagem.gifTest();
		PerformanceIndexacaoImagem.pngTest();

	}
	
	public static void jpgTest(){
		indexFormat(".jpg");
	}
	
	public static void bmpTest(){
		indexFormat(".bmp");
	}
		
	public static void gifTest(){
		indexFormat(".gif");
	}
	
	public static void pngTest(){
		indexFormat(".png");
	}
	
	private static void indexFormat(String format){
		File[] fileNames = new File("./WebContent/resources/image/").listFiles(new BaseFilter(format));
		ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();
		long timeIn = 0,timeOut = 0;
		ArrayList<Double> conjunto = new ArrayList<Double>();
		long soma = 0;
		
		if (format.equalsIgnoreCase(".jpg")){
			for (int i = 5000; i < fileNames.length; i++) {
				MetaDocument metaDocument = new MetaDocument();
				metaDocument.setId(fileNames[i].getName());
				ReturnMessage result = null;
				try {
					BufferedImage buf = ImageIO.read(fileNames[i]);
					if (buf != null)
						timeIn = System.currentTimeMillis();
					result = indexer.addImage(metaDocument, buf);
					timeOut = System.currentTimeMillis();
					conjunto.add((double)(timeOut-timeIn));
					soma += timeOut-timeIn;
				} catch (Exception e) {
					System.out.println(fileNames[i].getName()+" ERROR " + result);
					e.printStackTrace();
				}
				
			}
		}else{
			for (int i = 0; i < fileNames.length; i++) {
				MetaDocument metaDocument = new MetaDocument();
				metaDocument.setId(fileNames[i].getName());
				ReturnMessage result = null;
				try {
					BufferedImage buf = ImageIO.read(fileNames[i]);
					if (buf != null)
						timeIn = System.currentTimeMillis();
					result = indexer.addImage(metaDocument, buf);
					timeOut = System.currentTimeMillis();
					conjunto.add((double)(timeOut-timeIn));
					soma += timeOut-timeIn;
				} catch (Exception e) {
					System.out.println(fileNames[i].getName()+" ERROR " + result);
					e.printStackTrace();
				}
				
			}
			
		}
		
		
		new Grafico("Tempo de indexação de " +conjunto.size()+" Imagem no Formato "+format.toString(),
					conjunto, 
					new BigDecimal(new Double(soma/ (double) (fileNames.length))).setScale(2, 5).toString(),
					"ms");	
	}
	
	
}
