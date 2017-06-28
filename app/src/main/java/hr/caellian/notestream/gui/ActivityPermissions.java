package hr.caellian.notestream.gui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;

import hr.caellian.notestream.R;
import hr.caellian.notestream.gui.fragments.FragmentPermission;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.RequiredPermission;

/**
 * Created by caellyan on 27/06/17.
 */

public class ActivityPermissions extends AppCompatActivity {
    private static final String TAG = ActivityPermissions.class.getSimpleName();

    private boolean libraryStarted = true;
    ArrayList<String> permissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        libraryStarted = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions = new ArrayList<>();
            for (RequiredPermission permission : RequiredPermission.values()) {
                if (checkSelfPermission(permission.getId()) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(permission.getId());
                }
            }

//            FragmentManager fm = getFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            for (RequiredPermission permission : RequiredPermission.values()) {
//                FragmentPermission fragment = FragmentPermission.create(permission);
//                ft.add(R.id.permission_list, fragment, "permission-" + permission.getId());
//            }

            if (permissions.size() > 0) {
//                ft.commit();
                requestPermissions(permissions.toArray(new String[permissions.size()]), Constants.APP_REQUEST_CODE);
            } else {
                if (libraryStarted) {
                    finish();
                } else {
                    startLibrary();
                }
            }
        } else {
            startLibrary();
        }
    }

    private void startLibrary() {
        libraryStarted = true;
        Intent intent = new Intent(ActivityPermissions.this, ActivityLibrary.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (libraryStarted) {
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (libraryStarted) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        boolean done = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (RequiredPermission permission : RequiredPermission.values()) {
                if (checkSelfPermission(permission.getId()) != PackageManager.PERMISSION_GRANTED) {
                    done = false;
                }
            }
        }
        if (done) {
            if (libraryStarted) {
                super.onBackPressed();
                finish();
            } else {
                startLibrary();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean shouldClose = true;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                shouldClose = false;
                for (RequiredPermission permission : RequiredPermission.values()) {
                    if (Objects.equals(permissions[i], permission.getId())) {
                        FragmentPermission fragment = FragmentPermission.create(permission);
                        ft.add(R.id.permission_list, fragment, "permission-" + permission.getId());
                    }
                }
            }
        }

        if (shouldClose) {
            if (libraryStarted) {
                finish();
            } else {
                startLibrary();
            }
        } else {
            ft.commit();
        }
    }
}