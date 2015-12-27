package models;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import bitTorrent.tracker.protocol.udp.messages.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;
import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.ConnectResponse;
import utilities.ErrorsLog;

public class ClientManager extends Observable implements Runnable {

	private static int									DATAGRAM_LENGTH	= 2048;
	private static ClientManager						instance;

	private boolean										enable;
	private DatagramSocket								socket;
	private DatagramPacket								messageIn;
	private byte[]										buffer;
	private Thread										readingThread;
	private ConcurrentHashMap<Integer, ContentManager>	contentsManagers;

	private ClientManager() {
		try {
			this.contentsManagers = new ConcurrentHashMap<Integer, ContentManager>();
			this.socket = new DatagramSocket();
			this.enable = true;
			this.readingThread = new Thread(this);
			this.readingThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean stop() {
		try {
			this.socket.close();
			this.enable = false;
			return true;
		} catch (Exception e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public synchronized void sendData(final BitTorrentUDPMessage message, final InetAddress ip, final int port) {
		try {
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, ip, port);
			socket.send(packet);
		} catch (IOException e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
		}
	}

	public synchronized void processData(final DatagramPacket messageIn) {
		try {
			ByteBuffer bufferReceive = ByteBuffer.wrap(messageIn.getData());
			Action action = Action.valueOf(bufferReceive.getInt(0));
			switch (action) {
				case ANNOUNCE:
					AnnounceResponse announceResponse = AnnounceResponse.parse(messageIn.getData());
					this.contentsManagers.get(announceResponse.getTransactionId())
							.onAnnounceResponseReceived(announceResponse);
					break;

				case CONNECT:
					ConnectResponse connectResponse = ConnectResponse.parse(messageIn.getData());
					this.contentsManagers.get(connectResponse.getTransactionId())
							.onConnectResponseReceived(connectResponse);
					break;

				case ERROR:
					System.out.println("ERROR");
					break;

				case SCRAPE:
					System.out.println("SCRAPE");
					break;

				default:
					break;
			}

		} catch (Exception ex) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), ex.toString());
			ex.printStackTrace();
		}
	}

	public void addContentManager(final ContentManager contentManager) {
		this.contentsManagers.put(contentManager.getTransactionID(), contentManager);
	}

	public boolean existTransactionID(final int transactionID) {
		return this.contentsManagers.containsKey(transactionID);
	}

	public boolean existInfoHash(final String infoHash) {
		for (Map.Entry<Integer, ContentManager> entry : this.contentsManagers.entrySet()) {
			if (entry.getValue().getInfo_hash().equals(infoHash)) {
				return true;
			}
		}
		return false;
	}

	public ConcurrentHashMap<Integer, ContentManager> getContentsManagers() {
		return contentsManagers;
	}

	public void removeContent(final String name) {
		int transactionID = Integer.MIN_VALUE;
		for (Map.Entry<Integer, ContentManager> entry : this.contentsManagers.entrySet()) {
			if (entry.getValue().getName().equals(name)) {
				entry.getValue().remove();
				transactionID = entry.getKey();
				break;
			}
		}
		this.contentsManagers.remove(transactionID);
	}

	public boolean playAll() {
		boolean play = false;
		for (Map.Entry<Integer, ContentManager> entry : this.contentsManagers.entrySet()) {
			if (entry.getValue().getStatus().equals(ContentManager.Status.STOPPED.value())) {
				entry.getValue().setStatus(ContentManager.Status.CONNECTING);
				play = true;
			}
		}
		return play;
	}

	public void pauseContent(final String name) {
		for (Map.Entry<Integer, ContentManager> entry : this.contentsManagers.entrySet()) {
			if (entry.getValue().getName().equals(name)) {
				entry.getValue().setStatus(ContentManager.Status.STOPPED);
				break;
			}
		}
	}

	public boolean stopAll() {
		boolean stop = false;
		for (Map.Entry<Integer, ContentManager> entry : this.contentsManagers.entrySet()) {
			if (entry.getValue().getStatus().equals(ContentManager.Status.CONNECTING.value())
					|| entry.getValue().getStatus().equals(ContentManager.Status.DOWNLOADING.value())
					|| entry.getValue().getStatus().equals(ContentManager.Status.WAITING_SEEDS.value())
					|| entry.getValue().getStatus().equals(ContentManager.Status.COMPLETED.value())) {
				entry.getValue().setStatus(ContentManager.Status.STOPPED);
				stop = true;
			}
		}
		return stop;
	}

	public void run() {
		try {
			while (this.enable) {
				this.buffer = new byte[DATAGRAM_LENGTH];
				this.messageIn = new DatagramPacket(buffer, buffer.length);
				this.socket.receive(messageIn);
				processData(this.messageIn);
			}
		} catch (Exception e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
		}
	}

	public static ClientManager getInstance() {
		if (instance == null) {
			instance = new ClientManager();
		}
		return instance;
	}
}