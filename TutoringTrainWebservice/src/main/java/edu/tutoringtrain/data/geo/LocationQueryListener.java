package edu.tutoringtrain.data.geo;

import edu.tutoringtrain.data.geo.data.LocationObject;


/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface LocationQueryListener {
    void queryFinished(LocationObject loc);
    void queryFailed(Exception error);
}
