package aplicacion;

import interfaz.PanelPestanas;
import interfaz.Ventana;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utilidades.Propiedades;

public class Aplicacion {

	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(Propiedades.getLookAndFeel());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Ventana.getInstance().setContentPane(new PanelPestanas());
		Ventana.getInstance().setVisible(true);
	}
}