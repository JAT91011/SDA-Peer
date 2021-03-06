package models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import javax.swing.Timer;

import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.tracker.protocol.udp.messages.AnnounceRequest;
import bitTorrent.tracker.protocol.udp.messages.AnnounceRequest.Event;
import bitTorrent.tracker.protocol.udp.messages.AnnounceResponse;
import bitTorrent.tracker.protocol.udp.messages.ConnectRequest;
import bitTorrent.tracker.protocol.udp.messages.ConnectResponse;
import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
import bitTorrent.util.ByteUtils;

public class ContentManager extends Observable {

	private static int	ERROR_TIME_MARGIN							= 500;
	private static int	DOWNLOAD_SPEED_PERCENT						= 2;
	private static int	MAX_ANNOUNCE_WITHOUT_RESPONSE_BEFORE_RESET	= 5;

	public enum Status {
		CONNECTING("Connecting..."), DOWNLOADING("Downloading"), WAITING_SEEDS("Waiting for seeds..."), STOPPED(
				"Stopped"), COMPLETED("Completed");

		private String value;

		private Status(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}

	private InetAddress		ip;
	private int				port;
	private String			name;
	private Status			status;
	private String			info_hash;
	private long			downloaded;
	private long			size;
	private int				leechers;
	private int				seeders;
	private List<PeerInfo>	peers;

	private long			connectionID;
	private int				transactionID;

	private Timer			timerAnnounce;
	private Timer			timerConnect;

	private int				numAnnounceSentWithoutResponse;

	public ContentManager(final String ip, final int port, final MetainfoFile<?> info) {
		try {

			this.ip = InetAddress.getByName(ip);
			this.port = port;
			this.name = info.getInfo().getName();
			this.status = Status.CONNECTING;
			this.info_hash = info.getInfo().getHexInfoHash();
			this.downloaded = 0;
			this.size = info.getInfo().getLength();
			this.peers = new ArrayList<PeerInfo>();
			this.numAnnounceSentWithoutResponse = 0;
			Random random = new Random();
			this.transactionID = random.nextInt(Integer.MAX_VALUE);
			while (ClientManager.getInstance().existTransactionID(this.transactionID)) {
				this.transactionID = random.nextInt(Integer.MAX_VALUE);
			}

			this.timerAnnounce = new Timer(3000, new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (status == Status.DOWNLOADING || status == Status.WAITING_SEEDS || status == Status.COMPLETED) {
						if (numAnnounceSentWithoutResponse < MAX_ANNOUNCE_WITHOUT_RESPONSE_BEFORE_RESET) {
							System.out.println("Envia announce");
							sendAnnounce();
							ContentManager.this.numAnnounceSentWithoutResponse++;
						} else {
							setStatus(Status.CONNECTING);
						}
					} else {
						ContentManager.this.status = Status.STOPPED;
					}
				}
			});

			this.timerConnect = new Timer(3000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (status == Status.CONNECTING) {
						System.out.println("Envia connect");
						sendConnect();
					} else {
						ContentManager.this.status = Status.STOPPED;
					}
				}
			});
			this.timerConnect.start();
			ClientManager.getInstance().addContentManager(this);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void sendConnect() {
		this.status = Status.CONNECTING;
		ConnectRequest connectRequest = new ConnectRequest();
		connectRequest.setTransactionId(this.transactionID);
		ClientManager.getInstance().sendData(connectRequest, this.ip, this.port);
	}

	private void sendAnnounce() {
		AnnounceRequest announceRequest = new AnnounceRequest();
		announceRequest.setConnectionId(this.connectionID);
		announceRequest.setTransactionId(this.transactionID);
		announceRequest.setInfoHash(ByteUtils.toByteArray(this.info_hash));
		announceRequest.setPeerId(ByteUtils.createPeerId());
		announceRequest.setDownloaded(this.downloaded);
		announceRequest.setUploaded(0);
		announceRequest.setLeft(this.size - this.downloaded);
		if (this.size == this.downloaded) {
			announceRequest.setEvent(Event.COMPLETED);
		} else {
			announceRequest.setEvent(Event.STARTED);
		}
		announceRequest.getPeerInfo().setIpAddress(0);
		announceRequest.setKey(new Random().nextInt(Integer.MAX_VALUE));
		announceRequest.setNumWant(-1);
		announceRequest.getPeerInfo().setPort(28159);
		ClientManager.getInstance().sendData(announceRequest, this.ip, this.port);
	}

	public synchronized void onConnectResponseReceived(ConnectResponse connectResponse) {
		this.timerConnect.stop();
		this.status = Status.DOWNLOADING;
		this.connectionID = connectResponse.getConnectionId();
		this.timerAnnounce.start();
		ContentManager.this.numAnnounceSentWithoutResponse = 0;
	}

	public synchronized void onAnnounceResponseReceived(AnnounceResponse announceResponse) {
		System.out.println("Announce recibido para: " + this.name);
		System.out
				.println("Seeders: " + announceResponse.getSeeders() + ", Leechers: " + announceResponse.getLeechers());
		ContentManager.this.numAnnounceSentWithoutResponse = 0;
		if (this.timerAnnounce.getDelay() != (announceResponse.getInterval() + ERROR_TIME_MARGIN)) {
			this.timerAnnounce.setDelay(announceResponse.getInterval() + ERROR_TIME_MARGIN);
		}

		this.peers = announceResponse.getPeers();
		List<PeerInfo> aux = new ArrayList<PeerInfo>();
		for (PeerInfo peer : this.peers) {
			if (peer.getIpAddress() != 0) {
				aux.add(peer);
			}
		}

		if (announceResponse.getSeeders() > 0) {
			this.downloaded += (this.size * DOWNLOAD_SPEED_PERCENT) / 100;
			if (this.downloaded >= this.size) {
				this.downloaded = this.size;
				this.status = Status.COMPLETED;
			} else {
				this.status = Status.DOWNLOADING;
			}
		} else {
			this.status = Status.WAITING_SEEDS;
		}

		this.peers = aux;
		this.leechers = announceResponse.getLeechers();
		this.seeders = announceResponse.getSeeders();
		setChanged();
		notifyObservers();
	}

	public void remove() {
		this.timerConnect.stop();
		this.timerAnnounce.stop();
		this.deleteObservers();
	}

	public int getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status.value;
	}

	public void setStatus(Status status) {
		this.status = status;
		if (status == Status.CONNECTING) {
			this.timerConnect.start();
			this.timerAnnounce.stop();
		} else if (status == Status.STOPPED) {
			this.timerConnect.stop();
			this.timerAnnounce.stop();
		}
		setChanged();
		notifyObservers();
	}

	public long getDownloaded() {
		return downloaded;
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