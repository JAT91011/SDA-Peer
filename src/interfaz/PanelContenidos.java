package interfaz;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelContenidos extends JPanel {

	private static final long		serialVersionUID	= 1276595089834953384L;
	private JTable					tablaContenidos;
	private final DefaultTableModel	modelTable;

	public PanelContenidos() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final String[] header = { "ID", "Contenido", "Peers", "Seeds" };
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

		tablaContenidos = new JTable(modelTable);
		scrollPane.setViewportView(tablaContenidos);

		tablaContenidos.getTableHeader().setReorderingAllowed(false);
		tablaContenidos.setShowVerticalLines(true);
		tablaContenidos.setShowHorizontalLines(true);
		tablaContenidos.setDragEnabled(false);
		tablaContenidos.setSelectionForeground(Color.WHITE);
		tablaContenidos.setSelectionBackground(Color.BLUE);
		tablaContenidos.setForeground(Color.BLACK);
		tablaContenidos.setBackground(Color.WHITE);
		tablaContenidos.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
		tablaContenidos.setRowHeight(30);

		tablaContenidos.getColumnModel().getColumn(0).setMinWidth(80);
		tablaContenidos.getColumnModel().getColumn(0).setMaxWidth(80);

		tablaContenidos.getColumnModel().getColumn(2).setMinWidth(80);
		tablaContenidos.getColumnModel().getColumn(2).setMaxWidth(80);

		tablaContenidos.getColumnModel().getColumn(3).setMinWidth(80);
		tablaContenidos.getColumnModel().getColumn(3).setMaxWidth(80);

		JButton btnDescargar = new JButton("Descargar");
		btnDescargar.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		GridBagConstraints gbc_btnDescargar = new GridBagConstraints();
		gbc_btnDescargar.insets = new Insets(0, 0, 10, 0);
		gbc_btnDescargar.gridx = 0;
		gbc_btnDescargar.gridy = 1;
		add(btnDescargar, gbc_btnDescargar);

		tablaContenidos.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 15));
	}
}