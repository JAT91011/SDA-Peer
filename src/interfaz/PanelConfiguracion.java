package interfaz;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import utilidades.Propiedades;

public class PanelConfiguracion extends JPanel {

	private static final long		serialVersionUID	= 4959247560481979942L;

	private JTextField				txtIP;
	private JTextField				txtPuerto;
	private JLabel					lblRutaDescargas;
	private JButton					btnExaminar;
	private JTextField				txtRuta;
	private JButton					btnGuardar;
	private JLabel					lblIdioma;
	private JComboBox<String>		cboIdioma;
	private JLabel					lblApariencia;
	private JComboBox<String>		cboApariencia;

	private HashMap<String, String>	lookNFeelHashMap;
	private String					currentLookAndFeel;

	private static String			IPADDRESS_PATTERN	= "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
																+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
																+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
																+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public PanelConfiguracion() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
				0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblIP = new JLabel("Direcci\u00F3n IP:");
		lblIP.setForeground(Color.BLACK);
		lblIP.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_lblIP = new GridBagConstraints();
		gbc_lblIP.anchor = GridBagConstraints.WEST;
		gbc_lblIP.insets = new Insets(15, 15, 5, 5);
		gbc_lblIP.gridx = 0;
		gbc_lblIP.gridy = 0;
		add(lblIP, gbc_lblIP);

		txtIP = new JTextField(Propiedades.getIP());
		txtIP.setForeground(Color.BLACK);
		txtIP.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_txtIP = new GridBagConstraints();
		gbc_txtIP.anchor = GridBagConstraints.WEST;
		gbc_txtIP.insets = new Insets(15, 5, 5, 15);
		gbc_txtIP.gridx = 1;
		gbc_txtIP.gridy = 0;
		add(txtIP, gbc_txtIP);
		txtIP.setColumns(20);

		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setForeground(Color.BLACK);
		lblPuerto.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_lblPuerto = new GridBagConstraints();
		gbc_lblPuerto.anchor = GridBagConstraints.WEST;
		gbc_lblPuerto.insets = new Insets(5, 15, 5, 5);
		gbc_lblPuerto.gridx = 0;
		gbc_lblPuerto.gridy = 1;
		add(lblPuerto, gbc_lblPuerto);

		txtPuerto = new JTextField(Integer.toString(Propiedades.getPort()));
		txtPuerto.setForeground(Color.BLACK);
		txtPuerto.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtPuerto.setColumns(20);
		GridBagConstraints gbc_txtPuerto = new GridBagConstraints();
		gbc_txtPuerto.insets = new Insets(5, 5, 5, 15);
		gbc_txtPuerto.anchor = GridBagConstraints.WEST;
		gbc_txtPuerto.gridx = 1;
		gbc_txtPuerto.gridy = 1;
		add(txtPuerto, gbc_txtPuerto);

		lblRutaDescargas = new JLabel("Ruta descargas:");
		lblRutaDescargas.setForeground(Color.BLACK);
		lblRutaDescargas.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_lblRutaDescargas = new GridBagConstraints();
		gbc_lblRutaDescargas.anchor = GridBagConstraints.EAST;
		gbc_lblRutaDescargas.insets = new Insets(20, 15, 5, 5);
		gbc_lblRutaDescargas.gridx = 0;
		gbc_lblRutaDescargas.gridy = 2;
		add(lblRutaDescargas, gbc_lblRutaDescargas);

		txtRuta = new JTextField();
		txtRuta.setForeground(Color.BLACK);
		txtRuta.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtRuta.setColumns(20);
		GridBagConstraints gbc_txtRuta = new GridBagConstraints();
		gbc_txtRuta.insets = new Insets(20, 5, 5, 5);
		gbc_txtRuta.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtRuta.gridx = 1;
		gbc_txtRuta.gridy = 2;
		add(txtRuta, gbc_txtRuta);

		btnExaminar = new JButton("Examinar");
		btnExaminar.setForeground(Color.BLACK);
		btnExaminar.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_btnExaminar = new GridBagConstraints();
		gbc_btnExaminar.insets = new Insets(20, 5, 5, 15);
		gbc_btnExaminar.gridx = 2;
		gbc_btnExaminar.gridy = 2;
		add(btnExaminar, gbc_btnExaminar);

		lblIdioma = new JLabel("Idioma:");
		lblIdioma.setForeground(Color.BLACK);
		lblIdioma.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_lblIdioma = new GridBagConstraints();
		gbc_lblIdioma.anchor = GridBagConstraints.WEST;
		gbc_lblIdioma.insets = new Insets(20, 15, 5, 5);
		gbc_lblIdioma.gridx = 0;
		gbc_lblIdioma.gridy = 3;
		add(lblIdioma, gbc_lblIdioma);

		cboIdioma = new JComboBox<String>();
		cboIdioma.setForeground(Color.BLACK);
		cboIdioma.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_cboIdioma = new GridBagConstraints();
		gbc_cboIdioma.anchor = GridBagConstraints.WEST;
		gbc_cboIdioma.insets = new Insets(20, 5, 5, 15);
		gbc_cboIdioma.gridx = 1;
		gbc_cboIdioma.gridy = 3;
		add(cboIdioma, gbc_cboIdioma);

		lblApariencia = new JLabel("Apariencia:");
		lblApariencia.setForeground(Color.BLACK);
		lblApariencia.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_lblApariencia = new GridBagConstraints();
		gbc_lblApariencia.anchor = GridBagConstraints.WEST;
		gbc_lblApariencia.insets = new Insets(5, 15, 5, 5);
		gbc_lblApariencia.gridx = 0;
		gbc_lblApariencia.gridy = 4;
		add(lblApariencia, gbc_lblApariencia);

		cboApariencia = new JComboBox<String>(getAvailableLF());
		cboApariencia.setForeground(Color.BLACK);
		cboApariencia.setFont(new Font("Dialog", Font.PLAIN, 14));
		cboApariencia.setSelectedItem(currentLookAndFeel);
		GridBagConstraints gbc_cboApariencia = new GridBagConstraints();
		gbc_cboApariencia.anchor = GridBagConstraints.WEST;
		gbc_cboApariencia.insets = new Insets(5, 5, 5, 15);
		gbc_cboApariencia.gridx = 1;
		gbc_cboApariencia.gridy = 4;
		add(cboApariencia, gbc_cboApariencia);

		btnGuardar = new JButton("Guardar");
		btnGuardar.setForeground(Color.BLACK);
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PanelConfiguracion.this.GuardarConfiguracion();
			}
		});
		btnGuardar.setFont(new Font("Dialog", Font.PLAIN, 14));
		GridBagConstraints gbc_btnGuardar = new GridBagConstraints();
		gbc_btnGuardar.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnGuardar.insets = new Insets(15, 0, 15, 15);
		gbc_btnGuardar.gridx = 2;
		gbc_btnGuardar.gridy = 6;
		add(btnGuardar, gbc_btnGuardar);
	}

	private Vector<String> getAvailableLF() {
		final LookAndFeelInfo lfs[] = UIManager.getInstalledLookAndFeels();

		lookNFeelHashMap = new HashMap<>(lfs.length);
		final Vector<String> v = new Vector<>(lfs.length);

		for (final LookAndFeelInfo lf2 : lfs) {
			lookNFeelHashMap.put(lf2.getName(), lf2.getClassName());
			v.add(lf2.getName());
			if (Propiedades.getLookAndFeel().equals(lf2.getClassName())) {
				currentLookAndFeel = lf2.getName();
			}
		}
		return v;
	}

	private void GuardarConfiguracion() {

		String ip = txtIP.getText().trim();
		int port = Integer.parseInt(txtPuerto.getText().trim());
		boolean correcto = true;

		if (!ip.matches(IPADDRESS_PATTERN)) {
			correcto = false;
		}

		if (port > 65535) {
			correcto = false;
		}

		if (correcto) {
			Propiedades.setIP(ip);
			Propiedades.setPort(port);
			Propiedades.setLookAndFeelClass(lookNFeelHashMap.get(cboApariencia
					.getSelectedItem()));
			try {
				UIManager.setLookAndFeel(lookNFeelHashMap.get(cboApariencia
						.getSelectedItem()));
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
			SwingUtilities.updateComponentTreeUI(Ventana.getInstance());
		}
	}
}