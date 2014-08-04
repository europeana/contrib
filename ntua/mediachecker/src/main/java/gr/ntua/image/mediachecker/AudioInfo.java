package gr.ntua.image.mediachecker;

public final class AudioInfo {
	protected final int _SampleRate, _BitDepth, _Channels,  _BitRate;
	protected final long _Duration;
	protected final String _MimeType, _FileFormat;

	public AudioInfo(String mimeType, String fileFormat, long duration, int sampleRate, int bitDepth, int bitRate, int channels) {
		_SampleRate = sampleRate;
		_BitDepth   = bitDepth;
		_Channels   = channels;
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

	public int getBitDepth() {
		return _BitDepth;
	}

	public int getChannels() {
		return _Channels;
	}

	public long getDuration() {
		return _Duration;
	}

	@Override
	public String toString() {
		return "Sample Rate:\t" + _SampleRate + "\n" +
			"Bit Rate:\t" + _BitRate + "\n" +
			"Bit Depth:\t" + _BitDepth + "\n" +
			"Channels:\t" + _Channels + "\n" +
			"Duration:\t" + _Duration + "\n" +
			"File Format:\t" + _FileFormat + "\n" +
			"MimeType:\t"  + _MimeType;

	}
}