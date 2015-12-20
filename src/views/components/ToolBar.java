package views.components;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

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
		btnAdd.addActionListener(this);

		btnRemove = new JButton(new ImageIcon("icons/remove-icon.png"));
		btnRemove.setFocusable(false);
		btnRemove.setToolTipText("Remove torrent");
		btnRemove.addActionListener(this);

		btnPlay = new JButton(new ImageIcon("icons/play-icon.png"));
		btnPlay.setFocusable(false);
		btnPlay.setToolTipText("Start");
		btnPlay.addActionListener(this);

		btnPause = new JButton(new ImageIcon("icons/pause-icon.png"));
		btnPause.setFocusable(false);
		btnPause.setToolTipText("Pause");
		btnPause.addActionListener(this);

		btnStop = new JButton(new ImageIcon("icons/stop-icon.png"));
		btnStop.setFocusable(false);
		btnStop.setToolTipText("Stop");
		btnStop.addActionListener(this);

		lblFilter = new JLabel(" Filter  ");
		lblFilter.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));

		textField_Find = new JTextField();
		textField_Find.setColumns(10);
		textField_Find.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyTyped(final java.awt.event.KeyEvent e) {
				// final Start startPanel = (Start)
				// Window.getInstance().getContentPane();
				// final JTabbedPane tabs = startPanel.getTabbedPane();
				//
				// final int index = tabs.getSelectedIndex();
				// if (index >= 0) {
				// final LangEditor langEditor =
				// startPanel.getLangEditors().get(index);
				// final JTable table = langEditor.getTable();
				//
				// final TableRowSorter<TableModel> sorter = new
				// TableRowSorter<TableModel>(
				// (TableModel) table.getModel());
				// table.setRowSorter(sorter);
				// final String word = textField_Find.getText().trim();
				// if (word.length() == 0) {
				// sorter.setRowFilter(null);
				// } else {
				// try {
				// sorter.setRowFilter(RowFilter.regexFilter(word));
				// } catch (final PatternSyntaxException pse) {
				// System.err.println("Bad regex pattern");
				// }
				// }
				// }
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
		add(btnPlay);
		add(btnPause);
		add(btnStop);
		addSeparator();
		addSeparator();
		add(lblFilter);
		add(textField_Find);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		// final Start startPanel = (Start)
		// Window.getInstance().getContentPane();
		// final JTabbedPane tPane = startPanel.getTabbedPane();
		// final Vector<LangEditor> langEditors = startPanel.getLangEditors();
		//
		// if (e.getSource() == btnNewFile) {
		// newLangAction(tPane, langEditors);
		// } else if (e.getSource() == btnOpenFile) {
		// openAction(tPane, langEditors);
		// } else if (e.getSource() == btnSaveFile) {
		// saveAction(tPane, langEditors);
		// } else if (e.getSource() == btnSaveAsFile) {
		// saveAsAction(tPane, langEditors);
		// } else if (e.getSource() == btnPrint) {
		// printAction(tPane, startPanel);
		// } else if (e.getSource() == btnAddRow) {
		// addRowAction(tPane);
		// } else if (e.getSource() == btnRemoveRow) {
		// removeRowAction(tPane);
		// }
	}
}