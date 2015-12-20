package views;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import views.components.ToolBar;

public class StartPanel extends JPanel implements Observer {

	private static final long	serialVersionUID	= 4986034677227823532L;

	private final ToolBar		toolBar;

	public StartPanel() {
		setLayout(new BorderLayout(0, 0));

		toolBar = new ToolBar();
		add(toolBar, BorderLayout.NORTH);

	}

	@Override
	public void update(Observable o, Object arg) {

	}
}