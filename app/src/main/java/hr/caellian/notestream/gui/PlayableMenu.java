package hr.caellian.notestream.gui;

import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.playable.PlayableLocal;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.playable.PlayableDownloadable;
import hr.caellian.notestream.util.Util;

/**
 * Created by caellyan on 26/06/17.
 */

public class PlayableMenu implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    PopupMenu popup;
    Playable playable;

    public PlayableMenu(View parent, Playable playable) {
        this.playable = playable;
        popup = new PopupMenu(parent.getContext(), parent);
        popup.getMenuInflater().inflate(R.menu.menu_playable, popup.getMenu());

        if (playable instanceof PlayableLocal) {
            Util.menuRemoveItem(popup.getMenu(), R.id.download);
        } else if (playable instanceof PlayableDownloadable) {
            Util.menuRemoveItem(popup.getMenu(), R.id.show_album);
            Util.menuRemoveItem(popup.getMenu(), R.id.show_artist);
            Util.menuRemoveItem(popup.getMenu(), R.id.edit_tags);
            Util.menuRemoveItem(popup.getMenu(), R.id.delete);
        }

        popup.setOnMenuItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        popup.show();
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
                // TODO: Show playable album activity
                break;
            case R.id.show_artist:
                // TODO: Show playable artist activity
                break;
            case R.id.share:
                // TODO: Share playable
                break;
            case R.id.edit_tags:
                // TODO: Open tag editor
                break;
            case R.id.delete:
                // TODO: Delete file
                break;
            default:
        }
        return true;
    }
}
