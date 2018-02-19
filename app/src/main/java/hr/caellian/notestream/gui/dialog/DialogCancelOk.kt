package hr.caellian.notestream.gui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Button
import android.widget.TextView

import hr.caellian.notestream.R

/**
 * Created by tinsv on 16/07/2017.
 */

class DialogCancelOk(context: Context, internal var title: String, internal var description: String, internal var cancelListener: View.OnClickListener, internal var okListener: View.OnClickListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_cancel_ok)

        (findViewById<TextView>(R.id.labelTitle)).text = title
        val labelDescription = findViewById<TextView>(R.id.labelDescription)
        labelDescription.text = description
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            labelDescription.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
        }

        findViewById<Button>(R.id.buttonCancel).setOnClickListener(cancelListener)
        findViewById<Button>(R.id.buttonOk).setOnClickListener(okListener)
    }
}
