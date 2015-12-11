package views.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ITextField extends JTextField implements FocusListener {

	private String				hint;
	private boolean				showingHint;
	private Icon				icon;
	private Icon				errorIcon;
	private boolean				showError;

	private static final long	serialVersionUID	= 5728251792417526726L;

	/**
	 * @param hint
	 *            The hint text
	 * @param icon
	 *            The icon
	 */
	public ITextField(String hint) {
		super(hint);
		setOpaque(true);
		this.hint = hint;
		this.showingHint = true;
		this.setFont(new Font("Calibri", Font.PLAIN, 20));
		super.addFocusListener(this);
		this.showError = false;
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
			showingHint = false;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
			showingHint = true;
		}
	}

	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}

	/**
	 * @param hint
	 *            Change the text as hint text
	 */
	public void showAsHint(boolean hint) {
		this.showingHint = hint;
	}

	/**
	 * @param icon
	 *            The icon to be displayed
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * @param icon
	 *            The error icon to be displayed
	 */
	public void setErrorIcon(Icon icon) {
		this.errorIcon = icon;
	}

	/**
	 * Method to display error icon
	 */
	public void showError() {
		this.showError = true;
	}

	/**
	 * Method to hide error icon
	 */
	public void hideError() {
		this.showError = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (showError && errorIcon != null) {
			if (icon != null) {
				int iconHeight = icon.getIconHeight();
				int iconWidth = icon.getIconWidth();
				int iconHeight2 = errorIcon.getIconHeight();
				int iconWidth2 = errorIcon.getIconWidth();
				int y = (this.getHeight() - iconHeight) / 2;
				int y2 = (this.getHeight() - iconHeight2) / 2;

				icon.paintIcon(this, g, 5, y);
				errorIcon.paintIcon(this, g, this.getWidth() - iconWidth2 - 5,
						y2);

				setBorder(
						new EmptyBorder(3, iconWidth + 20, 0, iconWidth2 + 15));
			} else {
				int iconHeight = errorIcon.getIconHeight();
				int iconWidth = errorIcon.getIconWidth();
				int y = (this.getHeight() - iconHeight) / 2;
				errorIcon.paintIcon(this, g, this.getWidth() - iconWidth - 5,
						y);
				setBorder(new EmptyBorder(3, 10, 0, iconWidth + 15));
			}
		} else if (icon != null) {
			int iconHeight = icon.getIconHeight();
			int iconWidth = icon.getIconWidth();
			int y = (this.getHeight() - iconHeight) / 2;
			icon.paintIcon(this, g, 5, y);
			setBorder(new EmptyBorder(3, iconWidth + 20, 0, 2));
		} else {
			setBorder(new EmptyBorder(3, 10, 0, 2));
		}

		if (showingHint) {
			super.setForeground(new Color(170, 170, 170));
		} else {
			super.setForeground(Color.BLACK);
		}
	}
}