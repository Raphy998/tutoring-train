package at.tutoringtrain.adminclient.data;

import java.io.Serializable;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public abstract class Entity implements Serializable {

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
