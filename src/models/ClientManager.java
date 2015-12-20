package models;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Random;

import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;
import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.ConnectRequest;
import bitTorrent.tracker.protocol.udp.messages.ConnectResponse;
import utilities.ErrorsLog;

public class ClientManager extends Observable implements Runnable {

	private static int		DATAGRAM_LENGTH	= 2048;

	private InetAddress		ip;
	private int				port;

	private Thread			readingThread;
	private boolean			enable;
	private boolean			connected;

	private DatagramSocket	socket;
	private DatagramPacket	messageIn;
	private byte[]			buffer;

	private int				transactionID;

	public ClientManager(final String ip, final int port) {
		try {
			this.ip = InetAddress.getByName(ip);
			this.port = port;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			this.socket = new DatagramSocket();
			this.enable = true;

			this.readingThread = new Thread(this);
			this.readingThread.start();

			Random random = new Random();
			this.transactionID = random.nextInt(Integer.MAX_VALUE);
			ConnectRequest request = new ConnectRequest();
			request.setTransactionId(transactionID);

			while (!this.connected) {
				sendData(request);
				Thread.sleep(5000);
			}
			System.out.println("Esta conectado");
		} catch (Exception e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
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

	public void processData(final DatagramPacket messageIn) {
		try {
			ByteBuffer bufferReceive = ByteBuffer.wrap(messageIn.getData());
			Action action = Action.valueOf(bufferReceive.getInt(0));
			switch (action) {
				case ANNOUNCE:

					break;

				case CONNECT:
					ConnectResponse connectResponse = ConnectResponse.parse(messageIn.getData());
					System.out.println(connectResponse.toString());
					if (connectResponse.getTransactionId() == this.transactionID) {
						this.connected = true;
					}
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
}