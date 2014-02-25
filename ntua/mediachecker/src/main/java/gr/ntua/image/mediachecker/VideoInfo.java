package gr.ntua.image.mediachecker;

public class VideoInfo {

	protected int _Width, _Height;
	protected long _Duration;
	protected String _MimeType, _FileFormat;
	protected double _FrameRate;

	public VideoInfo(int width, int height, String mimetype, String fileFormat, long duration, double framerate) {
		_Width      = width;
		_Height     = height;
		_MimeType   = mimetype;
		_FileFormat = fileFormat;
		_Duration   = duration;
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