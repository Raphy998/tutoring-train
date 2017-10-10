package at.bsd.tutoringtrain.network.listener.user;

import at.bsd.tutoringtrain.network.Result;
import at.bsd.tutoringtrain.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public interface RequestUpdateUserListener extends RequestListener {
    public void requestUpdateUserFinished(Result result);
}
