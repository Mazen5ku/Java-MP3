package studiplayer.audio;

public class NotPlayableException extends Exception {
	private String pathname;

	public NotPlayableException(String path, String m) {
		super(m);
		this.pathname = path;
	}

	public NotPlayableException(String path, Throwable t) {
		super(t);
		this.pathname = path;
	}

	public NotPlayableException(String path, String m, Throwable t) {
		super(m, t);
		this.pathname = path;
	}

	@Override
	public String toString() {
		return "studiplayer.audio.NotPlayableException: Pathname - " + pathname + ", Message - " + getMessage();
	}

	public String getPathname() {
		return pathname;
	}
}
