package br.edu.ifpi.opala.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Faz conversões bastante utilizadas no projeto.
 * 
 * @author Pádua
 *
 */
public class Conversor {

	/**
	 * Transforma um File em array de bytes
	 * @param file - arquivo a ser convertido
	 * @return array de bytes correspondente ao arquivo
	 * @throws IOException
	 */
	public static byte[] fileToByteArray(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buf = new byte[(int) file.length()];

		int numBytesRead = 0;
		while ((numBytesRead = input.read(buf)) != -1) {
			output.write(buf, 0, numBytesRead);
		}
		
		output.close();
		input.close();
		
		return output.toByteArray();
	}
	
	/**
	 * Transforma um array de bytes em um File
	 * @param byteArray - vetor de bytes correspondente a um File
	 * @return arquivo criado
	 * @throws IOException
	 */
	public static File byteArrayToFile(byte[] byteArray) throws IOException {
		File file = File.createTempFile("temp", null);
		FileOutputStream output = new FileOutputStream(file);
		output.write(byteArray, 0, byteArray.length);
		output.close();
		
		return file;
	}
	
	/**
	 * Transforma um array de bytes em uma imagem
	 * @param byteArray - vetor de bytes correspondente a um BufferedImage
	 * @return um objeto BufferedImage retornado
	 */
	public static BufferedImage byteArrayToBufferedImage(byte[] byteArray) {
		ByteArrayInputStream is = new ByteArrayInputStream(byteArray);
		BufferedImage imageBuf = null;
		try {
			imageBuf = ImageIO.read(is);
			is.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return imageBuf;		
	}
	
	/**
	 * Transfora um array de bytes em uma imagem BMP
	 * @param byteArray - vetor de bytes correspondente a uma imagem BMP
	 * @return BufferedImage correspondete a uma imagem BMP
	 * @throws IOException
	 */
	public static BufferedImage byteArrayToBMP(byte[] byteArray) throws IOException {
		ByteArrayInputStream inb = new ByteArrayInputStream(byteArray);
		ImageReader rdr = (ImageReader) ImageIO.getImageReadersByFormatName("bmp").next();
		ImageInputStream imageInput = ImageIO.createImageInputStream(inb);
		rdr.setInput(imageInput);
		BufferedImage biParaFecharImageInput = rdr.read(0); 
		imageInput.close();
		return biParaFecharImageInput;
	}
	
	/**
	 * Transforma um objeto BufferedImage para array de bytes
	 * @param image - bufferedImage a ser convertido
	 * @return vetor de byte correspondente ao BufferedImage passado
	 * @throws IOException
	 */
	public static byte[] BufferdImageToByteArray(BufferedImage image) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		byte[] byteArray = baos.toByteArray();
		baos.close();
		return byteArray;
	}
	
	/**
	 * Transforma um InputStream em um array de bytes
	 * @param input - InputStream a ser convertido
	 * @return vetor de bytes correspondente ao InputStream
	 * @throws IOException
	 */
	public static byte[] InputStreamToByteArray(InputStream input) throws IOException{
		int i = 0;
		while (input.read()!= -1) {
			i++;
		}
		byte[] result = new byte[i];
		input.reset();
		input.read(result);
		return result;		
	}
	
	/**
	 * Transforma um InputStream em String
	 * @param in - InputStream a ser convertido
	 * @return String correspondente ao InputStream
	 * @throws IOException
	 */
	public static String InputStreamToString(InputStream in) throws IOException{
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader bufReader = new BufferedReader(reader);
		StringBuffer buffer= new StringBuffer();
		String linha = null;
		
		while ((linha = bufReader.readLine())!= null) {
			buffer.append(linha);
		}
		
		reader.close();
		bufReader.close();
		
		return buffer.toString();
	}
}