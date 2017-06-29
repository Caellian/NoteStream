package hr.caellian.notestream.data.playable;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by tinsv on 03/07/2017.
 */

public enum PlayableSource {
    LOCAL("local", PlayableLocal.class),
    YOUTUBE("remote-youtube", PlayableYouTube.class);

    private static final String TAG = PlayableSource.class.getSimpleName();

    String id;
    Class<? extends Playable> constructorClass;

    PlayableSource(String id, Class<? extends Playable> constructorClass){
        this.id = id;
        this.constructorClass = constructorClass;
    }

    public static PlayableSource getByID(String id) {
        for (PlayableSource source : values()) {
            if (Objects.equals(source.id, id.toLowerCase())) return source;
        }
        Log.e(TAG, "getByID: ", new IllegalArgumentException("Unsupported source ID: '" + id.toLowerCase() + "'!"));
        return null;
    }

    public Playable construct(Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        ArrayList<Class<?>> argTypes = new ArrayList<>(args.length);
        for (Object arg : args) {
            argTypes.add(arg.getClass());
        }

        Constructor<? extends Playable> constructor;
        try {
            constructor = constructorClass.getConstructor(argTypes.toArray(new Class<?>[0]));
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "construct: ", new IllegalArgumentException("Unsupported argument list!"));
            return null;
        }

        return constructor.newInstance(args);
    }

    public String getID() {
        return id;
    }
}
