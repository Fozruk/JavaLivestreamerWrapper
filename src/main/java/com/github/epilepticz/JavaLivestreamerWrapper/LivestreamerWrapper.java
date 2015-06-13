package com.github.epilepticz.JavaLivestreamerWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class LivestreamerWrapper {
	private static final Logger logger = Logger.getLogger(LivestreamerWrapper.class);

	private File playerExecutable;
	private File livestreamerExecutable;

	public LivestreamerWrapper(File livestreamerExecutable, File playerExecutable) {
		nullCheckForArguments(livestreamerExecutable, playerExecutable);
		logger.info("Creating a LivestreamerWrapper-Object");
		this.livestreamerExecutable = livestreamerExecutable;
		this.playerExecutable = playerExecutable;
		logger.info("Created a LivestreamerWrapper-Object");
	}

	public void startLivestreamerWithURL(URL url, String quality) {
		logger.info("Starting a Livestreamer-Process with URL: '" + url + "'");
		Thread streamthread = new Thread(new VlcThread(url, quality));
		streamthread.start();
		logger.info("Livestreamer-Process with URL: '" + url + "' started");
	}

	// ----- Observerstuff!

	ArrayList<ILivestreamerObserver> observers = new ArrayList<ILivestreamerObserver>();

	public void addObserver(ILivestreamerObserver listener) {
		nullCheckForArguments(listener);
		observers.add(listener);
	}

	public void removeObserver(ILivestreamerObserver listener) {
		nullCheckForArguments(listener);
		observers.remove(listener);
	}

	private void notifyObserversWithMessage(String message, SortOfMessage sort) {
		for (ILivestreamerObserver obsi : observers) {
			obsi.recieveLivestreamerMessage(message, sort);
		}
	}

	// ----- Util

	private void nullCheckForArguments(Object... args) {
		for (Object obj : args) {
			if (obj == null)
				throw new IllegalArgumentException("Parameter (" + obj + ") is null!");
		}
	}

	// ----- Player-Thread

	private class VlcThread implements Runnable {
		private URL url;
		private Process livestreamerProcess;

		public VlcThread(URL url, String quality) {
			nullCheckForArguments(url, quality);
			this.url = url;
			try {
				livestreamerProcess = Runtime.getRuntime().exec(
						livestreamerExecutable.getAbsolutePath() + " -p \"" + playerExecutable.getAbsolutePath() + "  --file-caching=5000\" " + url + " "
								+ quality);
			} catch (IOException e1) {
				throw new IllegalArgumentException("Livestreamer with Path '" + livestreamerExecutable.getAbsolutePath() + "' and playerpath '"
						+ playerExecutable.getAbsolutePath() + "' failed to start the process!");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int exitcode;
						exitcode = livestreamerProcess.waitFor();
						if (exitcode == 0)
							notifyObserversWithMessage("VLC Stream ended: Media Stream from " + url + " ended", SortOfMessage.WRAPPER_LOG);
						else
							notifyObserversWithMessage("No Stream found: Stream is offline or livestreamer is outdated!", SortOfMessage.WRAPPER_LOG);
					} catch (InterruptedException e) {
						notifyObserversWithMessage("LivestreamerProcess with URL '" + url + "' interrupted! Getting closed now!",
								SortOfMessage.LIVESTREAMERPROCESS_INTERRUPTED);
					}
				}
			}).start();
		}

		@Override
		public void run() {

			BufferedReader b = new BufferedReader(new InputStreamReader(livestreamerProcess.getInputStream()));
			String line;
			try {
				while ((line = b.readLine()) != null)
					notifyObserversWithMessage("Livestreamer output: " + line, SortOfMessage.LIVESTRAMER_LOG);
			} catch (IOException e) {
				notifyObserversWithMessage("LivestreamerProcess with URL '" + this.url + "' interrupted! Getting closed now!",
						SortOfMessage.LIVESTREAMERPROCESS_INTERRUPTED);
			}

		}
	}
}
