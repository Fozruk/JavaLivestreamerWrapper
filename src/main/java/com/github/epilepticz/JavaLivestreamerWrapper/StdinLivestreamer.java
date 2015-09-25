package com.github.epilepticz.JavaLivestreamerWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * Created by Philipp on 13.08.2015.
 */
public class StdinLivestreamer extends LivestreamerWrapper {

    private static final Logger logger = LoggerFactory.getLogger(StdinLivestreamer
            .class);

    private String args;

    public StdinLivestreamer(File livestreamerExecutable, File
            playerExecutable) {
        super(livestreamerExecutable, playerExecutable);
    }

    public void startLivestreamerWithURL(URL url, String quality) {
        logger.info("Starting a Livestreamer-Process with URL: '" + url + "'");
        args =  " -p \"" + playerExecutable.getAbsolutePath() + "  --file-caching=5000\" " + url + " "
                + quality;
        Thread streamthread = new Thread(new VlcThread(url, quality,args));
        streamthread.start();
        logger.info("Livestreamer-Process with URL: '" + url + "' started");
    }



}
