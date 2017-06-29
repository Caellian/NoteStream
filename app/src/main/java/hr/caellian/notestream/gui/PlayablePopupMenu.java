package hr.caellian.notestream.gui;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.playable.PlayableLocal;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.playable.PlayableDownloadable;
import hr.caellian.notestream.data.playable.PlayableYouTube;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.Util;

/**
 * Created by caellyan on 26/06/17.
 */

public class PlayablePopupMenu extends PopupMenu implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    Context parentContext;
    Playable playable;

    public PlayablePopupMenu(Context context, View anchor, Playable playable) {
        super(context, anchor);
        this.parentContext = context;
        this.playable = playable;

        getMenuInflater().inflate(R.menu.menu_playable, getMenu());

        if (playable instanceof PlayableLocal) {
            getMenuInflater().inflate(R.menu.menu_playable_local, getMenu());
        }
        if (playable instanceof PlayableDownloadable) {
            getMenuInflater().inflate(R.menu.menu_playable_downloadable, getMenu());
        }
        if (playable instanceof PlayableYouTube) {
            getMenuInflater().inflate(R.menu.menu_playable_youtube, getMenu());
        }

        setOnMenuItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        PlayerService.PlayerServiceBinder psb = NoteStream.getInstance().getPlayerServiceBinder();

        switch (item.getItemId()) {
            case R.id.add_library:
                NoteStream.getInstance().library.savePlayable(playable);
                break;
            case R.id.add_playlist:
                // TODO: Open playlist list
                break;
            case R.id.add_favorites:
                NoteStream.getInstance().library.favoriteMusic.add(playable);
                break;
            case R.id.download:
                ((PlayableDownloadable) playable).download();
                break;
            case R.id.play_next:
                if (psb != null) psb.playNext(playable);
                break;
            case R.id.add_queue:
                if (psb != null) psb.addToQueue(playable);
                break;
            case R.id.show_album:
                Intent albumIntent = new Intent(parentContext, ActivityPlaylist.class);
                String albumID = NoteStream.getInstance().library.albums.get(playable.getMetadata().getAlbum()).getID();
                albumIntent.putExtra(Constants.EXTRA_PLAYLIST, albumID);
                parentContext.startActivity(albumIntent);
                break;
            case R.id.show_artist:
                Intent artistIntent = new Intent(parentContext, ActivityPlaylist.class);
                String artistID = NoteStream.getInstance().library.artists.get(playable.getMetadata().getAuthor()).getID();
                artistIntent.putExtra(Constants.EXTRA_PLAYLIST, artistID);
                parentContext.startActivity(artistIntent);
                break;
            case R.id.share:
                if (playable instanceof PlayableLocal) {
                    PlayableLocal localPlayable = (PlayableLocal) playable;

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, localPlayable.getPath());
                    sendIntent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(localPlayable.getPath().substring(localPlayable.getPath().lastIndexOf(".") + 1))));
                    parentContext.startActivity(Intent.createChooser(sendIntent, "Share Music"));
                } else if (playable instanceof PlayableYouTube) {
                    PlayableYouTube localPlayable = (PlayableYouTube) playable;

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "http://youtube.com/watch?v=" + localPlayable.getPath());
                    sendIntent.setType("text/plain");
                    parentContext.startActivity(Intent.createChooser(sendIntent, "Share Video URL"));
                }
                // TODO: Share playable
                break;
            case R.id.edit_tags:
                Intent editorIntent = new Intent(parentContext, ActivityTagEdit.class);
                editorIntent.putExtra(Constants.EXTRA_PLAYABLE, playable.getID());
                parentContext.startActivity(editorIntent);
                break;
            case R.id.delete:
                // TODO: Delete file
                break;
            case R.id.suggestions:
                // TODO: Show YT suggestions
                break;
            default:
        }
        return true;
    }
}
