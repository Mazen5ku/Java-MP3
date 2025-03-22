package studiplayer.audio;

import java.util.Comparator;

public class AuthorComparator implements Comparator<AudioFile> {
    @Override
    public int compare(AudioFile f1, AudioFile f2) {
        if (f1 == null || f2 == null) {
            throw new NullPointerException(" instances cant be null");
        }

       
        if (f1.getAuthor() == null && f2.getAuthor() == null) {
            return 0;
        } else if (f1.getAuthor() == null) {
            return -1;
        } else if (f2.getAuthor() == null) {
            return 1;
        }

     // if none of them is null then :>
        return f1.getAuthor().compareTo(f2.getAuthor());
    }
}
