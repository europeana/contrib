package gr.ntua.image.mediachecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Enumeration;

import org.apache.tika.Tika;

import org.im4java.core.Info;
import org.im4java.core.InfoException;

import com.xuggle.xuggler.Configuration;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class MediaChecker {

	private static final String version = "0.8";

	/**
	 * Static function to get basic information about an image file
	 * @param  filename              filename of the query image
	 * @return                       an ImageInfo object with the results
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InfoException
	 */
	public static ImageInfo getImageInfo(String filename) throws IOException, FileNotFoundException, InfoException {
		Info ii           = new Info(filename, true);
		int width         = ii.getImageWidth();
		int height        = ii.getImageHeight();
		String format     = ii.getImageFormat();
		String mimeType   = MediaChecker.getMimeType(filename);

		ii                = new Info(filename);
		String colorSpace = ii.getProperty("Colorspace");

		return new ImageInfo(width, height, mimeType, format, colorSpace);
	}

	/**
	 * Static function to get basic information about a video file
	 * @param  filename                 filename of the video file
	 * @return                          a VideoInfo object with the results
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 */
	public static VideoInfo getVideoInfo(String filename) throws IOException, FileNotFoundException, IllegalArgumentException {
		IContainer container = IContainer.make();
		if (container.open(filename, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("Could not open file: " + filename);

		String mimeType  = MediaChecker.getMimeType(filename);
		int numStreams   = container.getNumStreams();
		long duration    = container.getDuration() == Global.NO_PTS ? -1 : container.getDuration() / 1000;
		int width        = 0;
		int height       = 0;
		double framerate = 0.0;
		int bitRate      = 0;
		String codec     = "";

		for (int i=0; i<numStreams; i++) {
			IStream stream     = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				width     = coder.getWidth();
				height    = coder.getHeight();
				framerate = coder.getFrameRate().getDouble();
				codec     = coder.getCodecID().toString();
				bitRate   = coder.getBitRate();
				break;
			}
		}

		return new VideoInfo(width, height, mimeType, codec.substring(9).toLowerCase(), duration, framerate, bitRate);
	}

	/**
	 * Static function to get basic information about an audio file
	 * @param  filename                 filename of the audio file
	 * @return                          an AudioInfo object with the results
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 */
	public static AudioInfo getAudioInfo(String filename) throws IOException, FileNotFoundException, IllegalArgumentException {
		IContainer container = IContainer.make();
		if (container.open(filename, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("Could not open file: " + filename);

		String mimeType = MediaChecker.getMimeType(filename);
		int numStreams  = container.getNumStreams();
		long duration   = container.getDuration() == Global.NO_PTS ? -1 : container.getDuration() / 1000;
		int channels    = 0;
		int sampleRate  = 0;
		int bitRate     = 0;
		String codec    = "";

		for (int i=0; i<numStreams; i++) {
			IStream stream     = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				channels   = coder.getChannels();
				sampleRate = coder.getSampleRate();
				bitRate    = Integer.parseInt(coder.getSampleFormat().toString().substring(5));
				codec      = coder.getCodecID().toString();
				break;
			}
		}

		return new AudioInfo(mimeType, codec.substring(9).toLowerCase(), duration, sampleRate, bitRate);
	}

	/**
	 * Function to determine if a text file is searchable.
	 * XML, RTF, ePub and text files are by definition searchable. A PDF file is considered searchable if it contains
	 * ANY text on any page.
	 * @param  filename              file to be checked
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
 	protected boolean isSearchable(String filename) throws IOException, FileNotFoundException {
		String mimeType = MediaChecker.getMimeType(filename);

		switch (mimeType) {
			case "application/xml":
			case "application/rtf":
			case "application/epub":
			case "text/plain":
				return true;
			case "application/pdf":
				PdfReader reader   = new PdfReader(filename);
				boolean searchable = false;

				String page;
				for (int i=0; i<reader.getNumberOfPages(); i++) {
					page = PdfTextExtractor.getTextFromPage(reader, i);
					if (page != null && !page.isEmpty()) {
						searchable = true;
						break;
					}
				}
				reader.close();

				return searchable;
			default:
				return false;
		}
	}

	/**
	 * Function to get the mime-type of a file.
	 * @param  filename              name of the file to be checked
	 * @return                       the mime-type
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String getMimeType(String filename) throws IOException, FileNotFoundException {
		File file         = new File(filename);
		InputStream is    = new FileInputStream(file);
		String mimeType   = new Tika().detect(file);
		is.close();

		return mimeType;
	}

	/**
	 * Function to get the size of a file in bytes
	 * @param  filename              name of the file to be checked
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static long getFileSize(String filename) throws IOException, FileNotFoundException {
		File file = new File(filename);
		return file.length();
	}

	/**
	 * Function to get the version of MediaChecker
	 * @return
	 */
	public static String getVersion() {
		return MediaChecker.version;
	}
}
