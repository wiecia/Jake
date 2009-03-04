package com.jakeapp.jake.ics.status;

/**
 * called when a login/logout happened. Please note that you are in the main
 * thread and blocking everything. quit or spawn asap.
 * 
 * @author johannes
 */
public interface ILoginStateListener {

	public enum ConnectionState {
		LOGGED_OUT, CONNECTING, LOGGED_IN, INVALID_CREDENTIALS
	}

	/**
	 * Fired when the connection state changes.
	 * 
	 * @param le
	 * @param ex
	 */
	public void connectionStateChanged(ConnectionState le, Exception ex);
}
