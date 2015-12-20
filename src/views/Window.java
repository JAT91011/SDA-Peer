package views;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Window extends JFrame {

	private static final long	serialVersionUID	= -8641413596663241575L;
	private static Window		instance;

	private Window() {
		super();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(null);
		setSize(750, 500);
		setIconImage((new ImageIcon("icons/app-icon.png")).getImage());
		setTitle("uTorrent");
		setLocationRelativeTo(null);
	}

	public static Window getInstance() {
		if (instance == null) {
			instance = new Window();
		}
		return instance;
	}
}