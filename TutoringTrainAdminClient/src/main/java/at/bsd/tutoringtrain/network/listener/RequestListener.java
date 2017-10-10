package at.bsd.tutoringtrain.network.listener;

import at.bsd.tutoringtrain.network.Result;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public abstract interface RequestListener {
    public void requestFailed(Result result);
}
