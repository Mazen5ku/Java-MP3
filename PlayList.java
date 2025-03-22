package studiplayer.audio;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PlayList implements Iterable<AudioFile> {
	private LinkedList<AudioFile> list = new LinkedList<>();
	private String search;
	private SortCriterion sortCriterion;
	private ControllablePlayListIterator iterator;

	public PlayList() {
		this.sortCriterion = SortCriterion.DEFAULT;
		this.search = "";
		resetIterator();
	}

	public PlayList(String m3uPathname) {
		this();
		loadFromM3U(m3uPathname);
	}

	public void add(AudioFile file) {
		list.add(file);
		resetIterator();
	}

	public void remove(AudioFile file) {
		list.remove(file);
		resetIterator();
	}

	public int size() {
		return list.size();
	}

	public List<AudioFile> getList() {
		return list;
	}

	public AudioFile currentAudioFile() {
		if (iterator != null) {
			return iterator.current();
		} else {
			return null;
		}
	}

	public void nextSong() {
		if (iterator == null || !iterator.hasNext()) {
			resetIterator();
		} else {
			iterator.next();
		}
	}

	public void setSearch(String search) {
		this.search = search;
		resetIterator();
	}

	public String getSearch() {
		return search;
	}

	public void setSortCriterion(SortCriterion sortCriterion) {
		this.sortCriterion = sortCriterion;
		resetIterator();
	}

	public SortCriterion getSortCriterion() {
		return sortCriterion;
	}

	public void saveAsM3U(String pathname) {
		String sep = System.getProperty("line.separator");
		try (FileWriter writer = new FileWriter(pathname)) {
			for (AudioFile file : list) {
				writer.write(file.getPathname() + sep);
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Error writing the file " + pathname + "!", e);
		}
	}

	public void loadFromM3U(String pathname) {
		list.clear();
		try (Scanner scanner = new Scanner(new FileReader(pathname))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (!line.isEmpty() && !line.startsWith("#")) {
					try {
						AudioFile audioFile = AudioFileFactory.createAudioFile(line);
						list.add(audioFile);
					} catch (NotPlayableException m) {

						m.printStackTrace();
					}
				}
			}
		} catch (IOException m) {
			throw new RuntimeException("Unable to read file " + pathname + "!", m);
		}
		resetIterator();
	}

	private void resetIterator() {
		this.iterator = new ControllablePlayListIterator(list, search, sortCriterion);
		if (iterator.hasNext()) {
			iterator.next();
		}
	}

	public void jumpToAudioFile(AudioFile audioFile) {
		if (iterator != null) {
			iterator.jumpToAudioFile(audioFile);
		}
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder("[");
		int i = 0;
		while (i < list.size()) {
			build.append(list.get(i).toString());
			if (i < list.size() - 1) {
				build.append(", ");
			}
			i++;
		}
		build.append("]");
		return build.toString();
	}

	@Override
	public Iterator<AudioFile> iterator() {
		return new ControllablePlayListIterator(list, search, sortCriterion);
	}
}
