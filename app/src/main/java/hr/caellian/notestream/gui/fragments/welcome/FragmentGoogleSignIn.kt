/*
 * Copyright (C) 2018 Tin Svagelj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.gui.fragments.welcome

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.gui.ActivityWelcome
import hr.caellian.notestream.lib.Constants
import kotlinx.android.synthetic.main.content_google_sign_in.*

class FragmentGoogleSignIn : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.content_google_sign_in, container, false)!!

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).also {
            it.requestScopes(Scope(Constants.Scopes[0]),Scope(Constants.Scopes[1]))
            it.requestIdToken("945221734020-ftqp0ip2bmjb793o0juodfrsuap2qqdb.apps.googleusercontent.com")
            it.requestEmail()
        }.build()
        googleSignInClient = GoogleSignIn.getClient(container!!.context, options)

        sign_in_button.apply {
            setSize(SignInButton.SIZE_STANDARD)
            setOnClickListener {
                when (it.id) {
                    R.id.sign_in_button -> signIn()
                }
            }
        }

        buttonSkip.setOnClickListener {
            NoteStream.instance.preferences.edit()
                    .putBoolean(Constants.CHECK_GOOGLE_SIGN_IN, false)
                    .apply()
            (activity as ActivityWelcome).onFragmentUpdate(this@FragmentGoogleSignIn)
        }

        return rootView
    }

    fun signIn() {
        startActivityForResult(googleSignInClient.signInIntent, Constants.SIGN_IN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                NoteStream.instance.googleAccount = task.getResult(ApiException::class.java)
                NoteStream.instance.googleAccountCredential = GoogleAccountCredential.usingOAuth2(NoteStream.instance, Constants.Scopes).also {
                    it.selectedAccount = NoteStream.instance.googleAccount!!.account
                }
                sign_in_button.visibility = View.GONE
            } catch (e: ApiException) {
//                Toast.makeText(rootView.context, R.string.google_sign_in_fail, Toast.LENGTH_LONG).show()
                Toast.makeText(view?.context, "signInResult:failed code=${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }
}