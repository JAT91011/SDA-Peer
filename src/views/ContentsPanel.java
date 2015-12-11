package views;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ContentsPanel extends JPanel implements Observer {

	private static final long		serialVersionUID	= 1276595089834953384L;
	private JTable					contentsTable;
	private final DefaultTableModel	modelTable;

	public ContentsPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final String[] header = { "ID", "Content", "Peers", "Seeds" };
		final String[][] content = new String[1][header.length];

		modelTable = new DefaultTableModel();
		modelTable.setDataVector(content, header);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(10, 10, 10, 10);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		contentsTable = new JTable(modelTable);
		scrollPane.setViewportView(contentsTable);

		contentsTable.getTableHeader().setReorderingAllowed(false);
		contentsTable.setShowVerticalLines(true);
		contentsTable.setShowHorizontalLines(true);
		contentsTable.setDragEnabled(false);
		contentsTable.setSelectionForeground(Color.WHITE);
		contentsTable.setSelectionBackground(Color.BLUE);
		contentsTable.setForeground(Color.BLACK);
		contentsTable.setBackground(Color.WHITE);
		contentsTable.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
		contentsTable.setRowHeight(30);

		contentsTable.getColumnModel().getColumn(0).setMinWidth(80);
		contentsTable.getColumnModel().getColumn(0).setMaxWidth(80);

		contentsTable.getColumnModel().getColumn(2).setMinWidth(80);
		contentsTable.getColumnModel().getColumn(2).setMaxWidth(80);

		contentsTable.getColumnModel().getColumn(3).setMinWidth(80);
		contentsTable.getColumnModel().getColumn(3).setMaxWidth(80);

		JButton btnDownload = new JButton("Download");
		btnDownload.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		GridBagConstraints gbc_btnDescargar = new GridBagConstraints();
		gbc_btnDescargar.insets = new Insets(0, 0, 10, 0);
		gbc_btnDescargar.gridx = 0;
		gbc_btnDescargar.gridy = 1;
		add(btnDownload, gbc_btnDescargar);

		contentsTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 15));
	}

	@Override
	public void update(Observable o, Object arg) {

	}
}