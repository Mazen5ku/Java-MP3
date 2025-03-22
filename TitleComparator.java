package studiplayer.audio;


import java.util.Comparator;

public class TitleComparator implements Comparator<AudioFile> {
    @Override
    public int compare(AudioFile f1, AudioFile f2) {
        if (f1 == null || f2 == null) {
            throw new NullPointerException(" instances cant be null");
        }

        if (f1.getTitle() == null && f2.getTitle() == null) {
            return 0;
        } else if (f1.getTitle() == null) {
            return -1;
        } else if (f2.getTitle() == null) {
            return 1;
        }

        return f1.getTitle().compareTo(f2.getTitle());
    }
}
