package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ErrorsLog {

	private static ErrorsLog	instance;

	private File				file;
	private BufferedWriter		writer;

	private ErrorsLog() {
		try {

			File directory = new File("data/errors");
			if (!directory.exists()) {
				directory.mkdir();
			}

			this.file = new File("data/errors/"
					+ new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()) + ".txt");
			if (!this.file.exists()) {
				this.file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeLog(final String classname, final String method, final String details) {
		try {
			this.writer = new BufferedWriter(new FileWriter(file, true));
			this.writer.write(
					new SimpleDateFormat("[HH:mm:ss]").format(Calendar.getInstance().getTime()) + " -->\tClase:\t\t"
							+ classname + "\n\t\t\t\tFunción:\t" + method + "\n\t\t\t\tDetalles:\t" + details + "\n\n");
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ErrorsLog getInstance() {
		if (instance == null) {
			instance = new ErrorsLog();
		}
		return instance;
	}
}