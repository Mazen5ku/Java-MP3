package studiplayer.audio;


public class AudioFileFactory {
    public static AudioFile createAudioFile(String path) throws NotPlayableException {
        String extension = getFileExtension(path);

        switch (extension.toLowerCase()) {
            case "wav":
                return new WavFile(path);
            case "ogg":
            case "mp3":
                return new TaggedFile(path);
            default:
            	throw new NotPlayableException ("cant find the mentioned extension", extension);
        }
    }

    private static String getFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("Path \"" + path + "\" does not have an extension.");
        }
        return path.substring(lastDotIndex + 1);
    }
}
