package hr.caellian.notestream;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Objects;

import hr.caellian.notestream.data.playable.PlayableRemote;
import hr.caellian.notestream.data.playable.PlayableYouTube;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.data.playable.PlayableLocal;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.playable.Playable;

/**
 * Created by caellyan on 16/06/17.
 */

public class NoteStream extends Application {

    private static NoteStream instance;

    private PlayerService.PlayerServiceBinder psb = null;
    public static final ArrayList<PlayerServiceListener> PSB_LISTENERS = new ArrayList<>();
    public static final ArrayList<Playable.ProgressListener> PROGRESS_LISTENERS = new ArrayList<>();
    public static final ArrayList<Playable.ControlListener> CONTROL_LISTENERS = new ArrayList<>();
    public static final ArrayList<Library.LibraryListener> LIBRARY_LISTENERS = new ArrayList<>();
    public static final ArrayList<PlayableRemote.AvailabilityListener> AVAILABILITY_LISTENERS = new ArrayList<>();

    public Library library;

    public static synchronized NoteStream getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;

        //Initialise player service for later use.
        Intent psi = new Intent(this, PlayerService.class);
        psi.setAction(Constants.ACTION_INIT);

        bindService(psi, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                psb = (PlayerService.PlayerServiceBinder) service;
                for (PlayerServiceListener psbListener : PSB_LISTENERS) {
                    psbListener.onPlayerServiceConnected(psb);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                psb = null;
            }
        }, BIND_ABOVE_CLIENT);
        startService(psi);

        super.onCreate();
    }

    public static void registerPlayerServiceListener(PlayerServiceListener listener) {
        PSB_LISTENERS.add(listener);
    }

    public static void registerProgressListener(Playable.ProgressListener listener) {
        PROGRESS_LISTENERS.add(listener);
    }

    public static void registerControlListener(Playable.ControlListener listener) {
        CONTROL_LISTENERS.add(listener);
    }

    public static void registerLibraryListener(Library.LibraryListener listener) {
        LIBRARY_LISTENERS.add(listener);
    }

    public static void registerAvailabilityListener(PlayableRemote.AvailabilityListener listener) {
        AVAILABILITY_LISTENERS.add(listener);
    }

    public void populateLibrary(final Activity activity) {
        int storageCheck = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (storageCheck == PackageManager.PERMISSION_GRANTED) {
            addLocalContentToLibrary(activity, library = new Library());
        }
    }

    private static void addLocalContentToLibrary(Context context, Library library) {
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
                        if (Objects.equals(playable.getID(), PlayableLocal.getId(path))) continue OuterLoop;
                    }

                    PlayableLocal playable = new PlayableLocal(path);
                    playable.getMetadata().setFromSource(path);
                    library.localMusic.add(playable);
                    library.savedMusic.add(playable);
                }
            }
            cur.close();
        }

        // TODO: Remove when done with testing.
        library.savedMusic.add(new PlayableYouTube("dMK_npDG12Q"));
        library.savedMusic.add(new PlayableYouTube("RM7lw0Ovzq0"));
        library.savedMusic.add(new PlayableYouTube("ozv4q2ov3Mk"));
    }

    public PlayerService.PlayerServiceBinder getPlayerServiceBinder() {
        return psb;
    }

    public abstract static class PlayerServiceListener {
        public abstract void onPlayerServiceConnected(PlayerService.PlayerServiceBinder psb);
        public void onPlayerServiceDisconnected() {}
    }
}
