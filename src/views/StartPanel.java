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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import bitTorrent.metainfo.MetainfoFile;
import bitTorrent.tracker.protocol.udp.messages.PeerInfo;
import bitTorrent.util.ByteUtils;
import models.ClientManager;
import models.ContentManager;
import utilities.ErrorsLog;
import views.components.ToolBar;

public class StartPanel extends JPanel implements Observer {

	private static final long	serialVersionUID	= 4986034677227823532L;

	private final ToolBar		toolBar;

	private String[]			headerContents;
	private String[]			headerPeers;

	private JTable				tableContents;
	private DefaultTableModel	modelTableContents;
	private JTable				tablePeers;
	private DefaultTableModel	modelTablePeers;

	public StartPanel() {

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

		headerContents = new String[7];
		headerContents[0] = "#";
		headerContents[1] = "Name";
		headerContents[2] = "Size";
		headerContents[3] = "Status";
		headerContents[4] = "Downloaded";
		headerContents[5] = "Seeds";
		headerContents[6] = "Peers";

		modelTableContents = new DefaultTableModel();
		modelTableContents.setDataVector(null, headerContents);

		tableContents = new JTable(modelTableContents);
		tableContents.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tableContents.getSelectedRow() > -1) {
					updatePeersTableData();
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
		tableContents.getColumnModel().getColumn(0).setMinWidth(50);
		tableContents.getColumnModel().getColumn(0).setMaxWidth(50);

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
		tablePeers.getColumnModel().getColumn(0).setMinWidth(50);
		tablePeers.getColumnModel().getColumn(0).setMaxWidth(50);
	}

	private void updatePeersTableData() {

		List<PeerInfo> peers = new ArrayList<PeerInfo>();
		if (this.tableContents.getSelectedRow() > -1) {
			for (Entry<Integer, ContentManager> entry : ClientManager.getInstance().getContentsManagers().entrySet()) {
				if (entry.getValue().getName()
						.equals(this.tableContents.getModel().getValueAt(this.tableContents.getSelectedRow(), 1))) {
					peers = entry.getValue().getPeers();
				}
			}
		}

		String[][] data = new String[peers.size()][this.headerPeers.length];

		for (int i = 0; i < peers.size(); i++) {
			data[i][0] = Integer.toString(i + 1);
			data[i][1] = ByteUtils.toStringIpAddress(peers.get(i).getIpAddress());
			data[i][2] = Integer.toString(peers.get(i).getPort());
		}

		this.modelTablePeers = new DefaultTableModel();
		this.modelTablePeers.setDataVector(data, headerPeers);
		this.tablePeers.setModel(this.modelTablePeers);
	}

	public JTable getTable() {
		return this.tableContents;
	}

	public String addContent(final MetainfoFile<?> newContent) {

		if (ClientManager.getInstance().existInfoHash(newContent.getInfo().getHexInfoHash())) {
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

	private double bytes2MegaBytes(final long bytes) {
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

			this.tableContents.getModel().setValueAt(contentManager.getStatus(), row, 3);
			this.tableContents.getModel().setValueAt(bytes2MegaBytes(contentManager.getDownloaded()) + " MB.", row, 4);
			this.tableContents.getModel().setValueAt(contentManager.getSeeders(), row, 5);
			this.tableContents.getModel().setValueAt(contentManager.getLeechers(), row, 6);
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

	public void removeSelectedTorrent() {
		int selectedRow = this.tableContents.getSelectedRow();
		if (selectedRow > -1) {
			String name = (String) this.tableContents.getModel().getValueAt(selectedRow, 1);
			ClientManager.getInstance().removeContent(name);
			this.modelTableContents.removeRow(selectedRow);
			if (selectedRow - 1 >= 0) {
				this.tableContents.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
				updatePeersTableData();
			} else {
				for (int i = 0; i < this.modelTablePeers.getRowCount(); i++) {
					this.modelTablePeers = new DefaultTableModel(null, this.headerPeers);
					this.tablePeers.setModel(this.modelTablePeers);
				}
			}
		}
	}

	public void pauseSelectedTorrent() {
		int selectedRow = this.tableContents.getSelectedRow();
		if (selectedRow > -1) {
			String name = (String) this.tableContents.getModel().getValueAt(selectedRow, 1);
			ClientManager.getInstance().pauseContent(name);
		}
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
	}
}