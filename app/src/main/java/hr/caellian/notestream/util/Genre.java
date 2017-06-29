package hr.caellian.notestream.util;

import java.util.ArrayList;

import hr.caellian.notestream.R;

/**
 * Created by tinsv on 16/07/2017.
 */

public class Genre {

    public static final ArrayList<Genre> genres = new ArrayList<>();

    public static final Genre CENTURIES = new Genre("centuries", "Centuries",R.drawable.genre_80_s, 11L);
    public static final Genre ACOUSTIC = new Genre("acoustic", "Acoustic", R.drawable.genre_acoustic, 99L);
    public static final Genre BLUES = new Genre("blues", "Blues", R.drawable.genre_blues, 0L);
    public static final Genre CHILDRENS_MUSIC = new Genre("childrens", "Children's Music", R.drawable.genre_childrens_music, 132L);
    public static final Genre CHILL = new Genre("chill", "Chill", R.drawable.genre_chill, 98L);
    public static final Genre FESTIVE = new Genre("festive", "Festive", R.drawable.genre_festive, 131L);
    public static final Genre CLASSICAL = new Genre("classical", "Classical", R.drawable.genre_classical, 32L);
    public static final Genre COUNTRY = new Genre("country", "Country", R.drawable.genre_country, 2L);
    public static final Genre DISCO = new Genre("disco", "Disco", R.drawable.genre_disco, 4L);
    public static final Genre EDM = new Genre("edm", "EDM", R.drawable.genre_edm, 18L);
    public static final Genre ETHNIC = new Genre("ethnic", "Ethnic", R.drawable.genre_ethnic, 48L);
    public static final Genre EXCERCISE = new Genre("excercise", "Excercise", R.drawable.genre_excercise, 130L);
    public static final Genre GOSPEL = new Genre("gospel", "Gospel", R.drawable.genre_gospel, 38L);
    public static final Genre HIP_HOP = new Genre("hiphop", "Hip-Hop", R.drawable.genre_hip_hop, 7L);
    public static final Genre HIPPIE = new Genre("hippie", "Hippie", R.drawable.genre_hippie, 129L);
    public static final Genre INDIE = new Genre("indie", "Indie", R.drawable.genre_indie, 128L);
    public static final Genre JAZZ = new Genre("jazz", "Jazz", R.drawable.genre_jazz, 8L);
    public static final Genre K_POP = new Genre("kpop", "K-Pop", R.drawable.genre_k_pop, 127L);
    public static final Genre LATIN = new Genre("latin", "Latin", R.drawable.genre_latin, 86L);
    public static final Genre METAL = new Genre("metal", "Metal", R.drawable.genre_metal, 9L);
    public static final Genre POP = new Genre("pop", "Pop", R.drawable.genre_pop, 13L);
    public static final Genre PUNK = new Genre("punk", "Punk", R.drawable.genre_punk, 43L);
    public static final Genre RNB = new Genre("rnb", "RnB", R.drawable.genre_rnb, 14L);
    public static final Genre REGGAE = new Genre("reggae", "Reggae", R.drawable.genre_reggae, 16L);
    public static final Genre ROCK = new Genre("rock", "Rock", R.drawable.genre_rock, 17L);
    public static final Genre ROMANTIC = new Genre("romantic", "Romantic", R.drawable.genre_romantic, 126L);
    public static final Genre TANGO = new Genre("tango", "Tango", R.drawable.genre_tango, 113L);
    public static final Genre TRENDING = new Genre("trending", "Trending", R.drawable.genre_trending, 60L);
    public static final Genre VOCAL = new Genre("vocal", "Vocal", R.drawable.genre_vocal, 28L);

    protected final String id;
    protected final String name;
    protected final int resourceIcon;
    protected final long ID3ID;

    public Genre(String id, String name, int resourceIcon, long ID3ID) {
        this.id = id;
        this.name = name;
        this.resourceIcon = resourceIcon;
        this.ID3ID = ID3ID;
        genres.add(this);
    }

    public static void removeGenre(Genre genre){
        genres.remove(genre);
    }
}
