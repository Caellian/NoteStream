package hr.caellian.notestream.gui.fragments.welcome

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import hr.caellian.notestream.R

@SuppressLint("ValidFragment")
/**
 * Created by caellian on 28/02/18.
 */
class FragmentPermission(val permission: Permission) : Fragment() {

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.content_permission, container, false)!!

        rootView.findViewById<ImageView>(R.id.permissionView).setImageResource(permission.iconResource)
        rootView.findViewById<TextView>(R.id.labelTitle).setText(permission.titleResource)
        rootView.findViewById<TextView>(R.id.labelDescription).setText(permission.descriptionResource)

        return rootView
    }

    enum class Permission(val iconResource: Int, val titleResource: Int, val descriptionResource: Int) {
        ACCESS_EXTERNAL(R.drawable.storage, R.string.permission_access_external, R.string.permission_access_external_explanation),
        PHONE_STATE(R.drawable.storage, R.string.permission_access_phone_state, R.string.permission_access_phone_state_explanation),
        ;
    }
}