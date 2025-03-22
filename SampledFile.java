package studiplayer.audio;

import studiplayer.basic.BasicPlayer;

public abstract class SampledFile extends AudioFile {
	protected long duration;
	protected String title;
	protected String author;
	protected String album;

	public SampledFile() {

	}

	public SampledFile(String path) throws NotPlayableException {
		super(path);
	}

	@Override
	public void play() throws NotPlayableException {
		BasicPlayer.play(super.getPathname());

	}

	@Override
	public void togglePause() {
		BasicPlayer.togglePause();
	}

	@Override
	public void stop() {
		BasicPlayer.stop();

	}

	@Override
	public String formatDuration() {

		long durationInMicroseconds = getDuration(); // getDuration() should return the duration in microseconds
		return timeFormatter(durationInMicroseconds);

	}

	public String formatPosition() {

		long currentPositionInMicroseconds = BasicPlayer.getPosition();

		return timeFormatter(currentPositionInMicroseconds);
	}

	public static String timeFormatter(long timeInMicroSeconds) {
		if (timeInMicroSeconds < 0) {
			throw new IllegalArgumentException("Time value cannot be negative");
		}
		int timeInSeconds = (int) (timeInMicroSeconds / 1000000);

		int minutes = timeInSeconds / 60;
		int seconds = timeInSeconds % 60;
		if (minutes >= 100) { // changing the condition here.
			throw new IllegalArgumentException("Time value is too large to format");
		}
		return String.format("%02d:%02d", minutes, seconds);
	}

	public long getDuration() {
		return this.duration;
	}
}