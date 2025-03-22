package studiplayer.audio;
import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile {

	public WavFile() {

	}

	public WavFile(String path) throws NotPlayableException {
		super(path);
		readAndSetDurationFromFile();
	}

	public void readAndSetDurationFromFile() throws NotPlayableException {
		try {
		WavParamReader.readParams(this.getPathname());
		float frameRate = WavParamReader.getFrameRate();
		long numberFrames = WavParamReader.getNumberOfFrames();
		duration = WavFile.computeDuration(numberFrames, frameRate);
		}
		catch (Exception m ) { 
			throw new NotPlayableException(this.getPathname(),"Error",m);
		}
	}

	@Override
	public String toString() {
		String basicString = super.toString();
		String duration = formatDuration();
		return basicString + " - " + duration;
	}

	public static long computeDuration(long numberOfFrames, float frameRate) {

		return (long) (numberOfFrames / frameRate * 1000000);

	}

	@Override
	public long getDuration() {
		return duration;
	}
}
