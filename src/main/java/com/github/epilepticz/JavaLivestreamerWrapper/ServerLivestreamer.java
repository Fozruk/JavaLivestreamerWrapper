package com.github.epilepticz.JavaLivestreamerWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp on 13.08.2015.
 */
public class ServerLivestreamer extends  LivestreamerWrapper implements ILivestreamerObserver {

    private static final Logger logger = LoggerFactory.getLogger(StdinLivestreamer
            .class);
    private String args;
    VlcThread thread;

    public ServerLivestreamer(File livestreamerExecutable, File
            playerExecutable,IServerOberserver observer) {
        super(livestreamerExecutable, playerExecutable);
        this.observer = observer;
    }

    public void startLiveStreamerAsServer(URL url,String quality,
                                   IServerOberserver observer)
    {
        this.observer = observer;

        logger.info("Starting a Livestreamer-Process with URL: '" + url + "'");
        args =  " " + url + " "
                + quality + " --player-external-http";

        thread = new VlcThread(url, quality,args);

        Thread streamthread = new Thread(thread);

        streamthread.start();
        logger.info("Livestreamer-Process with URL: '" + url + "' started");
        addObserver(this);
    }


    @Override
    public void recieveLivestreamerMessage(String message, SortOfMessage sort) {
        if(message.contains("http://127.0.0.1"))
        {
            int start = message.indexOf("http://127.0.0.1");
            String ip = message.substring(start);
            logger.debug("Found IP: " + ip);
            observer.onBroadcastServerip(ip);
        }

        if(message.contains("Stream ended"))
        {
            observer.onStreamEnded();
        }
    }

    public void killProcess()
    {
        thread.killProcess();
    }
}
