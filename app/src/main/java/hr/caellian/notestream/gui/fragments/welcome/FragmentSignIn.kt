package hr.caellian.notestream.gui.fragments.welcome

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import hr.caellian.notestream.R

/**
 * Created by caellian on 25/02/18.
 */
class FragmentSignIn : Fragment() {
    val RC_SIGN_IN = 9001

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.content_sign_in, container, false)!!

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).also {

        }.build()
        googleSignInClient = GoogleSignIn.getClient(rootView.context, options)

        silentSignIn()

        val signInButton = rootView.findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener {
            when (it.id) {
                R.id.sign_in_button -> signIn()
            }
        }

        return rootView
    }

    fun silentSignIn() {
        val signInIntent = googleSignInClient.silentSignIn()
        signInIntent.addOnCompleteListener {
            try {
                val account = it.getResult(ApiException::class.java)
                Toast.makeText(rootView.context, account.displayName, Toast.LENGTH_LONG).show()
            } catch (e: ApiException) {
                Log.w(FragmentSignIn::class.java.simpleName, e.stackTrace.joinToString("\n") { "${it.className}:${it.lineNumber} - ${it.methodName}" })
                Toast.makeText(rootView.context, "ApiException - AUTO", Toast.LENGTH_LONG).show()
            }
            // SWITCH TO NEXT
        }
    }

    fun signIn() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Toast.makeText(rootView.context, account.displayName, Toast.LENGTH_LONG).show()
            } catch (e: ApiException) {
                Log.w(FragmentSignIn::class.java.simpleName, e.stackTrace.joinToString("\n") { "${it.className}:${it.lineNumber} - ${it.methodName}" })
                Toast.makeText(rootView.context, "ApiException - MANUAL", Toast.LENGTH_LONG).show()
            }
        }
    }
}