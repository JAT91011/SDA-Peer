package views;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import views.components.JSlidePanel;

public class Window extends JFrame {

	private static final long	serialVersionUID	= -8641413596663241575L;
	private static Window		instance;
	private JPanel				container;
	private JSlidePanel<JFrame>	slider;

	private Window() {
		super();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(null);
		setSize(750, 500);
		// setIconImage((new ImageIcon("icons/app-icon.png")).getImage());
		setTitle("uTorrentFake");
		setLocationRelativeTo(null);

		this.slider = new JSlidePanel<JFrame>(this);
		this.container = slider.getBasePanel();
		getContentPane().add(this.container, BorderLayout.CENTER);
	}

	public JSlidePanel<JFrame> getSlider() {
		return this.slider;
	}

	public void setContainer(JPanel panel) {
		if (container != null) {
			instance.getContentPane().remove(container);
		}
		container = panel;
		getContentPane().add(container, BorderLayout.CENTER);
	}

	public static Window getInstance() {
		if (instance == null) {
			instance = new Window();
		}
		return instance;
	}
}