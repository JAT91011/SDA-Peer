package utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Properties implements Serializable {

	private static final long	serialVersionUID	= -7184695896737258947L;

	private static Properties	properties;

	private String				ip;
	private int					portTracker;
	private String				contents;

	private Properties(final String ip, final int portTracker, final String contents) {

		this.ip = ip;
		this.portTracker = portTracker;
		this.contents = contents;
	}

	private void update() {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream("data/config.properties"));
			oos.writeObject(properties);
			oos.close();
		} catch (final IOException e) {
			e.printStackTrace();
			properties = new Properties("", 0, "downloads");
		}
	}

	private static void init() {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream("data/config.properties"));
			properties = (Properties) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			if (!(e instanceof FileNotFoundException)) {
				e.printStackTrace();
			}
			properties = new Properties("", 0, "downloads");
			properties.update();
		}
	}

	public static String getIp() {
		if (properties == null) {
			init();
		}
		return properties.ip;
	}

	public static void setIp(final String ip) {
		if (properties == null) {
			init();
		}
		properties.ip = ip;
		properties.update();
	}

	public static int getPortTracker() {
		if (properties == null) {
			init();
		}
		return properties.portTracker;
	}

	public static void setPortTracker(final int portTracker) {
		if (properties == null) {
			init();
		}
		properties.portTracker = portTracker;
		properties.update();
	}

	public static String getContentsPath() {
		if (properties == null) {
			init();
		}
		return properties.contents;
	}

	public static void setContentsPath(final String contents) {
		if (properties == null) {
			init();
		}
		properties.contents = contents;
		properties.update();
	}
}