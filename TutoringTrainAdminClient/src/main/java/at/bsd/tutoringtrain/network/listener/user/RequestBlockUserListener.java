package at.bsd.tutoringtrain.network.listener.user;

import at.bsd.tutoringtrain.network.Result;
import at.bsd.tutoringtrain.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public interface RequestBlockUserListener extends RequestListener {
    public void requestBlockUserFinished(Result result);
}
