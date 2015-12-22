package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import bitTorrent.metainfo.MetainfoFile;
import models.ClientManager;
import utilities.ErrorsLog;
import views.components.ToolBar;

public class StartPanel extends JPanel implements Observer {

	private static final long							serialVersionUID	= 4986034677227823532L;

	private final ToolBar								toolBar;
	private JTable										table;
	private DefaultTableModel							modelTable;

	private String[]									header;

	private ConcurrentHashMap<String, ClientManager>	clientManagers;

	public StartPanel() {

		this.clientManagers = new ConcurrentHashMap<String, ClientManager>();

		setLayout(new BorderLayout(0, 0));

		toolBar = new ToolBar();
		add(toolBar, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		header = new String[6];
		header[0] = "#";
		header[1] = "Name";
		header[2] = "Size";
		header[3] = "Status";
		header[4] = "Seeds";
		header[5] = "Peers";
		final String[][] content = new String[0][header.length];

		modelTable = new DefaultTableModel();
		modelTable.setDataVector(content, header);

		table = new JTable(modelTable);
		scrollPane.setViewportView(table);

		table.getTableHeader().setReorderingAllowed(false);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setDragEnabled(false);
		table.setSelectionForeground(Color.WHITE);
		table.setSelectionBackground(Color.BLUE);
		table.setForeground(Color.BLACK);
		table.setBackground(Color.WHITE);
		table.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
		table.setRowHeight(30);

		table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 15));
	}

	public JTable getTable() {
		return this.table;
	}

	public String addContent(final MetainfoFile<?> newContent) {

		if (clientManagers.containsKey(newContent.getInfo().getHexInfoHash())) {
			return "El torrent ya ha sido añadido";
		}

		if (newContent.getUDPAnnounceList().size() == 0) {
			return "No se ha encontrado una dirección UDP";
		}

		final String[] data = new String[6];
		data[0] = Integer.toString(this.table.getRowCount() + 1);
		data[1] = newContent.getInfo().getName();
		data[2] = Integer.toString(newContent.getInfo().getLength());
		data[3] = "Connecting";
		this.modelTable.addRow(data);

		String[] aux = newContent.getUDPAnnounceList().get(0).split("/");

		System.out.println("AUX: " + aux[2]);
		String ip = aux[2].split(":")[0];
		String port = aux[2].split(":")[1];

		System.out.println("IP: " + ip);
		System.out.println("PORT: " + port);

		new ConnectThread(this, ip, Integer.parseInt(port), newContent).start();

		return "";
	}

	public ConcurrentHashMap<String, ClientManager> getClientManagers() {
		return clientManagers;
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			ClientManager clientManager = (ClientManager) o;

			int row = 0;
			while (row < this.modelTable.getRowCount()) {
				if (this.table.getModel().getValueAt(row, 1).equals(clientManager.getInfo().getInfo().getName())) {
					break;
				} else {
					row++;
				}
			}

			this.table.getModel().setValueAt(clientManager.getContent().getSeeders(), row, 4);
			this.table.getModel().setValueAt(clientManager.getContent().getLeechers(), row, 5);

			if (clientManager.getContent().getSeeders() > 0) {
				this.table.getModel().setValueAt("Downloading", row, 3);
			} else {
				this.table.getModel().setValueAt("Waiting for seeds", row, 3);
			}

		} catch (Exception e) {
			ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
			}.getClass().getEnclosingMethod().getName(), e.toString());
			e.printStackTrace();
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
		ClientManager clientManager = new ClientManager(ip, port, content);
		clientManager.addObserver(startPanel);
		this.startPanel.getClientManagers().put(content.getInfo().getHexInfoHash(), clientManager);
		clientManager.start();
	}
}