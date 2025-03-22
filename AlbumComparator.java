package studiplayer.audio;


import java.util.Comparator;

public class AlbumComparator implements Comparator<AudioFile> {


    public int compare(AudioFile f1, AudioFile f2) {
        if (f1 == null || f2 == null) {
            throw new NullPointerException(" instances cant be null");
        }

        String album1 = null;
        if (f1 instanceof TaggedFile) {
            album1 = ((TaggedFile) f1).getAlbum();
        }

        String album2 = null;
        if (f2 instanceof TaggedFile) {
            album2 = ((TaggedFile) f2).getAlbum();
        }

   
        if (album1 == null && album2 == null) {
            return 0;
        } else if (album1 == null) {
            return -1;
        } else if (album2 == null) {
            return 1;
        }

        return album1.compareTo(album2);
    }
}
