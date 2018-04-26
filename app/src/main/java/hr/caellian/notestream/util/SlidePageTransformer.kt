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
    }

}