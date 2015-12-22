package entities;

import java.util.List;

import bitTorrent.tracker.protocol.udp.messages.PeerInfo;

public class Content {

	private String			name;
	private int				size;
	private String			status;
	private List<PeerInfo>	peers;
	private int				seeders;
	private int				leechers;

	public Content() {

	}

	public Content(String name, int size, String status, List<PeerInfo> peers, int seeders, int leechers) {
		super();
		this.name = name;
		this.size = size;
		this.status = status;
		this.peers = peers;
		this.seeders = seeders;
		this.leechers = leechers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<PeerInfo> getPeers() {
		return peers;
	}

	public void setPeers(List<PeerInfo> peers) {
		this.peers = peers;
	}

	public int getSeeders() {
		return seeders;
	}

	public void setSeeders(int seeders) {
		this.seeders = seeders;
	}

	public int getLeechers() {
		return leechers;
	}

	public void setLeechers(int leechers) {
		this.leechers = leechers;
	}
}