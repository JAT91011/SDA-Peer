package utilidades;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Propiedades implements Serializable {

	private static final long serialVersionUID = -7184695896737258947L;

	private static Propiedades propiedades;

	private String	ip;
	private int		port;
	private String	downloadsPath;
	private Locale	locale;
	private String	lookAndFeelClass;

	private Propiedades(final String ip, final int port, final String downloadsPath, Locale locale,
			String lookAndFeelClass) {

		this.ip = ip;
		this.port = port;
		this.downloadsPath = downloadsPath;
		this.locale = locale;
		this.lookAndFeelClass = lookAndFeelClass;
	}

	private void update() {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream("data/config.properties"));
			oos.writeObject(propiedades);
			oos.close();
		} catch (final IOException e) {
			e.printStackTrace();
			propiedades = new Propiedades("", 0, "", Locale.getDefault(), UIManager.getSystemLookAndFeelClassName());
		}
	}

	private static void init() {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream("data/config.properties"));
			propiedades = (Propiedades) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			if (!(e instanceof FileNotFoundException)) {
				e.printStackTrace();
			}
			propiedades = new Propiedades("", 0, "", Locale.getDefault(), UIManager.getSystemLookAndFeelClassName());
			propiedades.update();
		}
	}

	public static String getIP() {
		if (propiedades == null) {
			init();
		}
		return propiedades.ip;
	}

	public static void setIP(final String ip) {
		if (propiedades == null) {
			init();
		}
		propiedades.ip = ip;
		propiedades.update();
	}

	public static int getPort() {
		if (propiedades == null) {
			init();
		}
		return propiedades.port;
	}

	public static void setPort(final int port) {
		if (propiedades == null) {
			init();
		}
		propiedades.port = port;
		propiedades.update();
	}

	public static String getDownloadsPath() {
		if (propiedades == null) {
			init();
		}
		return propiedades.downloadsPath;
	}

	public static void setDownloadsPath(final String downloadsPath) {
		if (propiedades == null) {
			init();
		}
		propiedades.downloadsPath = downloadsPath;
		propiedades.update();
	}

	public static Locale getLocale() {
		if (propiedades == null) {
			init();
		}
		return propiedades.locale;
	}

	public static void setLocale(final Locale locale) {
		if (propiedades == null) {
			init();
		}
		if (Lang.getAvailableLocales().contains(locale)) {
			propiedades.locale = locale;
		} else {
			propiedades.locale = Lang.getDefaultLocale();
		}
		propiedades.update();
	}

	public static String getLookAndFeel() {
		if (propiedades == null) {
			init();
		}
		return propiedades.lookAndFeelClass;
	}

	public static void setLookAndFeelClass(final String lookAndFeelClass) {
		if (propiedades == null) {
			init();
		}
		if (isLFAvailable(lookAndFeelClass)) {
			propiedades.lookAndFeelClass = lookAndFeelClass;
		}
		propiedades.update();
	}

	private static boolean isLFAvailable(final String lf) {
		final LookAndFeelInfo lfs[] = UIManager.getInstalledLookAndFeels();
		for (final LookAndFeelInfo lf2 : lfs) {
			if (lf2.getClassName().equals(lf)) {
				return true;
			}
		}
		return false;
	}
}
