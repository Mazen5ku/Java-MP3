package studiplayer.audio;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ControllablePlayListIterator implements Iterator<AudioFile> {
	private List<AudioFile> audioFiles;
	private int currentIndex;

	public ControllablePlayListIterator(List<AudioFile> audioFiles) {
		this.audioFiles = new LinkedList<>(audioFiles);
		this.currentIndex = 0;
	}

	public ControllablePlayListIterator(List<AudioFile> audioFiles, String search, SortCriterion sortCriterion) {
	    // Filter based on search
	    if (search != null && !search.isEmpty()) {
	        List<AudioFile> filteredList = new LinkedList<>();
	        for (AudioFile file : audioFiles) {
	            boolean matches = 
	                (file.getAuthor() != null && file.getAuthor().contains(search)) ||
	                (file.getTitle() != null && file.getTitle().contains(search)) ||
	                (file.getAlbum() != null && file.getAlbum().contains(search));

	            if (matches) {
	                filteredList.add(file);
	            }
	        }
	        this.audioFiles = filteredList;
	    } else {
	        this.audioFiles = new LinkedList<>(audioFiles);
	    }

	    // Sort based on sortCriterion
	    if (sortCriterion == SortCriterion.AUTHOR) {
	        this.audioFiles.sort(new AuthorComparator());
	    } else if (sortCriterion == SortCriterion.TITLE) {
	        this.audioFiles.sort(new TitleComparator());
	    } else if (sortCriterion == SortCriterion.ALBUM) {
	        this.audioFiles.sort(new AlbumComparator());
	    } else if (sortCriterion == SortCriterion.DURATION) {
	        this.audioFiles.sort(new DurationComparator());
	    }

	    this.currentIndex = 0;
	}

	@Override
	public boolean hasNext() {
		return currentIndex < audioFiles.size();
	}

	@Override
	public AudioFile next() {
		if (!hasNext()) {
			return null;
		}
		return audioFiles.get(currentIndex++);
	}

	public AudioFile current() {
		if (currentIndex == 0) {
			return null;
		}
		return audioFiles.get(currentIndex - 1);
	}

	public void reset() {
		this.currentIndex = 0;
	}

	public AudioFile jumpToAudioFile(AudioFile audioFile) {
		int argument = audioFiles.indexOf(audioFile);
		if (argument == -1) {
			return null;
		}
		this.currentIndex = argument + 1;
		return audioFile;
	}
}
