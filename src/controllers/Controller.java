package controllers;

import models.ClientManager;
import utilities.ErrorsLog;

public class Controller {
	public Controller() {

	}

	public boolean connect(final String ip, final int port) {
		try {
			ClientManager.getInstance().start(ip, port);
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
}