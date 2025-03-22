package studiplayer.audio;

import java.util.Comparator;

public class DurationComparator implements Comparator<AudioFile> {

	@Override
	public int compare(AudioFile f1, AudioFile f2) {
		if (f1 == null || f2 == null) {
			throw new RuntimeException(" instances cant be null");
		}

		Long duration1 = null;
		if (f1 instanceof SampledFile) {
			duration1 = ((SampledFile) f1).getDuration();
		}

		Long duration2 = null;
		if (f2 instanceof SampledFile) {
			duration2 = ((SampledFile) f2).getDuration();
		}

		if (duration1 == null && duration2 == null) {
			return 0;
		} else if (duration1 == null) {
			return -1;
		} else if (duration2 == null) {
			return 1;
		}
// if none of them is null then :>
		return duration1.compareTo(duration2);
	}
}
