package gr.ntua.image.mediachecker;

public final class VideoInfo {

	protected final int _Width, _Height;
	protected final long _Duration;
	protected final String _MimeType, _FileFormat;
	protected final double _FrameRate;

	public VideoInfo(int width, int height, String mimetype, String fileFormat, long duration, double framerate) {
		_Width      = width;
		_Height     = height;
		_MimeType   = mimetype;
		_FileFormat = fileFormat;
		_Duration   = duration;
		_FrameRate  = framerate;
	}

	public int getWidth() {
		return _Width;
	}

	public int getHeight() {
		return _Height;
	}

	public long getDuration() {
		return _Duration;
	}

	public String getMimeType() {
		return _MimeType;
	}

	public String getFileFormat() {
		return _FileFormat;
	}

	public double getFrameRate() {
		return _FrameRate;
	}
}