package hr.caellian.notestream.gui.fragments;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.Objects;

import hr.caellian.notestream.R;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.RequiredPermission;

/**
 * Created by caellyan on 27/06/17.
 */

public class FragmentPermission extends Fragment {
    private static final String PERMISSION = "permission";

    public static FragmentPermission create(RequiredPermission permission) {
        FragmentPermission fragment = new FragmentPermission();

        Bundle args = new Bundle();
        args.putSerializable(PERMISSION, permission);
        fragment.setArguments(args);
        return fragment;
    }

    View view;
    RequiredPermission permission;
    Switch permissionSwitch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_permission, container, false);

        permission = (RequiredPermission) getArguments().getSerializable(PERMISSION);
        permissionSwitch = (Switch) view.findViewById(R.id.permissionSwitch);
        if (permission != null) {
            permissionSwitch.setText(permission.getTitle());
            permissionSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        FragmentPermission.this.requestPermissions(new String[] {permission.getId()}, Constants.APP_REQUEST_CODE);
                    }
                }
            });
        } else {
            view.setVisibility(View.GONE);
            permissionSwitch.setText("NULL");
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (Objects.equals(permissions[i], permission.getId())) {
                view.setVisibility(grantResults[i] == PackageManager.PERMISSION_GRANTED ? View.GONE : View.INVISIBLE);
                permissionSwitch.setChecked(grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }
}
