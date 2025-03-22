package studiplayer.audio;


public abstract class AudioFile {

	protected String filename;
	private String pathname;
	private String author;
	private String title;
	protected String album;

	// constructor
	public AudioFile() {
	}

	public AudioFile(String path) throws NotPlayableException {
		parsePathname(path);
		parseFilename(filename);
	}

	// setters
	public void parsePathname(String pathname) {
		if (isWindows() == true) {

			this.pathname = pathname.replace('/', '\\'); // Replace forward slashes with backslashes for Windows paths

			int i = 0;
			while (i < this.pathname.length() - 1) {
				if (this.pathname.charAt(i) == '\\' && this.pathname.charAt(i + 1) == '\\') {
					// Replace double backslashes with a single backslash
					this.pathname = this.pathname.replace("\\\\", "\\");
					// Optionally reset i to start checking from the beginning again
					// because the string has changed in length
					i = 0;
				} else {
					i++; // Move to the next character
				}
			}

			int index = this.pathname.indexOf(":"); // check if it contains the drive opening

			if (index != -1) {
				String drive = this.pathname.substring(0, index);
				this.pathname = drive + ":" + this.pathname.substring(index + 1);
			}

			int lastslash = this.pathname.lastIndexOf('\\');
			this.pathname = this.pathname.trim(); // Remove leading/trailing spaces
			if (lastslash == -1) {
				filename = this.pathname.trim();
			} else {
				filename = this.pathname.substring(lastslash + 1);
			}
		} else {
			this.pathname = pathname.replace('\\', '/');
			this.pathname = this.pathname.replace("//", "/");
			int i = 0;
			while (i < this.pathname.length() - 1) {
				if (this.pathname.charAt(i) == '/' && this.pathname.charAt(i + 1) == '/') {

					this.pathname = this.pathname.replace("//", "/");
					i = 0;
				} else {
					i++;
				}
			}
			int index = this.pathname.indexOf(":");
			if (index != -1) {
				String drive = this.pathname.substring(0, index);
				this.pathname = "/" + drive + this.pathname.substring(index + 1);
			}

			int lastslash = this.pathname.lastIndexOf('/');
			this.pathname = this.pathname.trim(); // Remove leading/trailing spaces
			if (lastslash == -1) {
				filename = this.pathname.trim();
			} else {
				filename = this.pathname.substring(lastslash + 1);
			}
		}

		filename = filename.trim();
	}

	public void parseFilename(String filename) {
		int point = filename.lastIndexOf(".");
		if (point != -1) {
			String without_extension = filename.substring(point);
			filename = filename.replace(without_extension, "");// Remove the extension from the file name.
		}
		if (filename.equals("") || filename.equals(" ")) { // Check if the file has a name.
			author = "";
			title = "";
		} else {
			for (int i = 0; i < filename.length(); i++) {
				if (filename.charAt(i) == '-' && filename.length() > 1) {
					if (filename.charAt(filename.length() - 1) == '-') {
						author = "";
						title = "";
						break;
					}
					if (filename.charAt(i + 1) == ' ' && filename.charAt(i - 1) == ' ') {
						String part1 = filename.substring(0, i);
						String part2 = filename.substring(i + 1);
						if (part1.length() > 1) {
							author = part1.trim();
						} else {
							author = "";
						}
						if (part2.length() > 1) {
							title = part2.trim();
						} else {
							title = "";
						}
						break;
					} else {
						author = "";
						title = "";
						continue;
					}
				} else {
					author = "";
					title = filename;
					continue;
				}

			}
		}
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	}

	// Getters
	public String getPathname() {
		return pathname;
	}

	public String getFilename() {
		return filename;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	};

	public String toString() {
		if (author == "") {
			return title;
		} else {
			return author + " - " + title;
		}
	}

	public String getAlbum() {
	    return this.album == null ? "" : this.album;
	}

	public abstract void play() throws NotPlayableException;

	public abstract void togglePause();

	public abstract void stop();

	public abstract String formatDuration();

	public abstract String formatPosition();

}