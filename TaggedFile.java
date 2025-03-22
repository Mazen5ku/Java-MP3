package studiplayer.audio;

import java.util.Map;

import studiplayer.basic.TagReader;

public class TaggedFile extends SampledFile {
	protected long duration;

	public TaggedFile() {

	}

	public TaggedFile(String path) throws NotPlayableException {
		super(path);
		readAndStoreTags();
	}

	public String getAlbum() {
		return album.trim();
	}

	public void readAndStoreTags() throws NotPlayableException {
		try {
			Map<String, Object> tagMap = TagReader.readTags(this.getPathname());

			this.title = tagMap.containsKey("title") ? ((String) tagMap.get("title")).trim()
					: super.getTitle() != null ? super.getTitle().trim() : "";

			this.duration = tagMap.containsKey("duration") ? Long.parseLong(tagMap.get("duration").toString())
					: super.getDuration();

			this.author = tagMap.containsKey("author") ? ((String) tagMap.get("author")).trim()
					: super.getAuthor() != null ? super.getAuthor().trim() : "";

			this.album = tagMap.containsKey("album") ? ((String) tagMap.get("album")).trim() : "";
		} catch (Exception m) {
			throw new NotPlayableException(getPathname(), "Error", m);

		}
	}

	@Override
	public String toString() {
	    StringBuilder formatBuilder = new StringBuilder();

	    formatBuilder.append(!author.isEmpty() ? author.trim() + " - " : "")
	                 .append(!title.isEmpty() ? title.trim() + " - " : "")
	                 .append(!album.isEmpty() ? album.trim() + " - " : "");

	    return formatBuilder.length() > 0 
	           ? formatBuilder.append(timeFormatter(getDuration())).toString()
	           : this.getFilename() + " - " + timeFormatter(getDuration());
	}

	@Override
	public String getTitle() {
		return title.trim();
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public long getDuration() {
		return duration;
	}
}
