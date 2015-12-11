package application;

import java.awt.EventQueue;

import javax.swing.UIManager;

import utilities.ErrorsLog;
import views.StartPanel;
import views.TabsPanel;
import views.Window;

public class Application {

	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					StartPanel panel = new StartPanel();
					Window.getInstance().getSlider().addComponent(panel);
					Window.getInstance().getSlider().addComponent(new TabsPanel());
					Window.getInstance().setVisible(true);
					panel.getNextButton().requestFocus();
				} catch (Exception e) {
					ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
					}.getClass().getEnclosingMethod().getName(), e.toString());
					e.printStackTrace();
				}
			}
		});
	}
}