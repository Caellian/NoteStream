package hr.caellian.notestream.gui

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.gui.fragments.welcome.FragmentPermission
import hr.caellian.notestream.gui.fragments.welcome.FragmentGoogleSignIn
import hr.caellian.notestream.gui.fragments.welcome.FragmentLoadingScreen
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.LockableViewPager
import hr.caellian.notestream.util.SlidePageTransformer
import me.relex.circleindicator.CircleIndicator

class ActivityWelcome : AppCompatActivity(), ViewPager.OnPageChangeListener {

    lateinit var pager: LockableViewPager
    lateinit var pagerAdapter: PagerAdapter
    lateinit var fragmentLoadingScreen: FragmentLoadingScreen

    val pages = mutableListOf<Fragment>()
    val pendingRequests = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // TODO: Handle intents from other apps properly

        val account = GoogleSignIn.getLastSignedInAccount(NoteStream.instance)
        if (account != null) {
            NoteStream.instance.googleAccount = account
            NoteStream.instance.googleAccountCredential = GoogleAccountCredential.usingOAuth2(NoteStream.instance, Constants.Scopes).also {
                it.selectedAccount = NoteStream.instance.googleAccount!!.account
            }
        } else if (NoteStream.instance.preferences.getBoolean(Constants.CHECK_GOOGLE_SIGN_IN, true)) {
            pages += FragmentGoogleSignIn()
        }

        FragmentPermission.Permissions.forEach { bundle ->
            val permission = bundle.getString(Constants.ARGUMENT_PERMISSION)
            val check = checkSelfPermission(permission)
            if (check != PackageManager.PERMISSION_GRANTED && NoteStream.instance.preferences.getBoolean(Constants.CHECK_PERMISSION + permission.removePrefix(Constants.PERMISSION_PREFIX),true)) {
                pages += FragmentPermission().apply {
                    arguments = bundle
                }
            }
        }

        pendingRequests += pages

        fragmentLoadingScreen = FragmentLoadingScreen()
        pages += fragmentLoadingScreen

        pagerAdapter = PageSlideAdapter(supportFragmentManager, pages)
        this.pager = findViewById<LockableViewPager>(R.id.welcome_content).apply {
            locked = true
            adapter = pagerAdapter
            setPageTransformer(true, SlidePageTransformer())
            addOnPageChangeListener(this@ActivityWelcome)
        }

        if (pages.size > 1) {
            findViewById<CircleIndicator>(R.id.contentIndicator)?.also {
                it.configureIndicator(20, 20, 40)
                it.setViewPager(pager)
            }
        } else {
            findViewById<CircleIndicator>(R.id.contentIndicator)?.visibility = View.GONE
            fragmentLoadingScreen.ready = true
        }
    }

    fun onFragmentUpdate(fragment: Fragment) {
        pendingRequests -= fragment
        if (pendingRequests.isEmpty()) fragmentLoadingScreen.ready = true

        pager.currentItem++
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        if (position == pages.lastIndex) {
            fragmentLoadingScreen.whenScrolledTo()
            findViewById<CircleIndicator>(R.id.contentIndicator)?.visibility = View.GONE
        } else {
            findViewById<CircleIndicator>(R.id.contentIndicator)?.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0 || pager.currentItem == pages.lastIndex) {
            super.onBackPressed()
        } else {
            pager.currentItem--
        }
    }

    class PageSlideAdapter(fm: FragmentManager, val pages: List<Fragment>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return pages[position]
        }

        override fun getCount(): Int {
            return pages.size
        }
    }
}