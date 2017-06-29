package hr.caellian.notestream.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;

import hr.caellian.notestream.NoteStream;

/**
 * Created by caellyan on 17/06/17.
 */

public class Util {
    public static String timeToString(int ms) {
        return timeToString(ms, false);
    }

    public static String timeToString(int ms, boolean forceHourFormat) {
        String seconds = String.format("%02d", (ms % 60000) / 1000);
        String minutes = String.format("%02d", (ms % 3600000) / 60000);
        String hours = String.format("%02d", ms / 3600000);
        if (forceHourFormat) {
            return hours + ":" + minutes + ":" + seconds;
        } else {
            return ms >= 3600000 ?
                    hours + ":" + minutes + ":" + seconds :
                    minutes + ":" + seconds;
        }
    }

    public static Bitmap drawableToBitmap(int drawableID) {
        return drawableToBitmap(drawableID, -1, -1);
    }

    public static Bitmap drawableToBitmap(int drawableID, int width, int height) {
        Drawable drawable = NoteStream.getInstance().getResources().getDrawable(drawableID, null);
        Bitmap bitmap;
        width = Math.max(width, drawable.getIntrinsicWidth());
        height = Math.max(height, drawable.getIntrinsicHeight());

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Integer getMenuItemFromID(Menu menu, int id) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == id) return i;
        }
        return null;
    }


    public static void menuRemoveItem(Menu menu, int id) {
        Integer res = getMenuItemFromID(menu, id);
        if (res != null) menu.removeItem(res);
    }
}
