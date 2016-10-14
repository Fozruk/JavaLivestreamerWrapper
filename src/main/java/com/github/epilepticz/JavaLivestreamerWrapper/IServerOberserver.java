package com.github.epilepticz.JavaLivestreamerWrapper;

/**
 * Created by Philipp on 13.08.2015.
 */
public interface IServerOberserver {

    void onBroadcastServerip(String ip);
    void onShutdown(int exitcode);
    void onStreamEnded();

}
