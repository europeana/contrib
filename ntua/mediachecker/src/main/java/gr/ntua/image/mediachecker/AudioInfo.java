package gr.ntua.image.mediachecker;

public class AudioInfo {
	protected int _SampleRate, _BitRate;
	protected long _Duration;
	protected String _MimeType, _FileFormat;

	public AudioInfo(String mimeType, String fileFormat, long duration, int sampleRate, int bitRate) {
		_SampleRate = sampleRate;
		_BitRate    = bitRate;
		_Duration   = duration;
		_FileFormat = fileFormat;
		_MimeType   = mimeType;
	}

	public int getSampleRate() {
		return _SampleRate;
	}

	public String getMimeType() {
		return _MimeType;
	}

	public String getFileFormat() {
		return _FileFormat;
	}

	public int getBitRate() {
		return _BitRate;
	}

	public long getDuration() {
		return _Duration;
	}
}