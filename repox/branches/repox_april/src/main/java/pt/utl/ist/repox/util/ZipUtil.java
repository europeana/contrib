package pt.utl.ist.repox.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtil {
	public static byte[] zip(byte[] bytes) throws IOException {
		int sChunk = 8192;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream zipout = new GZIPOutputStream(out);
		byte[] buffer = new byte[sChunk];

		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		int length;
		while ((length = in.read(buffer, 0, sChunk)) != -1)
			zipout.write(buffer, 0, length);
		in.close();
		zipout.close();
		
		return out.toByteArray();
	}
	
	public static byte[] zip(String zippedString, Charset charset) throws IOException {
		return zip(zippedString.getBytes(charset));
	}

	public static byte[] unzip(byte[] zippedBytes) throws IOException {
		int sChunk = 8192;
		
		ByteArrayInputStream in = new ByteArrayInputStream(zippedBytes);
		GZIPInputStream zipin = new GZIPInputStream(in);
		byte[] buffer = new byte[sChunk];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int length;
		while ((length = zipin.read(buffer, 0, sChunk)) != -1)
			out.write(buffer, 0, length);
		out.close();
		zipin.close();
		
		return out.toByteArray();
	}
	

	public static String unzip(byte[] zippedBytes, Charset charset) throws IOException {
		return new String(unzip(zippedBytes), charset);
	}
	 
	public static void main(String[] args) throws IOException {
		String originalString = "Quero isto descomprimido com acentos";
		
		byte[] zippedValue = ZipUtil.zip(originalString, Charset.forName("ISO-8859-1"));
		String unzippedValue = new String(ZipUtil.unzip(zippedValue));
		
		System.out.println("*   zipped: " + zippedValue);
		System.out.println("* unzipped: " + unzippedValue);
	}
}
