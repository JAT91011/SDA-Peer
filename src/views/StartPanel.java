package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import bitTorrent.metainfo.MetainfoFile;
import views.components.ToolBar;

public class StartPanel extends JPanel implements Observer {

	private static final long		serialVersionUID	= 4986034677227823532L;

	private final ToolBar			toolBar;
	private JTable					table;
	private DefaultTableModel		modelTable;

	private String[]				header;

	private Vector<MetainfoFile<?>>	contents;

	public StartPanel() {

		this.contents = new Vector<MetainfoFile<?>>();
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

	public boolean addContent(final MetainfoFile<?> newContent) {
		for (MetainfoFile<?> content : contents) {
			if (content.getInfo().getHexInfoHash().equals(newContent.getInfo().getHexInfoHash())) {
				return false;
			}
		}
		this.contents.add(newContent);
		final String[] data = new String[6];
		data[0] = Integer.toString(this.table.getRowCount() + 1);
		data[1] = newContent.getInfo().getName();
		data[2] = Integer.toString(newContent.getInfo().getLength());
		this.modelTable.addRow(data);
		return true;
	}

	@Override
	public void update(Observable o, Object arg) {

	}
}