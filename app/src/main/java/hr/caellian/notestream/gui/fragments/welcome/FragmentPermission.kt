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

package hr.caellian.notestream.gui.fragments.welcome

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import hr.caellian.notestream.R
import hr.caellian.notestream.lib.Constants
import android.content.pm.PackageManager
import android.util.Log
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.gui.ActivityWelcome


open class FragmentPermission : Fragment() {
    lateinit var rootView: View

    var waiting = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.content_permission, container, false)!!

        arguments?.getInt(Constants.ARGUMENT_ICON)?.also { icon ->
            rootView.findViewById<ImageView>(R.id.permissionView)?.setImageResource(icon)
        }

        arguments?.getInt(Constants.ARGUMENT_TITLE)?.also { title ->
            rootView.findViewById<TextView>(R.id.labelTitle)?.setText(title)
        }

        arguments?.getInt(Constants.ARGUMENT_DESCRIPTION)?.also { description ->
            rootView.findViewById<TextView>(R.id.labelDescription)?.setText(description)
        }

        arguments?.getString(Constants.ARGUMENT_PERMISSION)?.also { permission ->
            rootView.findViewById<Button>(R.id.buttonDeny).setOnClickListener {
                (activity as ActivityWelcome).onFragmentUpdate(this)

                Log.w("TAG", Constants.CHECK_PERMISSION + permission.removePrefix(Constants.PERMISSION_PREFIX))
                NoteStream.instance.preferences.edit()
                        .putBoolean(Constants.CHECK_PERMISSION + permission.removePrefix(Constants.PERMISSION_PREFIX), false)
                        .apply()
                it.visibility = View.GONE
                rootView.findViewById<Button>(R.id.buttonAllow)?.visibility = View.VISIBLE
            }

            rootView.findViewById<Button>(R.id.buttonAllow).setOnClickListener {
                waiting = true
                requestPermissions(arrayOf(permission), Constants.APP_REQUEST_CODE)

                NoteStream.instance.preferences.edit()
                        .putBoolean("${Constants.CHECK_PERMISSION}${permission.removePrefix(Constants.PERMISSION_PREFIX)}", true)
                        .apply()
                it.visibility = View.GONE
                rootView.findViewById<Button>(R.id.buttonDeny)?.visibility = View.VISIBLE
            }
        }

        return rootView
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted = permissions.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED

        if (!granted) {
            arguments?.getString(Constants.ARGUMENT_PERMISSION)?.also {
                NoteStream.instance.preferences.edit()
                        .putBoolean(Constants.CHECK_PERMISSION + permissions[0].removePrefix(Constants.PERMISSION_PREFIX), false)
                        .apply()
                rootView.findViewById<Button>(R.id.buttonDeny)?.visibility = View.GONE
                rootView.findViewById<Button>(R.id.buttonAllow)?.visibility = View.VISIBLE
            }
        }

        if (waiting) (activity as ActivityWelcome).onFragmentUpdate(this@FragmentPermission)
    }

    companion object {
        val BundleAccessExternal = Bundle().also {
            it.putInt(Constants.ARGUMENT_ICON, R.drawable.ic_storage)
            it.putInt(Constants.ARGUMENT_TITLE, R.string.permission_access_external)
            it.putInt(Constants.ARGUMENT_DESCRIPTION, R.string.permission_access_external_explanation)
            it.putString(Constants.ARGUMENT_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val BundlePhoneState = Bundle().also {
            it.putInt(Constants.ARGUMENT_ICON, R.drawable.ic_phone)
            it.putInt(Constants.ARGUMENT_TITLE, R.string.permission_access_phone_state)
            it.putInt(Constants.ARGUMENT_DESCRIPTION, R.string.permission_access_phone_state_explanation)
            it.putString(Constants.ARGUMENT_PERMISSION, Manifest.permission.READ_PHONE_STATE)
        }

        val Permissions = listOf(BundleAccessExternal, BundlePhoneState)
    }
}