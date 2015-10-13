package interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Ventana extends JFrame {

	private static final long	serialVersionUID	= -8641413596663241575L;
	private static Ventana		instance;
	private JPanel				container;

	private Ventana() {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(null);
		setSize(800, 600);
		// setIconImage((new ImageIcon("img/app-icon.png")).getImage());
		setMinimumSize(new Dimension(800, 600));
		setTitle("Peer");
		setLocationRelativeTo(null);

		JPanel container = new JPanel();
		getContentPane().add(container, BorderLayout.CENTER);
	}

	public void setContainer(JPanel panel) {
		if (container != null) {
			instance.getContentPane().remove(container);
		}
		container = panel;
		getContentPane().add(container, BorderLayout.CENTER);
	}

	public static Ventana getInstance() {
		if (instance == null) {
			instance = new Ventana();
		}
		return instance;
	}
}