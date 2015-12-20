package controllers;

import utilities.ErrorsLog;

public class Controller {

	private static Controller instance;

	private Controller() {

	}

	public boolean connect(final String ip, final int port) {
		try {
			// ClientManager.getInstance().start(ip, port);
			return true;
		} catch (Exception e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public void disconnect() {
		// TODO
	}

	public boolean isConnected() {
		// TODO
		return true;
	}

	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}
}