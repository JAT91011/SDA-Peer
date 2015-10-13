package interfaz;

import javax.swing.JTabbedPane;

public class PanelPesta�as extends JTabbedPane {

	private static final long	serialVersionUID	= 8155818731609154350L;
	private PanelConfiguracion	panelConfiguracion;
	private PanelContenidos		panelContenidos;
	private PanelActividad		panelActividad;

	public PanelPesta�as() {
		panelConfiguracion = new PanelConfiguracion();
		addTab("Configuraci�n", null, panelConfiguracion, null);

		panelContenidos = new PanelContenidos();
		addTab("Contenidos", null, panelContenidos, null);

		panelActividad = new PanelActividad();
		addTab("Actividad", null, panelActividad, null);
	}
}