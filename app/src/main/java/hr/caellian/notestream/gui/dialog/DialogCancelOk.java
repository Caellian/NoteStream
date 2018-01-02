package hr.caellian.notestream.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import hr.caellian.notestream.R;

/**
 * Created by tinsv on 16/07/2017.
 */

public class DialogCancelOk extends Dialog {
    String title;
    String description;

    View.OnClickListener cancelListener;
    View.OnClickListener okListener;

    public DialogCancelOk(@NonNull Context context, String title, String description, View.OnClickListener cancel, View.OnClickListener ok) {
        super(context);
        this.title = title;
        this.description = description;
        this.cancelListener = cancel;
        this.okListener = ok;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cancel_ok);



        ((TextView) findViewById(R.id.labelTitle)).setText(title);
        TextView labelDescription = findViewById(R.id.labelDescription);
        labelDescription.setText(description);
//        labelDescription.setJustify(true); // TODO: Uncomment if it gets ported to support library.

        findViewById(R.id.buttonCancel).setOnClickListener(cancelListener);
        findViewById(R.id.buttonOk).setOnClickListener(okListener);
    }
}
