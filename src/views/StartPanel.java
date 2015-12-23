package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
import bitTorrent.util.ByteUtils;
import models.ContentManager;
import utilities.ErrorsLog;
import views.components.ToolBar;

public class StartPanel extends JPanel implements Observer {

	private static final long							serialVersionUID	= 4986034677227823532L;

	private final ToolBar								toolBar;

	private String[]									headerContents;
	private String[]									headerPeers;

	private ConcurrentHashMap<String, ContentManager>	contentsManagers;

	private JTable										tableContents;
	private DefaultTableModel							modelTableContents;
	private JTable										tablePeers;
	private DefaultTableModel							modelTablePeers;

	public StartPanel() {

		this.contentsManagers = new ConcurrentHashMap<String, ContentManager>();

		setLayout(new BorderLayout(0, 0));

		toolBar = new ToolBar();
		add(toolBar, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		splitPane.setPreferredSize(new Dimension(0, 300));
		splitPane.setSize(new Dimension(0, 300));
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPaneTop = new JScrollPane();
		splitPane.setLeftComponent(scrollPaneTop);

		headerContents = new String[6];
		headerContents[0] = "#";
		headerContents[1] = "Name";
		headerContents[2] = "Size";
		headerContents[3] = "Status";
		headerContents[4] = "Seeds";
		headerContents[5] = "Peers";

		modelTableContents = new DefaultTableModel();
		modelTableContents.setDataVector(null, headerContents);

		tableContents = new JTable(modelTableContents);
		tableContents.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tableContents.getSelectedRow() > -1) {
					updatePeersTableData();
					// print first column value from selected row
					System.out.println(tableContents.getSelectedRow());
				}
			}
		});
		scrollPaneTop.setViewportView(tableContents);

		tableContents.getTableHeader().setReorderingAllowed(false);
		tableContents.setShowVerticalLines(true);
		tableContents.setShowHorizontalLines(true);
		tableContents.setDragEnabled(false);
		tableContents.setSelectionForeground(Color.WHITE);
		tableContents.setSelectionBackground(Color.BLUE);
		tableContents.setForeground(Color.BLACK);
		tableContents.setBackground(Color.WHITE);
		tableContents.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
		tableContents.setRowHeight(30);
		tableContents.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 15));

		JScrollPane scrollPaneBottom = new JScrollPane();
		splitPane.setRightComponent(scrollPaneBottom);

		headerPeers = new String[3];
		headerPeers[0] = "#";
		headerPeers[1] = "IP";
		headerPeers[2] = "Port";

		modelTablePeers = new DefaultTableModel();
		modelTablePeers.setDataVector(null, headerPeers);

		tablePeers = new JTable(modelTablePeers);
		scrollPaneBottom.setViewportView(tablePeers);
		tablePeers.getTableHeader().setReorderingAllowed(false);
		tablePeers.setShowVerticalLines(true);
		tablePeers.setShowHorizontalLines(true);
		tablePeers.setDragEnabled(false);
		tablePeers.setSelectionForeground(Color.WHITE);
		tablePeers.setSelectionBackground(Color.BLUE);
		tablePeers.setForeground(Color.BLACK);
		tablePeers.setBackground(Color.WHITE);
		tablePeers.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
		tablePeers.setRowHeight(30);
		tablePeers.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 15));
	}

	private void updatePeersTableData() {

		List<PeerInfo> peers = new ArrayList<PeerInfo>();
		if (this.tableContents.getSelectedRow() > -1) {
			for (Entry<String, ContentManager> entry : this.contentsManagers.entrySet()) {
				if (entry.getValue().getName()
						.equals(this.tableContents.getModel().getValueAt(this.tableContents.getSelectedRow(), 1))) {
					peers = entry.getValue().getPeers();
				}
			}
		}
		System.out.println("FILAS: " + peers.size());

		String[][] data = new String[peers.size()][this.headerPeers.length];

		for (int i = 0; i < peers.size(); i++) {
			data[i][0] = Integer.toString(i + 1);
			data[i][1] = ByteUtils.toStringIpAddress(peers.get(i).getIpAddress());
			data[i][2] = Integer.toString(peers.get(i).getPort());
		}

		this.modelTablePeers = new DefaultTableModel();
		this.modelTablePeers.setDataVector(data, headerPeers);
		tablePeers.setModel(this.modelTablePeers);
	}

	public JTable getTable() {
		return this.tableContents;
	}

	public String addContent(final MetainfoFile<?> newContent) {

		if (contentsManagers.containsKey(newContent.getInfo().getHexInfoHash())) {
			return "El torrent ya ha sido añadido";
		}

		if (newContent.getUDPAnnounceList().size() == 0) {
			return "No se ha encontrado una dirección UDP";
		}

		final String[] data = new String[6];
		data[0] = Integer.toString(this.tableContents.getRowCount() + 1);
		data[1] = newContent.getInfo().getName();
		data[2] = bytes2MegaBytes(newContent.getInfo().getLength()) + " MB.";
		data[3] = "Connecting";
		this.modelTableContents.addRow(data);

		String[] aux = newContent.getUDPAnnounceList().get(0).split("/");

		System.out.println("AUX: " + aux[2]);
		String ip = aux[2].split(":")[0];
		String port = aux[2].split(":")[1];

		System.out.println("IP: " + ip);
		System.out.println("PORT: " + port);

		new ConnectThread(this, ip, Integer.parseInt(port), newContent).start();

		return "";
	}

	private double bytes2MegaBytes(final int bytes) {
		return (bytes / 1024) / 1024;
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			ContentManager contentManager = (ContentManager) o;

			int row = 0;
			while (row < this.modelTableContents.getRowCount()) {
				if (this.tableContents.getModel().getValueAt(row, 1).equals(contentManager.getName())) {
					break;
				} else {
					row++;
				}
			}

			this.tableContents.getModel().setValueAt(contentManager.getSeeders(), row, 4);
			this.tableContents.getModel().setValueAt(contentManager.getLeechers(), row, 5);

			if (contentManager.getSeeders() > 0) {
				this.tableContents.getModel().setValueAt("Downloading", row, 3);
			} else {
				this.tableContents.getModel().setValueAt("Waiting for seeds", row, 3);
			}
			tablePeers.setModel(this.modelTablePeers);

			if (this.tableContents.getSelectedRow() > -1) {
				if (this.tableContents.getModel().getValueAt(this.tableContents.getSelectedRow(), 1)
						.equals(contentManager.getName())) {
					updatePeersTableData();
				}
			}

		} catch (Exception e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
		}
	}

	public ConcurrentHashMap<String, ContentManager> getContentsManagers() {
		return this.contentsManagers;
	}
}

class ConnectThread extends Thread {

	private String			ip;
	private int				port;
	private StartPanel		startPanel;
	private MetainfoFile<?>	content;

	public ConnectThread(final StartPanel startPanel, final String ip, final int port, final MetainfoFile<?> content) {
		this.startPanel = startPanel;
		this.ip = ip;
		this.port = port;
		this.content = content;
	}

	public void run() {
		ContentManager contentManager = new ContentManager(ip, port, content);
		contentManager.addObserver(startPanel);
		this.startPanel.getContentsManagers().put(content.getInfo().getHexInfoHash(), contentManager);
		contentManager.start();
	}
}