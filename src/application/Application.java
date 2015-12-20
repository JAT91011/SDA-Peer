package application;

import java.awt.EventQueue;

import javax.swing.UIManager;

import utilities.ErrorsLog;
import views.StartPanel;
import views.Window;

public class Application {

	public static void main(final String[] args) {
		System.out.println("PEER START");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					StartPanel panel = new StartPanel();
					Window.getInstance().setContainer(new StartPanel());
					Window.getInstance().setVisible(true);
				} catch (Exception e) {
					ErrorsLog.getInstance().writeLog(this.getClass().getName(), new Object() {
					}.getClass().getEnclosingMethod().getName(), e.toString());
					e.printStackTrace();
				}
			}
		});
	}
}