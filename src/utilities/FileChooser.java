package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser {

	/**
	 * Opens a file with a file chooser
	 * 
	 * @param description
	 *            - The description of the file
	 * @param extensions
	 *            - The extension of the file. Could be multiple extensions.
	 * @return The file loaded
	 */
	public static File openFile(final String description, final String... extensions) {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(description, extensions));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		String path = "";
		File file = null;
		try {
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				path = fileChooser.getSelectedFile().getAbsolutePath();
				file = new File(path);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * Saves a file choosen by a file chooser
	 * 
	 * @param content
	 *            - The object to save
	 * @param description
	 *            - The description of the file
	 * @param extension
	 *            - The extension of the file
	 * @param file
	 *            - The file in where to save
	 * @return The path of the saved file
	 */
	public static String saveObjectFile(final Object content, final String description, final String extension,
			final File file) {
		final JFileChooser fileChooser = new JFileChooser();
		final FileNameExtensionFilter langFilter = new FileNameExtensionFilter(description, extension);
		fileChooser.setFileFilter(langFilter);
		fileChooser.setSelectedFile(file);
		String path = "";
		try {
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				path = fileChooser.getSelectedFile().getAbsolutePath();
				if (!path.endsWith("." + extension)) {
					path += "." + extension;
				}
				final File file2 = new File(path);
				if ((file2.exists() && JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null,
						"The file exists, do you want to replace it?", "File Exists", JOptionPane.YES_NO_OPTION))
						|| !file2.exists()) {
					final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
					oos.writeObject(content);
					oos.close();
				}
			}
			return path;
		} catch (final Exception e) {
			e.printStackTrace();
			return path;
		}
	}
}