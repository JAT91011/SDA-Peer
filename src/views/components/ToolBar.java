package views.components;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.PatternSyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import bitTorrent.metainfo.handler.MetainfoHandler;
import bitTorrent.metainfo.handler.MetainfoHandlerMultipleFile;
import bitTorrent.metainfo.handler.MetainfoHandlerSingleFile;
import utilities.FileChooser;
import views.StartPanel;
import views.Window;

public class ToolBar extends JToolBar implements ActionListener {

	private static final long	serialVersionUID	= -4146568246110544527L;
	private final JButton		btnAdd;
	private final JButton		btnRemove;
	private final JButton		btnPlay;
	private final JButton		btnPause;
	private final JButton		btnStop;
	private final JLabel		lblFilter;
	private final JTextField	textField_Find;

	public ToolBar() {
		super();
		setMargin(new Insets(0, 5, 0, 5));

		btnAdd = new JButton(new ImageIcon("icons/add-icon.png"));
		btnAdd.setFocusable(false);
		btnAdd.setToolTipText("Add torrent");
		btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnAdd.setFocusPainted(false);
		btnAdd.addActionListener(this);

		btnRemove = new JButton(new ImageIcon("icons/remove-icon.png"));
		btnRemove.setFocusable(false);
		btnRemove.setToolTipText("Remove torrent");
		btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnRemove.setFocusPainted(false);
		btnRemove.addActionListener(this);

		btnPlay = new JButton(new ImageIcon("icons/play-icon.png"));
		btnPlay.setFocusable(false);
		btnPlay.setToolTipText("Start");
		btnPlay.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnPlay.setFocusPainted(false);
		btnPlay.addActionListener(this);

		btnPause = new JButton(new ImageIcon("icons/pause-icon.png"));
		btnPause.setFocusable(false);
		btnPause.setToolTipText("Pause");
		btnPause.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnPause.setFocusPainted(false);
		btnPause.addActionListener(this);

		btnStop = new JButton(new ImageIcon("icons/stop-icon.png"));
		btnStop.setFocusable(false);
		btnStop.setToolTipText("Stop");
		btnStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnStop.setFocusPainted(false);
		btnStop.addActionListener(this);

		lblFilter = new JLabel(" Filter  ");
		lblFilter.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));

		textField_Find = new JTextField();
		textField_Find.setColumns(10);
		textField_Find.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyTyped(final java.awt.event.KeyEvent e) {
				final StartPanel startPanel = (StartPanel) Window.getInstance().getContentPane();

				final JTable table = startPanel.getTable();

				final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>((TableModel) table.getModel());
				table.setRowSorter(sorter);
				final String word = textField_Find.getText().trim();
				if (word.length() == 0) {
					sorter.setRowFilter(null);
				} else {
					try {
						sorter.setRowFilter(RowFilter.regexFilter(word));
					} catch (final PatternSyntaxException pse) {
						System.err.println("Bad regex pattern");
					}
				}
			}

			@Override
			public void keyPressed(final java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textField_Find.setText("");
				}
			}
		});

		add(btnAdd);
		add(btnRemove);
		addSeparator();
		add(btnPlay);
		add(btnPause);
		add(btnStop);
		addSeparator();
		add(lblFilter);
		add(textField_Find);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final StartPanel startPanel = (StartPanel) Window.getInstance().getContentPane();

		if (e.getSource() == btnAdd) {
			addTorrent(startPanel);
		} else if (e.getSource() == btnRemove) {
			removeTorrent(startPanel);
		} else if (e.getSource() == btnPlay) {
			play(startPanel);
		} else if (e.getSource() == btnPause) {
			pause(startPanel);
		} else if (e.getSource() == btnStop) {
			stop(startPanel);
		}
	}

	private void addTorrent(StartPanel startPanel) {
		final File file = FileChooser.openFile("Torrent file", "torrent");
		if (file != null) {
			MetainfoHandler<?> handler = null;
			try {
				handler = new MetainfoHandlerSingleFile();
				handler.parseTorrenFile(file.getPath());
			} catch (Exception ex) {
				handler = new MetainfoHandlerMultipleFile();
				handler.parseTorrenFile(file.getPath());
			}

			if (handler != null) {
				System.out.println("#######################################\n" + file.getPath());
				System.out.println(handler.getMetainfo());
				String result = startPanel.addContent(handler.getMetainfo());
				if (!result.isEmpty()) {
					System.out.println(result);
				}
			}
		}
	}

	private void removeTorrent(StartPanel startPanel) {
		// Se comprueba si hay alguna fila seleccionada
		if (startPanel.getTable().getSelectedRowCount() > 0) {
			startPanel.removeSelectedTorrent();
		} else {

		}
	}

	private void play(StartPanel startPanel) {
		// Se arrancan todos los torrents

	}

	private void pause(StartPanel startPanel) {
		// Se comprueba si hay alguna fila seleccionada
		if (startPanel.getTable().getSelectedRowCount() > 0) {
			// Se detiene el torrent
		} else {

		}

	}

	private void stop(StartPanel startPanel) {
		// Se paran todos los torrents

	}
}