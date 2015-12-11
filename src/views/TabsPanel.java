package views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controllers.Controller;
import views.components.JSlidePanel;

public class TabsPanel extends JPanel implements MouseListener {

	private static final long	serialVersionUID	= 8155818731609154350L;

	private JSlidePanel<JPanel>	slider;

	private JLabel				lblDisconnect;
	private JLabel				lblContents;
	private JLabel				lblActivity;

	private JPanel				container;
	private boolean				watchingContents;

	private Controller			configController;

	public TabsPanel() {
		setOpaque(true);

		this.configController = new Controller();
		this.slider = new JSlidePanel<JPanel>(this);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 60, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 50, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblDisconnect = new JLabel(new ImageIcon("icons/disconnect-icon.png"));
		lblDisconnect.addMouseListener(this);
		lblDisconnect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblDisconnect.setText("");
		lblDisconnect.setBackground(new Color(240, 128, 128));
		lblDisconnect.setOpaque(true);
		GridBagConstraints gbc_lblDisconnect = new GridBagConstraints();
		gbc_lblDisconnect.fill = GridBagConstraints.BOTH;
		gbc_lblDisconnect.gridx = 0;
		gbc_lblDisconnect.gridy = 0;
		add(lblDisconnect, gbc_lblDisconnect);

		lblContents = new JLabel("Contents");
		lblContents.addMouseListener(this);
		lblContents.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblContents.setForeground(Color.WHITE);
		lblContents.setHorizontalAlignment(SwingConstants.CENTER);
		lblContents.setOpaque(true);
		lblContents.setBackground(new Color(102, 205, 170));
		lblContents.setFont(new Font("Tahoma", Font.PLAIN, 23));
		GridBagConstraints gbc_lblContents = new GridBagConstraints();
		gbc_lblContents.fill = GridBagConstraints.BOTH;
		gbc_lblContents.gridx = 1;
		gbc_lblContents.gridy = 0;
		add(lblContents, gbc_lblContents);

		lblActivity = new JLabel("Activity");
		lblActivity.addMouseListener(this);
		lblActivity.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblActivity.setForeground(Color.WHITE);
		lblActivity.setHorizontalAlignment(SwingConstants.CENTER);
		lblActivity.setOpaque(true);
		lblActivity.setBackground(new Color(255, 218, 185));
		lblActivity.setFont(new Font("Tahoma", Font.PLAIN, 23));
		GridBagConstraints gbc_lblPeers = new GridBagConstraints();
		gbc_lblPeers.fill = GridBagConstraints.BOTH;
		gbc_lblPeers.gridx = 2;
		gbc_lblPeers.gridy = 0;
		add(lblActivity, gbc_lblPeers);

		container = slider.getBasePanel();
		GridBagConstraints gbc_panContent = new GridBagConstraints();
		gbc_panContent.gridwidth = 3;
		gbc_panContent.fill = GridBagConstraints.BOTH;
		gbc_panContent.gridx = 0;
		gbc_panContent.gridy = 1;
		add(container, gbc_panContent);

		this.watchingContents = false;
		this.slider.addComponent(new ContentsPanel());
		this.slider.addComponent(new ActivityPanel());
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == lblDisconnect) {
			this.configController.disconnect();
			Window.getInstance().getSlider().slideRight();
		} else if (e.getSource() == lblContents) {
			if (!this.watchingContents) {
				this.slider.slideTop();
				this.watchingContents = true;
			}
		} else if (e.getSource() == lblActivity) {
			if (this.watchingContents) {
				this.slider.slideRight();
				this.watchingContents = false;
			}
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}
}