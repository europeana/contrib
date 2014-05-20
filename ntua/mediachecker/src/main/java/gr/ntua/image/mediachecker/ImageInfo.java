package gr.ntua.image.mediachecker;

public final class ImageInfo {

	protected final int _Width, _Height;
	protected final String _MimeType, _FileFormat, _ColorSpace;

	public ImageInfo(int width, int height, String mimeType, String fileFormat, String colorSpace) {
		_Width      = width;
		_Height     = height;
		_MimeType   = mimeType;
		_FileFormat = fileFormat;
		_ColorSpace = colorSpace;
	}

	public int getWidth() {
		return _Width;
	}

	public int getHeight() {
		return _Height;
	}

	public String getMimeType() {
		return _MimeType;
	}

	public String getFileFormat() {
		return _FileFormat;
	}

	public String getColorSpace() {
		return _ColorSpace;
	}

	public String toString() {
		String out = "Mime Type: " + _MimeType + "\n" +
			"File Format: " + _FileFormat + "\n" +
			"Width: " + _Width + "\n" +
			"Height: " + _Height + "\n" +
			"Colorspace: " + _ColorSpace + "\n";

		return out;
	}

}