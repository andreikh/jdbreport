/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 * 
 */
package jdbreport.grid;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;

import javax.swing.JDialog;

import jdbreport.model.Border;
import jdbreport.model.CellStyle;
import javax.swing.JComboBox;

import and.actions.ToggleAction;
import and.swing.JColorBox;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

/**
 * @version 2.0 03.05.2011
 * @author Andrey Kholmanskih
 * 
 */
public class BorderDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static Dimension boxSize = new Dimension(60, 16);

	private JPanel jContentPane = null;

	private JToolBar toolBar;

	private ToggleAction[] buttons = new ToggleAction[8];

	private JPanel bottomPanel;

	private JComboBox widthBox = null;

	private JColorBox colorBox = null;

	public BorderDialog(Frame owner) throws HeadlessException {
		super(owner);
		initialize();
	}

	public BorderDialog(Dialog owner) throws HeadlessException {
		super(owner);
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle(Messages.getString("BorderDialog.0")); //$NON-NLS-1$
		this.setResizable(false);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getToolBar(), BorderLayout.CENTER);
			jContentPane.add(getBottomPanel(), BorderLayout.SOUTH);

		}
		return jContentPane;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 10);
			gridBagConstraints1.ipadx = 20;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.weightx = 1.0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.ipadx = 26;
			gridBagConstraints.insets = new Insets(2, 2, 2, 10);
			gridBagConstraints.weightx = 1.0;
			bottomPanel = new JPanel(new GridBagLayout());
			bottomPanel.add(getWidthBox(), gridBagConstraints);
			bottomPanel.add(getColorBox(), gridBagConstraints1);
		}
		return bottomPanel;
	}

	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setFloatable(false);
			toolBar.setRollover(false);
		}
		return toolBar;
	}

	protected JToggleButton createActionComponent(Action a) {
		Icon icon = a != null ? (Icon) a.getValue(Action.SMALL_ICON) : null;
		boolean enabled = a != null ? a.isEnabled() : true;
		String tooltip = a != null ? (String) a
				.getValue(Action.SHORT_DESCRIPTION) : null;
		JToggleButton b = new JToggleButton("", icon); //$NON-NLS-1$
		b.putClientProperty("hideActionText", Boolean.TRUE); //$NON-NLS-1$
		b.setEnabled(enabled);
		b.setToolTipText(tooltip);
		b.setAction(a);
		return b;
	}

	public void add(Action action, int pos) {
		if (pos >= 0 && pos < buttons.length && action instanceof ToggleAction) {
			JToggleButton b = (JToggleButton) ((ToggleAction) action)
					.addButton(new JToggleButton());
			b.setText("");
			getToolBar().add(b);
			buttons[pos] = (ToggleAction) action;
		} else
			getToolBar().add(action);
	}

	public void updateActions(CellStyle style) {
		for (int pos = Border.LINE_LEFT; pos <= Border.LINE_BOTTOM; pos++) {
			buttons[pos].setSelected(style.getBorders(pos) != null);
			ReportAction.ToggleBorderAction a = (ReportAction.ToggleBorderAction) buttons[pos];
			a.setSelected(buttons[pos].isSelected());
		}
		buttons[Border.LINE_BORDER].setSelected(buttons[Border.LINE_LEFT]
				.isSelected()
				&& buttons[Border.LINE_RIGHT].isSelected()
				&& buttons[Border.LINE_TOP].isSelected()
				&& buttons[Border.LINE_BOTTOM].isSelected());
	}

	/**
	 * This method initializes widthBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getWidthBox() {
		if (widthBox == null) {
			widthBox = new JComboBox();
			widthBox.setToolTipText(Messages.getString("BorderDialog.3")); //$NON-NLS-1$
			widthBox.addItem(new Border(Color.black, 0.5f));
			widthBox.addItem(new Border(Color.black, 1.0f));
			widthBox.addItem(new Border(Color.black, 2.0f));
			widthBox.addItem(new Border(Color.black, 3.0f));
			widthBox.addItem(new Border(Color.black, 0.5f, Border.psDash));
			widthBox.addItem(new Border(Color.black, 0.5f, Border.psDot));
			widthBox.addItem(new Border(Color.black, 0.5f, Border.psDashDot));
			widthBox.addItem(new Border(Color.black, 0.5f, Border.psDashDotDot));
			widthBox.addItem(new Border(Color.black, 3.0f, Border.psDouble));
			widthBox.setMaximumSize(boxSize);
			widthBox.setPreferredSize(boxSize);
			widthBox.setSelectedIndex(0);
			widthBox.setFont(getFont().deriveFont(Font.PLAIN, 11f));
			widthBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					Border.defaultBorder = new Border(colorBox.getColor(),
							getBorderWidth(),  getBorderStyle());
				}

			});

			widthBox.setRenderer(new LineCellRenderer());

		}
		return widthBox;
	}

	protected float getBorderWidth() {
		return ((Border)widthBox.getSelectedItem()).getLineWidth();
	}

	protected int getBorderStyle() {
		return ((Border)widthBox.getSelectedItem()).getStyle();
	}
	/**
	 * This method initializes colorBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JColorBox getColorBox() {
		if (colorBox == null) {
			colorBox = new JColorBox(false);
			colorBox.setColor(Color.BLACK);
			colorBox.setToolTipText(Messages.getString("BorderDialog.4")); //$NON-NLS-1$
			colorBox.setFont(new Font("Dialog", Font.PLAIN, 8)); //$NON-NLS-1$
			colorBox.setMaximumSize(boxSize);
			colorBox.setPreferredSize(boxSize);
			colorBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					Border.defaultBorder = new Border(colorBox.getColor(),
							getBorderWidth(), getBorderStyle());
				}

			});
		}
		return colorBox;
	}


	private class LineCellRenderer extends JComponent implements
			ListCellRenderer {

		private static final long serialVersionUID = 1L;

		Border border;
		
		public LineCellRenderer() {
			setOpaque(true);
			setPreferredSize(new Dimension(widthBox.getWidth() - 20, 20));
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			border = (Border)value;
			setBackground(isSelected ? Color.BLUE.darker() : Color.white);
			setForeground(isSelected ? Color.white : Color.black);
			return this;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
            int w = getWidth();
            int h = getHeight();
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(getBackground());
			g2.fillRect(0, 0, w, h);
			g2.setColor(getForeground());
            g2.setStroke(border.getStroke());
            float x2 = w - 20;
            float y2 = h / 2;
            g2.draw(new Line2D.Float(4, h / 2, x2 - 2, y2));
			g2.drawString("" + border.getLineWidth(), x2, y2 + 4);
		}
		
		
	}

}
