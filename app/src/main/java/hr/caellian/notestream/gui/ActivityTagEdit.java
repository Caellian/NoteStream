package hr.caellian.notestream.gui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import hr.caellian.notestream.R;

/**
 * Created by caellyan on 27/06/17.
 */

public class ActivityTagEdit extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);
    }
}
