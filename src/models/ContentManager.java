package models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import javax.swing.Timer;

import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.tracker.protocol.udp.messages.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.AnnounceRequest.Event;
import bitTorrent.tracker.protocol.udp.messages.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage;
import bitTorrent.tracker.protocol.udp.messages.BitTorrentUDPMessage.Action;
import bitTorrent.tracker.protocol.udp.messages.ConnectRequest;
import bitTorrent.tracker.protocol.udp.messages.ConnectResponse;
import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
import bitTorrent.util.ByteUtils;
import utilities.ErrorsLog;

public class ContentManager extends Observable implements Runnable {

	private static int		DATAGRAM_LENGTH		= 2048;
	private static int		ERROR_TIME_MARGIN	= 500;

	private InetAddress		ip;
	private int				port;
	private String			name;
	private String			info_hash;
	private long			size;
	private int				leechers;
	private int				seeders;
	private List<PeerInfo>	peers;

	private Thread			readingThread;
	private boolean			enable;
	private boolean			connected;

	private DatagramSocket	socket;
	private DatagramPacket	messageIn;
	private byte[]			buffer;

	private long			connectionID;
	private int				transactionID;

	private Timer			timerAnnounce;

	public ContentManager(final String ip, final int port, final MetainfoFile<?> info) {
		try {

			this.ip = InetAddress.getByName(ip);
			this.port = port;
			this.name = info.getInfo().getName();
			this.info_hash = info.getInfo().getHexInfoHash();
			this.size = info.getInfo().getLength();
			this.peers = new ArrayList<PeerInfo>();

			this.timerAnnounce = new Timer(3000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendAnnounce();
				}
			});

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
			this.timerAnnounce.start();
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

	public synchronized void updateContents(final List<PeerInfo> peers, final int leechers, final int seeders) {
		try {
			// Se quitan los que tienen ip = 0 PROVISIONAL
			List<PeerInfo> aux = new ArrayList<PeerInfo>();
			for (PeerInfo peer : peers) {
				if (peer.getIpAddress() != 0) {
					aux.add(peer);
				}
			}
			this.peers = aux;
			this.leechers = leechers;
			this.seeders = seeders;
			setChanged();
			notifyObservers();
		} catch (Exception ex) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), ex.toString());
			ex.printStackTrace();
		}
	}

	private void sendAnnounce() {
		AnnounceRequest announceRequest = new AnnounceRequest();
		announceRequest.setConnectionId(this.connectionID);
		announceRequest.setTransactionId(this.transactionID);
		announceRequest.setInfoHash(this.info_hash);
		// �?
		announceRequest.setPeerId(ByteUtils.createPeerId());
		announceRequest.setDownloaded(0);
		announceRequest.setUploaded(0);
		announceRequest.setLeft(this.size);
		// �?
		announceRequest.setEvent(Event.STARTED);
		// �?
		announceRequest.getPeerInfo().setIpAddress(0);
		// �?
		announceRequest.setKey(new Random().nextInt(Integer.MAX_VALUE));
		// �?
		announceRequest.setNumWant(-1);
		// �?
		announceRequest.getPeerInfo().setPort(28159);

		sendData(announceRequest);
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
					AnnounceResponse announceResponse = AnnounceResponse.parse(messageIn.getData());
					System.out.println("Announce recibido para: " + this.name);
					System.out.println("Seeders: " + announceResponse.getSeeders() + ", Leechers: "
							+ announceResponse.getLeechers() + ", Peers: " + announceResponse.getPeers().size());
					if (announceResponse.getTransactionId() == this.transactionID) {
						// Se cambia el delay del timer si es distinto y se
						// a�ade
						// margen de tiempo
						if (this.timerAnnounce.getDelay() != (announceResponse.getInterval() + ERROR_TIME_MARGIN)) {
							System.out.println("El tiempo es distinto se cambia de " + this.timerAnnounce.getDelay()
									+ " a " + (announceResponse.getInterval() + ERROR_TIME_MARGIN));
							this.timerAnnounce.setDelay(announceResponse.getInterval() + ERROR_TIME_MARGIN);
						}
						updateContents(announceResponse.getPeers(), announceResponse.getLeechers(),
								announceResponse.getSeeders());
					}
					break;

				case CONNECT:
					ConnectResponse connectResponse = ConnectResponse.parse(messageIn.getData());
					if (connectResponse.getTransactionId() == this.transactionID) {
						this.connectionID = connectResponse.getConnectionId();
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

	public String getName() {
		return name;
	}

	public String getInfo_hash() {
		return info_hash;
	}

	public long getSize() {
		return size;
	}

	public int getLeechers() {
		return leechers;
	}

	public int getSeeders() {
		return seeders;
	}

	public List<PeerInfo> getPeers() {
		return peers;
	}
}