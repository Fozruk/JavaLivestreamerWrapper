package com.github.epilepticz.JavaLivestreamerWrapper;

public interface ILivestreamerObserver {
	/**
	 * This method is asynchronously called whenever the LivestreamerWrapper got a message for an observer
	 * @param message The message from the LivestreamerWrapper
	 */
	public void recieveLivestreamerMessage(String message, SortOfMessage sort);


}
