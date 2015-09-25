package com.github.epilepticz.JavaLivestreamerWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LivestreamerWrapper {
	private static final Logger logger = LoggerFactory.getLogger
			(LivestreamerWrapper.class);

	protected File playerExecutable;
	protected File livestreamerExecutable;
	protected IServerOberserver observer;

	public LivestreamerWrapper(File livestreamerExecutable, File playerExecutable) {
		nullCheckForArguments(livestreamerExecutable, playerExecutable);
		logger.info("Creating a LivestreamerWrapper-Object");
		this.livestreamerExecutable = livestreamerExecutable;
		this.playerExecutable = playerExecutable;
		logger.info("Created a LivestreamerWrapper-Object");
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
			new Thread(new Runnable() {
				@Override
				public void run() {
					obsi.recieveLivestreamerMessage(message, sort);
				}
			}).start();
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

	protected class VlcThread implements Runnable {
		private URL url;
		private Process livestreamerProcess;

		public VlcThread(URL url, String quality,String args) {
			nullCheckForArguments(url, quality,args);
			this.url = url;
			try {
				livestreamerProcess = Runtime.getRuntime().exec(
						livestreamerExecutable.getAbsolutePath() + args);
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
						if(observer != null)
							observer.onShutdown(exitcode);
					} catch (InterruptedException e) {
						notifyObserversWithMessage("LivestreamerProcess with URL '" + url + "' interrupted! Getting closed now!",
								SortOfMessage.LIVESTREAMERPROCESS_INTERRUPTED);
					}
				}
			}).start();

		}

		public void killProcess()
		{
			livestreamerProcess.destroy();
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
