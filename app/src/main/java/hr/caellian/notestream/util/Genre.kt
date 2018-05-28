/*
 * The MIT License (MIT)
 * NoteStream, android music player and streamer
 * Copyright (c) 2018 Tin Švagelj <tin.svagelj.email@gmail.com> a.k.a. Caellian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.util

import hr.caellian.notestream.R

data class Genre(val id: String, val name: String, val resourceIcon: Int, val ID3ID: Long) {
    companion object {
        val genres: MutableList<Genre> = mutableListOf()

        private val defaults: List<Genre> = listOf(
                Genre("centuries", "Centuries", R.drawable.genre_80_s, 11L),
                Genre("acoustic", "Acoustic", R.drawable.genre_acoustic, 99L),
                Genre("blues", "Blues", R.drawable.genre_blues, 0L),
                Genre("childrens", "Children's Music", R.drawable.genre_childrens_music, 132L),
                Genre("chill", "Chill", R.drawable.genre_chill, 98L),
                Genre("festive", "Festive", R.drawable.genre_festive, 131L),
                Genre("classical", "Classical", R.drawable.genre_classical, 32L),
                Genre("country", "Country", R.drawable.genre_country, 2L),
                Genre("disco", "Disco", R.drawable.genre_disco, 4L),
                Genre("edm", "EDM", R.drawable.genre_edm, 18L),
                Genre("ethnic", "Ethnic", R.drawable.genre_ethnic, 48L),
                Genre("excercise", "Excercise", R.drawable.genre_excercise, 130L),
                Genre("gospel", "Gospel", R.drawable.genre_gospel, 38L),
                Genre("hiphop", "Hip-Hop", R.drawable.genre_hip_hop, 7L),
                Genre("hippie", "Hippie", R.drawable.genre_hippie, 129L),
                Genre("indie", "Indie", R.drawable.genre_indie, 128L),
                Genre("jazz", "Jazz", R.drawable.genre_jazz, 8L),
                Genre("kpop", "K-Pop", R.drawable.genre_k_pop, 127L),
                Genre("latin", "Latin", R.drawable.genre_latin, 86L),
                Genre("metal", "Metal", R.drawable.genre_metal, 9L),
                Genre("pop", "Pop", R.drawable.genre_pop, 13L),
                Genre("punk", "Punk", R.drawable.genre_punk, 43L),
                Genre("rnb", "RnB", R.drawable.genre_rnb, 14L),
                Genre("reggae", "Reggae", R.drawable.genre_reggae, 16L),
                Genre("rock", "Rock", R.drawable.genre_rock, 17L),
                Genre("romantic", "Romantic", R.drawable.genre_romantic, 126L),
                Genre("tango", "Tango", R.drawable.genre_tango, 113L),
                Genre("trending", "Trending", R.drawable.genre_trending, 60L),
                Genre("vocal", "Vocal", R.drawable.genre_vocal, 28L)
        )

        fun resetGenres() {
            genres.clear()
            genres.addAll(defaults)
        }
    }
}
