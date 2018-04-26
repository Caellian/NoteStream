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

class FragmentGoogleSignIn : Fragment() {
    lateinit var rootView: View

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.content_google_sign_in, container, false)!!

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).also {
            it.requestScopes(Scope(Constants.Scopes[0]),Scope(Constants.Scopes[1]))
            it.requestIdToken("945221734020-ftqp0ip2bmjb793o0juodfrsuap2qqdb.apps.googleusercontent.com")
            it.requestEmail()
        }.build()
        googleSignInClient = GoogleSignIn.getClient(rootView.context, options)

        rootView.findViewById<SignInButton>(R.id.sign_in_button).apply {
            setSize(SignInButton.SIZE_STANDARD)
            setOnClickListener {
                when (it.id) {
                    R.id.sign_in_button -> signIn()
                }
            }
        }

        rootView.findViewById<Button>(R.id.buttonSkip).setOnClickListener {
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
                rootView.findViewById<SignInButton>(R.id.sign_in_button)?.visibility = View.GONE
            } catch (e: ApiException) {
//                Toast.makeText(rootView.context, R.string.google_sign_in_fail, Toast.LENGTH_LONG).show()
                Toast.makeText(rootView.context, "signInResult:failed code=${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }
}