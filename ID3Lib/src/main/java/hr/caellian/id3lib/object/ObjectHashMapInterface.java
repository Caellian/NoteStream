package hr.caellian.id3lib.object;

import java.util.HashMap;
import java.util.Iterator;

public interface ObjectHashMapInterface {

    HashMap getIdToString();

    HashMap getStringToId();

    Iterator iterator();
}