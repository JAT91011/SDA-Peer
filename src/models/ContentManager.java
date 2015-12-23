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

	private static int ERROR_TIME_MARGIN = 500;

	public enum Status {
		CONNECTING("Connecting..."), DOWNLOADING("Downloading"), WAITING_SEEDS("Waiting for seeds..."), STOPPED(
				"Stopped"), SHARING("Sharing");

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
	private long			size;
	private int				leechers;
	private int				seeders;
	private List<PeerInfo>	peers;

	private boolean			enable;

	private long			connectionID;
	private int				transactionID;

	private Timer			timerAnnounce;
	private Timer			timerConnect;

	public ContentManager(final String ip, final int port, final MetainfoFile<?> info) {
		try {

			this.ip = InetAddress.getByName(ip);
			this.port = port;
			this.name = info.getInfo().getName();
			this.info_hash = info.getInfo().getHexInfoHash();
			this.size = info.getInfo().getLength();
			this.peers = new ArrayList<PeerInfo>();
			Random random = new Random();
			this.transactionID = random.nextInt(Integer.MAX_VALUE);
			while (ClientManager.getInstance().existTransactionID(this.transactionID)) {
				this.transactionID = random.nextInt(Integer.MAX_VALUE);
			}
			this.enable = true;
			ClientManager.getInstance().addContentManager(this);

			this.timerAnnounce = new Timer(3000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (enable) {
						sendAnnounce();
					} else {
						ContentManager.this.status = Status.STOPPED;
					}
				}
			});

			this.timerConnect = new Timer(5000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (enable) {
						sendConnect();
					} else {
						ContentManager.this.status = Status.STOPPED;
					}
				}
			});
			this.timerConnect.start();

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
		announceRequest.setInfoHash(this.info_hash);
		announceRequest.setPeerId(ByteUtils.createPeerId());
		announceRequest.setDownloaded(0);
		announceRequest.setUploaded(0);
		announceRequest.setLeft(this.size);
		announceRequest.setEvent(Event.STARTED);
		announceRequest.getPeerInfo().setIpAddress(0);
		announceRequest.setKey(new Random().nextInt(Integer.MAX_VALUE));
		announceRequest.setNumWant(-1);
		announceRequest.getPeerInfo().setPort(28159);
		ClientManager.getInstance().sendData(announceRequest, this.ip, this.port);
	}

	public synchronized void onConnectResponseReceived(ConnectResponse connectResponse) {
		this.timerConnect.stop();
		this.connectionID = connectResponse.getConnectionId();
		this.timerAnnounce.start();
	}

	public synchronized void onAnnounceResponseReceived(AnnounceResponse announceResponse) {
		System.out.println("Announce recibido para: " + this.name);
		System.out
				.println("Seeders: " + announceResponse.getSeeders() + ", Leechers: " + announceResponse.getLeechers());

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
			this.status = Status.DOWNLOADING;
		} else {
			this.status = Status.WAITING_SEEDS;
		}

		this.peers = aux;
		this.leechers = announceResponse.getLeechers();
		this.seeders = announceResponse.getSeeders();
		setChanged();
		notifyObservers();
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