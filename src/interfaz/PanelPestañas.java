package interfaz;

import javax.swing.JTabbedPane;

public class PanelPestañas extends JTabbedPane {

	private static final long	serialVersionUID	= 8155818731609154350L;
	private PanelConfiguracion	panelConfiguracion;
	private PanelContenidos		panelContenidos;
	private PanelActividad		panelActividad;

	public PanelPestañas() {
		panelConfiguracion = new PanelConfiguracion();
		addTab("Configuración", null, panelConfiguracion, null);

		panelContenidos = new PanelContenidos();
		addTab("Contenidos", null, panelContenidos, null);

		panelActividad = new PanelActividad();
		addTab("Actividad", null, panelActividad, null);
	}
}