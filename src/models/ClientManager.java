package models;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Observable;

import utilities.ErrorsLog;

public class ClientManager extends Observable implements Runnable {

	private static ClientManager	instance;

	private static int				DATAGRAM_CONTENT_LENGTH	= 2032;
	private static int				DATAGRAM_HEADER_LENGTH	= 16;

	private String					ip;
	private int						port;

	private Thread					readingThread;
	private boolean					enable;

	private MulticastSocket			socket;
	private InetAddress				group;
	private DatagramPacket			messageIn;
	private byte[]					buffer;

	private ClientManager() {

	}

	public boolean start(final String ip, final int port) {
		try {
			this.ip = ip;
			this.port = port;

			this.socket = new MulticastSocket(port);
			this.group = InetAddress.getByName(this.ip);
			this.enable = true;

			this.readingThread = new Thread(this);
			this.readingThread.start();

			return true;
		} catch (Exception e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public boolean stop() {
		try {
			this.socket.leaveGroup(group);
			this.enable = false;
			return true;
		} catch (IOException e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public synchronized void updateContents() {
		try {
			// TODO
			setChanged();
			notifyObservers();
		} catch (Exception ex) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), ex.toString());
			ex.printStackTrace();
		}
	}

	public static ClientManager getInstance() {
		if (instance == null) {
			instance = new ClientManager();
		}
		return instance;
	}

	public void run() {

	}
}