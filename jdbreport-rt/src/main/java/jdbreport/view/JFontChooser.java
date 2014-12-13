/*
 * Created 06/24/06
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jdbreport.view;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.TextAttribute;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.6 13.03.2011
 * @author Andrey Kholmanskih
 * 
 */
public class JFontChooser extends JPanel {

	private static final long serialVersionUID = 1L;
	public static int OK = 0;
	public static int CANCEL = 1;

	private static int modalResult = CANCEL;

	private JList familyList;

	private JCheckBox italicBox;

	private JCheckBox boldBox;

	private JList sizeList;

	private JFormattedTextField sizeField;

	private JTextPane sampleText;

	private Font fontValue;

	private JCheckBox underlineBox;

	private JCheckBox strikeBox;

	private JColorBox colorBox;

	public JFontChooser() {
		this(UIManager.getFont("Button.font")); //$NON-NLS-1$
	}

	public JFontChooser(Font initialFont) {
		initComponents();
		initListeners();
		setFontValue(initialFont);
	}

	public JFontChooser(Font initialFont, Color color) {
		initComponents();
		initListeners();
		setFontValue(initialFont, color);
	}

	public void setFont(Font font) {
		super.setFont(font);
		if (familyList != null) {
			familyList.setFont(font);
			sizeList.setFont(font);
			italicBox.setFont(font);
			italicBox.setFont(font);
			boldBox.setFont(font);
			underlineBox.setFont(font);
			strikeBox.setFont(font);
		}
	}

	public void setFontValue(Font font, Color color) {
		this.setFontValue(font);
		this.setFontColor(color);
	}

	protected void initComponents() {
		this.setLayout(new BorderLayout());
		familyList = new JList(GraphicsEnvironment
				.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		familyList.setFont(getFont());
		familyList.setSelectedValue(UIManager.getFont("Label.font") //$NON-NLS-1$
				.getFamily(), true);

		sizeList = new JList(
				new String[] {
						"6",
						"7",
						"8",
						"9",
						"10",
						"11",
						"12",
						"14",
						"16",
						"18", "20", "22", "24", "26", "28", "32", "36", "42", "72", Messages.getString("JFontChooser.20") }); //$NON-NLS-10$
		sizeList.setFont(getFont());
		sizeList.setSelectedValue(new Integer(UIManager.getFont("Label.font") //$NON-NLS-1$
				.getSize()).toString(), true);
		sizeField = new JFormattedTextField();
		sizeField.setFont(getFont());
		sizeField.setValue(new Integer(12));

		italicBox = new JCheckBox(Messages.getString("JFontChooser.21"), false); //$NON-NLS-1$
		italicBox.setFont(getFont());
		boldBox = new JCheckBox(Messages.getString("JFontChooser.22"), false); //$NON-NLS-1$
		boldBox.setFont(getFont());
		underlineBox = new JCheckBox(
				Messages.getString("JFontChooser.23"), false); //$NON-NLS-1$
		underlineBox.setFont(getFont());
		strikeBox = new JCheckBox(Messages.getString("JFontChooser.24"), false); //$NON-NLS-1$
		strikeBox.setFont(getFont());

		JPanel fontBox = new JPanel();
		fontBox.setFont(getFont());
		fontBox.setLayout(new BorderLayout());
		JScrollPane scrollpane = new JScrollPane(familyList);
		scrollpane.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("JFontChooser.25"))); //$NON-NLS-1$
		fontBox.add(scrollpane, BorderLayout.WEST);

		JPanel effectsBox = new JPanel();
		effectsBox.setLayout(new BoxLayout(effectsBox, BoxLayout.Y_AXIS));
		effectsBox.add(italicBox);
		effectsBox.add(boldBox);
		effectsBox.add(underlineBox);
		effectsBox.add(strikeBox);
		effectsBox.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("JFontChooser.26"))); //$NON-NLS-1$
		fontBox.add(effectsBox, BorderLayout.CENTER);

		JPanel sizePanel = new JPanel();
		sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.Y_AXIS));
		sizePanel.add(sizeField);
		sizePanel.add(new JScrollPane(sizeList));
		sizePanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("JFontChooser.27"))); //$NON-NLS-1$
		fontBox.add(sizePanel, BorderLayout.EAST);
		add(fontBox, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorBox = new JColorBox(false);
		colorBox.setColor(Color.BLACK);
		colorPanel.add(colorBox, BorderLayout.NORTH);
		colorPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("JFontChooser.28"))); //$NON-NLS-1$
		centerPanel.add(colorPanel, BorderLayout.WEST);
		add(centerPanel, BorderLayout.CENTER);

		sampleText = new JTextPane();
		sampleText.setText(Messages.getString("JFontChooser.29")); //$NON-NLS-1$
		sampleText.setPreferredSize(new Dimension(100, 40));
		JPanel samplePanel = new JPanel(new BorderLayout());
		samplePanel.add(new JScrollPane(sampleText), BorderLayout.CENTER);
		samplePanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("JFontChooser.30"))); //$NON-NLS-1$
		add(samplePanel, BorderLayout.SOUTH);
	}

	protected void initListeners() {
		familyList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				updateFont();
				firePropertyChange("family", null, familyList
						.getSelectedValue());
			}
		});

		sizeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				Integer value = new Integer((String) sizeList
						.getSelectedValue());
				if (!sizeField.getValue().equals(value)) {
					sizeField.setValue(value);
					updateFont();
					firePropertyChange("size", null, new Float(sizeField
							.getValue().toString()));
				}
			}
		});

		sizeField.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				if (sizeField.isEditValid())
					try {
						sizeField.commitEdit();
						updateFont();
						firePropertyChange("size", null, new Float(sizeField
								.getValue().toString()));
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
			}

		});

		boldBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFont();
				firePropertyChange("bold", null, new Boolean(boldBox
						.isSelected()));
			}
		});

		italicBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFont();
				firePropertyChange("italic", null, new Boolean(italicBox
						.isSelected()));
			}
		});

		underlineBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFont();
				firePropertyChange("underline", null, new Boolean(underlineBox
						.isSelected()));
			}
		});

		strikeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFont();
				firePropertyChange("strike", null, new Boolean(strikeBox
						.isSelected()));
			}
		});

		colorBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFont();
				firePropertyChange("color", null, colorBox.getColor());
			}
		});
	}

	public void assignStyleAttributes(Document doc) {
		if (!(doc instanceof StyledDocument))
			return;
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attr, (String) familyList
				.getSelectedValue());
		Integer value = (Integer) sizeField.getValue();
		StyleConstants.setFontSize(attr, value.intValue());
		StyleConstants.setBold(attr, isBold());
		StyleConstants.setItalic(attr, isItalic());
		StyleConstants.setUnderline(attr, isUnderline());
		StyleConstants.setStrikeThrough(attr, isStrikethrough());
		StyleConstants.setForeground(attr, getFontColor());
		((StyledDocument) doc).setParagraphAttributes(0, doc.getLength(), attr,
				true);
	}

	public Color getFontColor() {
		return colorBox.getColor();
	}

	public void setFontColor(Color value) {
		if (value != null)
			colorBox.setColor(value);
		else
			colorBox.setSelectedIndex(-1);
	}

	public boolean isBold() {
		return boldBox.isSelected();
	}

	public void setBold(boolean value) {
		boldBox.setSelected(value);
		updateFont();
	}

	public boolean isItalic() {
		return italicBox.isSelected();
	}

	public void setItalic(boolean value) {
		italicBox.setSelected(value);
		updateFont();
	}

	public boolean isUnderline() {
		return underlineBox.isSelected();
	}

	public void setUnderline(boolean value) {
		underlineBox.setSelected(value);
		updateFont();
	}

	public boolean isStrikethrough() {
		return strikeBox.isSelected();
	}

	public void setStrikethrough(boolean value) {
		strikeBox.setSelected(value);
		updateFont();
	}

	protected void updateFont() {
		if (familyList.getSelectedValue() == null
				|| sizeField.getValue() == null || colorBox.getColor() == null) {
			fontValue = null;
			return;
		}
		assignStyleAttributes(sampleText.getDocument());
		Map<TextAttribute, Object> fontAttrs = new HashMap<TextAttribute, Object>();
		fontAttrs.put(TextAttribute.FAMILY, familyList
				.getSelectedValue());
		fontAttrs.put(TextAttribute.SIZE, new Float(sizeField.getValue()
				.toString()));

		if (boldBox.isSelected())
			fontAttrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		else
			fontAttrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);

		if (italicBox.isSelected())
			fontAttrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		else
			fontAttrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);

		Font newFont = new Font(fontAttrs);
		Font oldFont = fontValue;
		fontValue = newFont;
		sampleText.repaint(100);
		if (oldFont == null || !oldFont.equals(newFont)) {
			firePropertyChange("fontValue", oldFont, newFont); //$NON-NLS-1$
		}
	}

	public void setFontFamily(String fontFamily) {
		if (fontFamily != null)
			familyList.setSelectedValue(fontFamily, true);
		else {
			familyList.setSelectedIndex(-1);
		}
		updateFont();
	}

	public void setFontSize(int fontSize) {
		if (fontSize > 0)
			sizeList.setSelectedValue(Integer.toString(fontSize), true);
		else
			sizeList.setSelectedIndex(-1);
		updateFont();
	}

	public void setFontValue(Font newfontValue) {
		boldBox.setSelected(newfontValue != null && newfontValue.isBold());
		italicBox.setSelected(newfontValue != null && newfontValue.isItalic());
		if (newfontValue == null) {
			familyList.setSelectedIndex(-1);
			sizeList.setSelectedIndex(-1);
		} else {
			familyList.setSelectedValue(newfontValue.getName(), true);
			String s = Integer.toString(newfontValue.getSize());
			sizeList.setSelectedValue(s, true);
		}
		this.fontValue = newfontValue;
		if (newfontValue != null)
			sampleText.setFont(newfontValue);

	}

	public void setFontValue(MutableAttributeSet attr) {
		familyList.setSelectedValue(StyleConstants.getFontFamily(attr), true);
		sizeList.setSelectedValue(
				new Integer(StyleConstants.getFontSize(attr)), true);
		boldBox.setSelected(StyleConstants.isBold(attr));
		italicBox.setSelected(StyleConstants.isItalic(attr));
		underlineBox.setSelected(StyleConstants.isUnderline(attr));
		strikeBox.setSelected(StyleConstants.isStrikeThrough(attr));
		colorBox.setColor(StyleConstants.getForeground(attr));
		Document doc = sampleText.getDocument();
		((StyledDocument) doc).setParagraphAttributes(0, doc.getLength(), attr,
				true);
	}

	public Font getFontValue() {
		return fontValue;
	}

	public static Font showDialog(Component parent, String title,
			Font initialfont) {
		return showDialog(parent, title, initialfont, Color.BLACK).getFontValue();
	}

	public static JFontChooser showDialog(Component parent, String title,
			Font initialfont, Color initialColor) {

		Window windowParent;
		if (parent instanceof Window)
			windowParent = (Window) parent;
		else
			windowParent = SwingUtilities.getWindowAncestor(parent);
		if (windowParent == null)
			throw new IllegalArgumentException(Messages
					.getString("JFontChooser.32")); //$NON-NLS-1$
		final JDialog dialog;
		if (windowParent instanceof Frame)
			dialog = new JDialog((Frame) windowParent, title, true);
		else
			dialog = new JDialog((Dialog) windowParent, title, true);
		dialog.getContentPane().setLayout(new BorderLayout());

		final JFontChooser fontChooser = new JFontChooser(initialfont, initialColor);
		dialog.getContentPane().add(fontChooser, BorderLayout.CENTER);

		JButton okBtn = new JButton(Messages.getString("JFontChooser.33")); //$NON-NLS-1$
		JButton cancelBtn = new JButton(Messages.getString("JFontChooser.34")); //$NON-NLS-1$
		JPanel buttonsBox = new JPanel();
		buttonsBox.setLayout(new BoxLayout(buttonsBox, BoxLayout.X_AXIS));
		buttonsBox.add(Box.createHorizontalGlue());
		buttonsBox.add(okBtn);
		buttonsBox.add(Box.createHorizontalStrut(30));
		buttonsBox.add(cancelBtn);
		buttonsBox.add(Box.createHorizontalGlue());
		dialog.getContentPane().add(buttonsBox, BorderLayout.SOUTH);
		dialog.pack();
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				modalResult = OK;
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				modalResult = CANCEL;
			}
		});

		screenCenter(dialog, windowParent);
		dialog.setVisible(true);

		return fontChooser;
	}

	public int getModalResult() {
		return modalResult;
	}
	
	public static void screenCenter(Window window, Window parent) {
		Dimension screenSize = parent.getSize();
		Point location = parent.getLocation();
		Dimension frameSize = window.getSize();
		window.setLocation(location.x + (screenSize.width - frameSize.width)
				/ 2, location.y + (screenSize.height - frameSize.height) / 2);
	}


}