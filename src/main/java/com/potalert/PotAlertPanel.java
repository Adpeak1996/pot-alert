package com.potalert;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.Scrollable;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.Filepath;

class PotAlertPanel extends PluginPanel
{
	private static final String SETTINGS_SOUND = "Settings sound";
	private static final String OPEN = "<html><body style='width: 140px'>";
	private static final String CLOSE = "</body></html>";

	private static final String INTRO = OPEN
		+ "<font size='+1' color='#ff981f'><b>Pot Alert</b></font><br>"
		+ "Your screen glows in the pot's color with a sound when it's time to drink.<br><br>"
		+ "<font color='#ff981f'><b>How it works</b></font><br>"
		+ "&bull; Warns once, then goes quiet<br>"
		+ "&bull; Ready again once you drink<br>"
		+ "&bull; Only pots in your inventory<br>"
		+ "&bull; Only boosts you've actually drunk"
		+ CLOSE;

	private static final String TEST_HELP = OPEN
		+ "<font color='#ff981f'><b>Test</b></font><br>"
		+ "Pick a pot to see and hear its warning."
		+ CLOSE;

	private static final String CUSTOM_HELP = OPEN
		+ "<font color='#ff981f'><b>Custom sounds</b></font><br>"
		+ "Click <i>Add</i> to pick .wav files, then choose one for any pot."
		+ CLOSE;

	private static final String REFERENCE = OPEN
		+ "<font color='#ff981f'><b>When it warns</b></font><br>"
		+ "<b>Boosts:</b> at your <i>Resip at level</i><br>"
		+ "<b>Timed pots:</b> before they run out<br>"
		+ "<b>Prayer:</b> at your <i>Alert at prayer</i><br>"
		+ "<b>Absorption:</b> at your <i>Alert at absorption</i>, once you've drunk above it<br>"
		+ "<b>Anti-venom, antifire:</b> before they run out, and again when they do<br><br>"
		+ "<font color='#ff981f'><b>Tips</b></font><br>"
		+ "&bull; Want repeats? Set <i>Remind again every</i>. 0 is one warning only<br>"
		+ "&bull; Sound <i>Off</i> is glow only for that pot. <i>Volume</i> 0 mutes everything<br>"
		+ "&bull; <i>Brighten dark potions</i> (on by default) makes dark pots easier to see<br>"
		+ "&bull; <i>Highlight pot in inventory</i> (on by default) outlines the pot you need. <i>Outline thickness</i> makes it thinner or bolder<br>"
		+ "&bull; <i>Match my weapon</i> (off by default) only warns for the style you're using<br>"
		+ "&bull; Skilling pots still warn with <i>Only in combat</i> on<br>"
		+ "&bull; Raid pots count as their normal versions"
		+ CLOSE;

	private final PotAlertPlugin plugin;
	private final ConfigManager configManager;
	private final Map<AlertType, JComboBox<String>> pickers = new EnumMap<>(AlertType.class);
	private boolean loading;

	PotAlertPanel(PotAlertPlugin plugin, ConfigManager configManager)
	{
		super(false);
		this.plugin = plugin;
		this.configManager = configManager;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel content = new WidthTrackingPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		content.setBackground(ColorScheme.DARK_GRAY_COLOR);

		content.add(left(new JLabel(INTRO)));
		content.add(gap());
		content.add(left(new JLabel(TEST_HELP)));
		content.add(gap());
		JPanel testRow = new JPanel(new BorderLayout(4, 0));
		testRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JComboBox<String> testPicker = new JComboBox<>();
		testPicker.setFocusable(false);
		for (AlertType type : AlertType.values())
		{
			testPicker.addItem(type.getLabel());
		}
		JButton test = button("Test");
		test.addActionListener(e -> plugin.preview(AlertType.values()[testPicker.getSelectedIndex()]));
		testRow.add(testPicker, BorderLayout.CENTER);
		testRow.add(test, BorderLayout.EAST);
		content.add(left(testRow));
		content.add(gap());
		content.add(gap());
		content.add(left(new JLabel(CUSTOM_HELP)));
		content.add(gap());

		JPanel folderRow = new JPanel(new GridLayout(1, 2, 4, 0));
		folderRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JButton add = button("Add");
		add.addActionListener(e -> addSounds());
		JButton remove = button("Remove");
		remove.addActionListener(e -> removeSound());
		folderRow.add(add);
		folderRow.add(remove);
		content.add(left(folderRow));
		content.add(gap());

		JPanel pickerGrid = new JPanel(new GridLayout(0, 1, 0, 2));
		pickerGrid.setBackground(ColorScheme.DARK_GRAY_COLOR);
		for (AlertType type : AlertType.values())
		{
			JPanel row = new JPanel(new BorderLayout(4, 0));
			row.setBackground(ColorScheme.DARK_GRAY_COLOR);
			JLabel label = new JLabel(type.getLabel());
			label.setPreferredSize(new Dimension(82, 20));
			JComboBox<String> picker = new JComboBox<>();
			picker.setFocusable(false);
			picker.setPrototypeDisplayValue(SETTINGS_SOUND);
			picker.setPreferredSize(new Dimension(100, 22));
			picker.setRenderer(new TruncatingRenderer());
			picker.addActionListener(e -> onPick(type, picker));
			pickers.put(type, picker);
			row.add(label, BorderLayout.WEST);
			row.add(picker, BorderLayout.CENTER);
			pickerGrid.add(row);
		}
		content.add(left(pickerGrid));
		content.add(gap());
		content.add(gap());
		content.add(left(new JLabel(REFERENCE)));
		content.add(gap());

		JButton reset = button("Reset all settings");
		reset.addActionListener(e -> confirmReset());
		content.add(left(reset));

		JScrollPane scroll = new JScrollPane(content);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(scroll, BorderLayout.CENTER);

		loadPickers();
	}

	@Override
	public void onActivate()
	{
		loadPickers();
	}

	void loadPickers()
	{
		loading = true;
		List<String> files = wavFiles();
		for (AlertType type : AlertType.values())
		{
			JComboBox<String> picker = pickers.get(type);
			picker.removeAllItems();
			picker.addItem(SETTINGS_SOUND);
			for (String f : files)
			{
				picker.addItem(f);
			}

			String current = plugin.customFile(type);
			if (current != null && files.contains(current))
			{
				picker.setSelectedItem(current);
			}
			else
			{
				picker.setSelectedItem(SETTINGS_SOUND);
			}
		}
		loading = false;
	}

	private void onPick(AlertType type, JComboBox<String> picker)
	{
		if (loading || picker.getSelectedItem() == null)
		{
			return;
		}

		String choice = (String) picker.getSelectedItem();
		String value = SETTINGS_SOUND.equals(choice) ? "" : choice;
		configManager.setConfiguration(PotAlertConfig.GROUP, type.getKey() + "CustomFile", value);
	}

	private void confirmReset()
	{
		Window window = SwingUtilities.getWindowAncestor(this);
		int result = JOptionPane.showOptionDialog(window,
			"Reset all Pot Alert settings to their defaults? This also clears your custom sound picks.",
			"Reset Pot Alert", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
			null, new String[]{"Reset", "Cancel"}, "Cancel");
		if (result == JOptionPane.YES_OPTION)
		{
			configManager.setDefaultConfiguration(plugin.getConfig(), true);
			loadPickers();
		}
	}

	private void addSounds()
	{
		Filepath dir = plugin.getSoundDir();
		if (dir == null)
		{
			return;
		}

		List<Filepath> picked = new Filepath.Chooser()
			.setIsOpen()
			.setAcceptsFiles()
			.setMultiSelectionEnabled(true)
			.setDialogTitle("Add sounds to Pot Alert")
			.addExtensionFilter("WAV sounds", "wav")
			.showDialog(this);
		if (picked == null)
		{
			return;
		}

		List<String> skipped = new ArrayList<>();
		for (Filepath file : picked)
		{
			if (!file.getFileName().toLowerCase().endsWith(".wav"))
			{
				skipped.add(file.getFileName());
				continue;
			}
			try
			{
				file.copyTo(dir.joinSegment(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
					"Couldn't add " + file.getFileName(), "Pot Alert", JOptionPane.WARNING_MESSAGE);
			}
		}
		if (!skipped.isEmpty())
		{
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
				"Only .wav files can be added. Skipped:\n" + String.join("\n", skipped),
				"Pot Alert", JOptionPane.INFORMATION_MESSAGE);
		}
		loadPickers();
	}

	private void removeSound()
	{
		Filepath dir = plugin.getSoundDir();
		List<String> files = wavFiles();
		Window window = SwingUtilities.getWindowAncestor(this);
		if (dir == null || files.isEmpty())
		{
			JOptionPane.showMessageDialog(window, "You haven't added any sounds yet.", "Pot Alert", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JComboBox<String> choice = new JComboBox<>(files.toArray(new String[0]));
		int result = JOptionPane.showConfirmDialog(window, choice, "Remove a sound",
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION || choice.getSelectedItem() == null)
		{
			return;
		}

		String name = (String) choice.getSelectedItem();
		try
		{
			dir.joinSegment(name).deleteIfExists();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(window, "Couldn't remove " + name, "Pot Alert", JOptionPane.WARNING_MESSAGE);
			return;
		}

		for (AlertType type : AlertType.values())
		{
			if (name.equals(plugin.customFile(type)))
			{
				configManager.setConfiguration(PotAlertConfig.GROUP, type.getKey() + "CustomFile", "");
			}
		}
		loadPickers();
	}

	private List<String> wavFiles()
	{
		List<String> names = new ArrayList<>();
		Filepath dir = plugin.getSoundDir();
		if (dir == null || !dir.exists())
		{
			return names;
		}
		try (Stream<Filepath> files = dir.walk(1))
		{
			files.filter(Filepath::isFile)
				.map(Filepath::getFileName)
				.filter(name -> name.toLowerCase().endsWith(".wav"))
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.forEach(names::add);
		}
		catch (Exception e)
		{
			return names;
		}
		return names;
	}

	private static JButton button(String text)
	{
		JButton b = new JButton(text);
		b.setFocusable(false);
		b.setMargin(new Insets(2, 2, 2, 2));
		b.setToolTipText(text);
		return b;
	}

	private static class WidthTrackingPanel extends JPanel implements Scrollable
	{
		@Override
		public Dimension getPreferredScrollableViewportSize()
		{
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction)
		{
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction)
		{
			return visible.height;
		}

		@Override
		public boolean getScrollableTracksViewportWidth()
		{
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight()
		{
			return false;
		}
	}

	private static class TruncatingRenderer extends DefaultListCellRenderer
	{
		private static final int MAX = 14;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus)
		{
			String text = value == null ? "" : value.toString();
			String shown = text.length() > MAX ? text.substring(0, MAX - 1) + "\u2026" : text;
			JLabel label = (JLabel) super.getListCellRendererComponent(list, shown, index, selected, focus);
			label.setToolTipText(text);
			return label;
		}
	}

	private static Component gap()
	{
		JPanel gap = new JPanel();
		gap.setBackground(ColorScheme.DARK_GRAY_COLOR);
		gap.setPreferredSize(new Dimension(0, 10));
		gap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		return gap;
	}

	private static <T extends javax.swing.JComponent> T left(T c)
	{
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		return c;
	}
}
