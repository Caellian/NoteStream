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

import android.support.v4.view.ViewPager
import android.view.View

class SlidePageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position <= -1 -> {
                page.alpha = 0f
            }
            position >= 1 -> {
                page.alpha = 0f
            }
            position < 0 -> {
                page.alpha = 1 + position
                page.translationX = position
            }
            position > 0 -> {
                page.alpha = 1 - position
                page.translationX = position
            }
        }

        page.invalidate()
    }

}