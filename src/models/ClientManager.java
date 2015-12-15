package models;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Observable;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;
import bitTorrent.tracker.protocol.udp.messages.ConnectRequest;
import utilities.ErrorsLog;

public class ClientManager extends Observable implements Runnable {

	private static ClientManager	instance;

	private static int				OK						= 0;
	private static int				NEW_CONNECTION			= 1;
	private static int				ANNOUNCE				= 2;
	private static int				ERR						= 99;

	private static int				DATAGRAM_CONTENT_LENGTH	= 2032;
	private static int				DATAGRAM_HEADER_LENGTH	= 16;

	private InetAddress				ip;
	private int						port;

	private Thread					readingThread;
	private boolean					enable;
	private boolean					connected;

	private DatagramSocket			socket;
	private DatagramPacket			messageIn;
	private byte[]					buffer;

	private ClientManager() {

	}

	public boolean start(final String ip, final int port) {
		try {
			this.ip = InetAddress.getByName(ip);
			this.port = port;

			this.socket = new DatagramSocket();
			this.enable = true;

			this.readingThread = new Thread(this);
			this.readingThread.start();

			while (!this.connected) {
				sendData(new ConnectRequest());
				Thread.sleep(2000);
			}
			System.out.println("Esta conectado");
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

	public synchronized void sendData(final BitTorrentUDPMessage message) {
		try {
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, this.ip,
					this.port);
			socket.send(packet);
		} catch (IOException e) {
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

	public void processData(final DatagramPacket messageIn) {
		try {
			ByteBuffer bufferReceive = ByteBuffer.wrap(messageIn.getData());
			if (!connected) {
				this.connected = true;
			} else {
				// TODO Procesar announce
			}
		} catch (Exception ex) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), ex.toString());
			ex.printStackTrace();
		}
	}

	public void run() {
		try {
			while (this.enable) {
				this.buffer = new byte[DATAGRAM_CONTENT_LENGTH + DATAGRAM_HEADER_LENGTH];
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
}