package hr.caellian.notestream.gui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView

import hr.caellian.notestream.R

/**
 * Created by tinsv on 16/07/2017.
 */

class DialogCancelOk(context: Context, internal var title: String, internal var description: String, internal var cancelListener: View.OnClickListener, internal var okListener: View.OnClickListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_cancel_ok)

        (findViewById<View>(R.id.labelTitle) as TextView).text = title
        val labelDescription = findViewById<TextView>(R.id.labelDescription)
        labelDescription.text = description
        //        labelDescription.setJustify(true); // TODO: Uncomment if it gets ported to support library.

        findViewById<View>(R.id.buttonCancel).setOnClickListener(cancelListener)
        findViewById<View>(R.id.buttonOk).setOnClickListener(okListener)
    }
}
