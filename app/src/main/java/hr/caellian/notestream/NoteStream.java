package hr.caellian.notestream;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import hr.caellian.notestream.data.playable.PlayableYouTube;
import hr.caellian.notestream.gui.ActivityPermissions;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.data.playable.PlayableLocal;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.playable.Playable;
import io.paperdb.Paper;

/**
 * Created by caellyan on 16/06/17.
 */

public class NoteStream extends Application {
    // TODO: Use Glide library!
    // https://github.com/bumptech/glide

    private static NoteStream instance;

    private PlayerService.PlayerServiceBinder psb = null;
    public static final ArrayList<Playable.ProgressListener> PROGRESS_LISTENERS = new ArrayList<>();
    public static final ArrayList<Playable.ControlListener> CONTROL_LISTENERS = new ArrayList<>();

    public Library library;

    public static synchronized NoteStream getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        Paper.init(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent permissionsIntent = new Intent(this, ActivityPermissions.class);
            startActivity(permissionsIntent);
        }

        //Initialise player service for later use.
        Intent psi = new Intent(this, PlayerService.class);
        psi.setAction(Constants.ACTION_INIT);

        bindService(psi, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                psb = (PlayerService.PlayerServiceBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                psb = null;
            }
        }, BIND_ABOVE_CLIENT);
        startService(psi);

        super.onCreate();
    }

    public static void registerProgressListener(Playable.ProgressListener listener) {
        PROGRESS_LISTENERS.add(listener);
    }

    public static void registerControlListener(Playable.ControlListener listener) {
        CONTROL_LISTENERS.add(listener);
    }

    public static Library populateLibrary(final Activity activity) {
        final Library result = new Library();

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // We need to ask for permissions here.
//
//            int readExternal = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
//            if (readExternal != PackageManager.PERMISSION_GRANTED) {
//                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    new AlertDialog.Builder(activity)
//                            .setMessage("We need to access your local music.")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                        activity.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.APP_REQUEST_CODE);
//                                        addLocalToLibrary(activity, result);
//                                    }
//                                }
//                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(activity, "App functionality might be limited.", Toast.LENGTH_LONG).show();
//                                }
//                            })
//                            .create()
//                            .show();
//                }
//                activity.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.APP_REQUEST_CODE);
//            } else {
//                addLocalToLibrary(activity, result);
//            }
//        } else {
            addLocalToLibrary(activity, result);
//        }

        return result;
    }

    protected static void addLocalToLibrary(Context context, Library library) {
        Uri extUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cur = context.getContentResolver().query(extUri, null, selection, null, sortOrder);

        if(cur != null) {
            if(cur.getCount() > 0) {
                OuterLoop:
                while(cur.moveToNext()) {
                    final String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    for (Playable playable : library.localMusic.getPlaylist()) {
                        if (Objects.equals(playable.getPlayableId(), PlayableLocal.getId(path))) continue OuterLoop;
                    }

                    PlayableLocal playable = new PlayableLocal(path);
                    playable.getMetadata().setFromSource(path);
                    library.localMusic.add(playable);
                    library.savedMusic.add(playable);
                }
            }
            cur.close();
        }

        library.savedMusic.add(new PlayableYouTube("dMK_npDG12Q"));
    }

    public PlayerService.PlayerServiceBinder getPlayerServiceBinder() {
        return psb;
    }
}
