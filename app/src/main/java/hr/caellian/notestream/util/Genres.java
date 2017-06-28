package hr.caellian.notestream.util;

import hr.caellian.notestream.R;

/**
 * Created by caellyan on 26/06/17.
 */

public enum Genres {
    // TODO: Incorporate all ID3 tag genres?
    // TODO: Turn into a class with ArrayList containing registered genres, GUI to add custom genres (+ select icon)...

    CENTURIES("centuries", "Centuries", R.drawable.genre_80_s, 11L),
    ACOUSTIC("acoustic", "Acoustic", R.drawable.genre_acoustic, 99L),
    BLUES("blues", "Blues", R.drawable.genre_blues, 0L),
    CHILDRENS_MUSIC("childrens", "Children's Music", R.drawable.genre_childrens_music, 132L),
    CHILL("chill", "Chill", R.drawable.genre_chill, 98L),
    FESTIVE("festive", "Festive", R.drawable.genre_festive, 131L),
    CLASSICAL("classical", "Classical", R.drawable.genre_classical, 32L),
    COUNTRY("country", "Country", R.drawable.genre_country, 2L),
    DISCO("disco", "Disco", R.drawable.genre_disco, 4L),
    EDM("edm", "EDM", R.drawable.genre_edm, 18L),
    ETHNIC("ethnic", "Ethnic", R.drawable.genre_ethnic, 48L),
    EXCERCISE("excercise", "Excercise", R.drawable.genre_excercise, 130L),
    GOSPEL("gospel", "Gospel", R.drawable.genre_gospel, 38L),
    HIP_HOP("hiphop", "Hip-Hop", R.drawable.genre_hip_hop, 7L),
    HIPPIE("hippie", "Hippie", R.drawable.genre_hippie, 129L),
    INDIE("indie", "Indie", R.drawable.genre_indie, 128L),
    JAZZ("jazz", "Jazz", R.drawable.genre_jazz, 8L),
    K_POP("kpop", "K-Pop", R.drawable.genre_k_pop, 127L),
    LATIN("latin", "Latin", R.drawable.genre_latin, 86L),
    METAL("metal", "Metal", R.drawable.genre_metal, 9L),
    POP("pop", "Pop", R.drawable.genre_pop, 13L),
    PUNK("punk", "Punk", R.drawable.genre_punk, 43L),
    RNB("rnb", "RnB", R.drawable.genre_rnb, 14L),
    REGGAE("reggae", "Reggae", R.drawable.genre_reggae, 16L),
    ROCK("rock", "Rock", R.drawable.genre_rock, 17L),
    ROMANTIC("romantic", "Romantic", R.drawable.genre_romantic, 126L),
    TANGO("tango", "Tango", R.drawable.genre_tango, 113L),
    TRENDING("trending", "Trending", R.drawable.genre_trending, 60L),
    VOCAL("vocal", "Vocal", R.drawable.genre_vocal, 28L);

    public final String id;
    public final String name;
    public final int resourceIcon;
    public final long ID3ID;

    Genres(String id, String name, int resourceIcon, long ID3ID) {
        this.id = id;
        this.name = name;
        this.resourceIcon = resourceIcon;
        this.ID3ID = ID3ID;
    }
}
