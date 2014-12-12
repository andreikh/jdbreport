/*
 * CellPropertiesDlg.java
 *
 * Created on 30/05/2006 
 * 
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2012 Andrey Kholmanskih. All rights reserved.
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
 */

package jdbreport.grid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jdbreport.grid.undo.CellUndoItem;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.GridRect;
import jdbreport.model.ReportModel;
import jdbreport.util.Utils;
import jdbreport.view.JFontChooser;
import jdbreport.view.JRotateChooser;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class CellPropertiesDlg extends javax.swing.JDialog implements
		PropertyChangeListener, ActionListener, ChangeListener {

	private static final long serialVersionUID = 1L;
	private static final String TITLE = Messages
			.getString("CellPropertiesDlg.title");
	private JRotateChooser rotateChooser;
	private GridRect selectedRect;
	private JReportGrid grid;
	private CellPropertiesPanel cellPropertiesPanel;
	private JSpinner carryField;
	private JCheckBox wrapLineBox;
	private javax.swing.JCheckBox autoHeightBox;
	private javax.swing.JComboBox hAlignBox;
	private javax.swing.JTabbedPane tabbedPane;
	private javax.swing.JComboBox vAlignBox;

	private JFontChooser fontPanel = null;
	private List<Cell> cells = null;
	private JLabel carryLabel;

	private boolean fontFamilyChange;
	private boolean fontSizeChange;
	private boolean fontColorChange;
	private boolean fontBoldChange;
	private boolean fontItalicChange;
	private boolean fontUnderlineChange;
	private boolean fontStrikeChange;

	private boolean hAlignChange;
	private boolean vAlignChange;
	private boolean rotateChange;
	private boolean wrapLineChange;
	private boolean autoHeightChange;
	private boolean carryChange;
	private boolean ok;
	private JSpinner lineSpacingField;
	private boolean lineSpacingChange;

	/** Creates new form CellPropertiesDlg */
	public CellPropertiesDlg(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		addTabs();
	}

	public CellPropertiesDlg(java.awt.Frame owner, JReportGrid grid)
			throws HeadlessException {
		super(owner, true);
		initComponents();
		this.grid = grid;
		this.selectedRect = grid.getSelectionRect();
		cells = new ArrayList<>();
		addTabs();
		setCell(selectedRect);
		Utils.screenCenter(this, owner);
	}

	public CellPropertiesDlg(java.awt.Dialog owner, JReportGrid grid)
			throws HeadlessException {
		super(owner, true);
		initComponents();
		this.grid = grid;
		this.selectedRect = grid.getSelectionRect();
		cells = new ArrayList<>();
		addTabs();
		setCell(selectedRect);
		Utils.screenCenter(this, owner);
	}

	protected ReportModel getModel() {
		return grid.getReportModel();
	}

	private void fillCells() {
		cells.clear();
		if (selectedRect != null) {
			for (int row = selectedRect.getTopRow(); row <= selectedRect
					.getBottomRow(); row++) {
				for (int col = selectedRect.getLeftCol(); col <= selectedRect
						.getRightCol(); col++) {
					Cell cell = getModel().createReportCell(row, col);
					if (!cell.isChild())
						cells.add(cell);
				}
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		setTitle(CellPropertiesDlg.TITLE);
		setBackground(java.awt.Color.white);
		setModal(true);
		setResizable(false);
		
		getContentPane().add(createButtonPanel(), java.awt.BorderLayout.SOUTH);

		tabbedPane = new javax.swing.JTabbedPane();
		tabbedPane
				.addTab(
						Messages.getString("CellPropertiesDlg.font"), null, getFontPanel(), Messages.getString("CellPropertiesDlg.cell_font")); //$NON-NLS-1$ //$NON-NLS-2$

		 
		tabbedPane.addTab(
				Messages.getString("CellPropertiesDlg.align"), createAlignPanel());

		getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

		pack();
	}

	private JPanel createAlignPanel() {
		JPanel alignPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
		
		hAlignBox = new javax.swing.JComboBox();
		hAlignBox.setActionCommand("halign"); //$NON-NLS-1$
		hAlignBox.addActionListener(this);
		
		vAlignBox = new javax.swing.JComboBox();
		vAlignBox.setActionCommand("valign"); //$NON-NLS-1$
		vAlignBox.addActionListener(this);
		

		wrapLineBox = new javax.swing.JCheckBox(Messages
				.getString("CellPropertiesDlg.wrap")); //$NON-NLS-1$
		wrapLineBox.setActionCommand("wrap_line"); //$NON-NLS-1$
		wrapLineBox.addActionListener(this);
		
		autoHeightBox = new javax.swing.JCheckBox();
		autoHeightBox.setActionCommand("auto_height"); //$NON-NLS-1$
		autoHeightBox.addActionListener(this);

		carryField = new JSpinner();
		carryField.setModel(new SpinnerNumberModel(0, 0, null, 1));
		carryField.setToolTipText(Messages
				.getString("CellPropertiesDlg.rowscount")); //$NON-NLS-1$
		carryField.addChangeListener(this);
		
		lineSpacingField = new JSpinner();
		lineSpacingField.setModel(new SpinnerNumberModel(new Float(0), new Float(
				-10), new Float(20), new Float(0.1)));
		lineSpacingField.setToolTipText(Messages.getString("CellPropertiesDlg.line_spacing")); //$NON-NLS-1$
		lineSpacingField.addChangeListener(this);
		
		java.awt.GridBagConstraints gridBagConstraints;
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(12, 20, 4, 4);
		gridBagConstraints.gridwidth = 2;
		alignPanel.add(new JLabel(Messages.getString("CellPropertiesDlg.horz")), gridBagConstraints);

		hAlignBox
				.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] {
								Messages.getString("CellPropertiesDlg.left"), Messages.getString("CellPropertiesDlg.center"), Messages.getString("CellPropertiesDlg.right"), Messages.getString("CellPropertiesDlg.filled") })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		hAlignBox.setToolTipText(Messages
				.getString("CellPropertiesDlg.horz_align"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 20, 4, 6);
		alignPanel.add(hAlignBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(12, 6, 4, 4);
		alignPanel.add(new JLabel(Messages.getString("CellPropertiesDlg.vert")), gridBagConstraints);

		vAlignBox
				.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] {
								Messages.getString("CellPropertiesDlg.top"), Messages.getString("CellPropertiesDlg.vcenter"), Messages.getString("CellPropertiesDlg.bottom") })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		vAlignBox
				.setToolTipText(Messages.getString("CellPropertiesDlg.valign")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.insets = new java.awt.Insets(4, 6, 4, 20);
		alignPanel.add(vAlignBox, gridBagConstraints);

		JPanel rotatePanel = new JPanel(new BorderLayout());
		rotatePanel.setPreferredSize(new Dimension(120, 120));
		
		rotateChooser = new JRotateChooser();
		rotateChooser.addPropertyChangeListener("angle", this); //$NON-NLS-1$
		rotatePanel.add(rotateChooser, BorderLayout.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.weighty = 0.1;
		gridBagConstraints.insets = new java.awt.Insets(8, 18, 8, 8);
		alignPanel.add(rotatePanel, gridBagConstraints);

		wrapLineBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0,
				0, 0));
		wrapLineBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.insets = new java.awt.Insets(12, 20, 4, 4);
		alignPanel.add(wrapLineBox, gridBagConstraints);

		autoHeightBox.setText(Messages
				.getString("CellPropertiesDlg.autoheight")); //$NON-NLS-1$
		autoHeightBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		autoHeightBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.insets = new java.awt.Insets(4, 20, 8, 4);
		alignPanel.add(autoHeightBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 20, 8, 4);
		carryLabel = new JLabel(Messages
				.getString("CellPropertiesDlg.carryrows")); //$NON-NLS-1$
		alignPanel.add(carryLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.insets = new java.awt.Insets(4, 2, 8, 4);
		alignPanel.add(carryField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 20, 24, 4);
		alignPanel.add(new JLabel(Messages.getString("CellPropertiesDlg.line_spacing")), gridBagConstraints);
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.insets = new java.awt.Insets(4, 2, 24, 4);
		alignPanel.add(lineSpacingField, gridBagConstraints);

		return alignPanel;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		JButton okButton = new javax.swing.JButton();
		JButton cancelButton = new javax.swing.JButton();
		okButton.setMnemonic(KeyEvent.VK_ENTER);
		okButton.setText(Messages.getString("CellPropertiesDlg.ok")); //$NON-NLS-1$
		okButton.setActionCommand("ok"); //$NON-NLS-1$
		okButton.addActionListener(this);
		buttonPanel.add(okButton);

		cancelButton.setText(Messages.getString("CellPropertiesDlg.cancel")); //$NON-NLS-1$
		cancelButton.setActionCommand("cancel"); //$NON-NLS-1$
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	protected void addPanel(String label, Component component, String tip) {
		tabbedPane.addTab(label, null, component, tip);
	}

	protected javax.swing.JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	protected void addTabs() {
		addPanel(
				Messages.getString("CellPropertiesDlg.properties"), getCellPropertiesPanel(), Messages.getString("CellPropertiesDlg.tool_tip")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private CellPropertiesPanel getCellPropertiesPanel() {
		if (cellPropertiesPanel == null) {
			cellPropertiesPanel = new CellPropertiesPanel();
		}
		return cellPropertiesPanel;
	}

	private void cancelButtonActionPerformed() {
		setVisible(false);
	}

	private void okButtonActionPerformed() {
		save();
		ok = true;
		setVisible(false);
	}

	protected void save() {
		if (grid.canUndo())
			grid.pushUndo(new CellUndoItem(grid, Messages
					.getString("CellPropertiesDlg.cell_prop"))); //$NON-NLS-1$
		for (Cell cell : cells)
			saveCellStyle(cell);
		getCellPropertiesPanel().apply();
	}

	protected void saveCellStyle(Cell cell) {
		CellStyle style = getModel().getStyles(cell.getStyleId());

		if (fontColorChange && getFontPanel().getFontColor() != null)
			style = style.deriveForeground(getFontPanel().getFontColor());

		int fontStyle = style.getStyle();

		if (fontUnderlineChange)
			fontStyle = getFontPanel().isUnderline() ? (fontStyle | CellStyle.UNDERLINE)
					: (fontStyle & ~CellStyle.UNDERLINE);

		if (fontStrikeChange)
			fontStyle = getFontPanel().isStrikethrough() ? (fontStyle | CellStyle.STRIKETHROUGH)
					: (fontStyle & ~CellStyle.STRIKETHROUGH);

		if (fontItalicChange)
			fontStyle = getFontPanel().isItalic() ? (fontStyle | CellStyle.ITALIC)
					: (fontStyle & ~CellStyle.ITALIC);

		if (fontBoldChange)
			fontStyle = getFontPanel().isBold() ? (fontStyle | CellStyle.BOLD)
					: (fontStyle & ~CellStyle.BOLD);

		style = style.deriveFont(fontStyle);

		if (fontFamilyChange && getFontPanel().getFontValue() != null)
			style = style.deriveFont(getFontPanel().getFontValue()
					.getFontName());

		if (fontSizeChange && getFontPanel().getFontValue() != null)
			style = style.deriveFont((float) getFontPanel().getFontValue()
					.getSize());

		if (hAlignChange) {
			int align = -1;
			switch (hAlignBox.getSelectedIndex()) {
			case 0:
				align = CellStyle.LEFT;
				break;
			case 1:
				align = CellStyle.CENTER;
				break;
			case 2:
				align = CellStyle.RIGHT;
				break;
			case 3:
				align = CellStyle.JUSTIFY;
				break;
			}
			if (align >= 0)
				style = style.deriveHAlign(align);
		}

		if (vAlignChange) {
			int align = -1;
			switch (vAlignBox.getSelectedIndex()) {
			case 0:
				align = CellStyle.TOP;
				break;
			case 1:
				align = CellStyle.CENTER;
				break;
			case 2:
				align = CellStyle.BOTTOM;
				break;
			}
			if (align >= 0)
				style = style.deriveVAlign(align);
		}

		if (rotateChange)
			style = style.deriveAngle(rotateChooser.getAngle());

		if (wrapLineChange)
			style = style.deriveWrapLine(wrapLineBox.isSelected());

		if (autoHeightChange)
			style = style.deriveAutoHeight(autoHeightBox.isSelected());

		if (carryChange && carryField.isVisible()) {
			style = style.deriveCarryRows((Integer) carryField.getValue());
		}

		if (lineSpacingChange)
			style = style.deriveLineSpacing((Float)lineSpacingField.getValue());

		cell.setStyleId(getModel().addStyle(style));

	}

	public void setCell(GridRect grid) {
		ok = false;
		this.selectedRect = grid;
		fillCells();
		updateComponents();
		if (cells.size() == 1) {
			setTitle(TITLE
					+ " (" + (grid.getTopRow() + 1) + "," + (grid.getLeftCol() + 1) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else
			setTitle(TITLE
					+ " [(" + (grid.getTopRow() + 1) + "," + (grid.getLeftCol() + 1) + ") : (" + (grid.getBottomRow() + 1) + "," + (grid.getRightCol() + 1) + ")]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		getCellPropertiesPanel().setCells(cells);
	}

	protected void updateComponents() {
		fontFamilyChange = false;
		fontSizeChange = false;
		fontColorChange = false;
		fontBoldChange = false;
		fontItalicChange = false;
		fontUnderlineChange = false;
		fontStrikeChange = false;
		hAlignChange = false;
		vAlignChange = false;
		rotateChange = false;
		wrapLineChange = false;
		autoHeightChange = false;
		carryChange = false;
		lineSpacingChange = false;

		CellStyle style = getModel().getStyles(cells.get(0).getStyleId());
		String fontFamily = "Default".equals(style.getFamily()) ? getFont().getFamily() : style.getFamily(); //$NON-NLS-1$
		if (cells.size() > 1) {
			String fn = style.getFamily();
			boolean equals = true;
			for (int i = 1; i < cells.size(); i++) {
				String otherFamily = getModel().getStyles(
						cells.get(i).getStyleId()).getFamily();
				if (!fn.equals(otherFamily)) {
					equals = false;
					break;
				}
			}
			fontPanel.setFontFamily(equals ? fontFamily : null);

			int size = style.getSize();
			equals = true;
			for (int i = 1; i < cells.size(); i++) {
				int otherSize = getModel().getStyles(cells.get(i).getStyleId())
						.getSize();
				if (size != otherSize) {
					equals = false;
					break;
				}
			}
			fontPanel.setFontSize(equals ? size : 0);

			Color color = style.getForegroundColor();
			equals = true;
			for (int i = 1; i < cells.size(); i++) {
				Color otherColor = getModel().getStyles(
						cells.get(i).getStyleId()).getForegroundColor();
				if (!color.equals(otherColor)) {
					equals = false;
					break;
				}
			}
			fontPanel.setFontColor(equals ? color : null);

			int fontStyle = style.getStyle();
			equals = true;
			for (int i = 1; i < cells.size(); i++) {
				int otherStyle = getModel()
						.getStyles(cells.get(i).getStyleId()).getStyle();
				if (fontStyle != otherStyle) {
					equals = false;
					break;
				}
			}
			fontPanel.setBold(equals && style.isBold());
			fontPanel.setItalic(equals ? style.isItalic() : false);
		} else {
			Font font = new Font(fontFamily, style.getStyle(), style.getSize());
			fontPanel.setFontValue(font, style.getForegroundColor());
			fontPanel.setUnderline(style.isUnderline());
			fontPanel.setStrikethrough(style.isStrikethrough());
		}
		
		lineSpacingField.setValue(style.getLineSpacing());

		if (cells.size() > 1) {
			hAlignBox.setSelectedIndex(-1);
			vAlignBox.setSelectedIndex(-1);
			rotateChooser.setAngle(0);
			wrapLineBox.setSelected(true);
			autoHeightBox.setSelected(false);
		} else {
			int index = 0;
			switch (style.getHorizontalAlignment()) {
			case CellStyle.LEFT:
				index = 0;
				break;
			case CellStyle.CENTER:
				index = 1;
				break;
			case CellStyle.RIGHT:
				index = 2;
				break;
			case CellStyle.JUSTIFY:
				index = 3;
				break;
			}
			hAlignBox.setSelectedIndex(index);

			switch (style.getVerticalAlignment()) {
			case CellStyle.TOP:
				index = 0;
				break;
			case CellStyle.CENTER:
				index = 1;
				break;
			case CellStyle.BOTTOM:
				index = 2;
				break;
			}
			vAlignBox.setSelectedIndex(index);

			rotateChooser.setAngle(style.getAngle());
			wrapLineBox.setSelected(style.isWrapLine());
			autoHeightBox.setSelected(style.isAutoHeight());
			carryField.setValue(style.getCarryRows());
		}
		carryField.setVisible(cells.size() == 1);
		carryLabel.setVisible(carryField.isVisible());
	}

	/**
	 * @return Returns the cells.
	 */
	public List<Cell> getCells() {
		return cells;
	}

	private JFontChooser getFontPanel() {
		if (fontPanel == null) {
			fontPanel = new JFontChooser();
			fontPanel.addPropertyChangeListener(this);
		}
		return fontPanel;
	}

	public boolean isOk() {
		return ok;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String pn = evt.getPropertyName();
		if (pn.equals("family")) {
			fontFamilyChange = true;
		} else if (pn.equals("size")) {
			fontSizeChange = true;
		} else if (pn.equals("color")) {
			fontColorChange = true;
		} else if (pn.equals("bold")) {
			fontBoldChange = true;
		} else if (pn.equals("italic")) {
			fontItalicChange = true;
		} else if (pn.equals("underline")) {
			fontUnderlineChange = true;
		} else if (pn.equals("strike")) {
			fontStrikeChange = true;
		} else if (pn.equals("angle")) {
			rotateChange = true;
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if ("ok".equals(e.getActionCommand())) { //$NON-NLS-1$
			okButtonActionPerformed();
		} else if ("cancel".equals(e.getActionCommand())) { //$NON-NLS-1$
			cancelButtonActionPerformed();
		} else if ("auto_height".equals(e.getActionCommand())) { //$NON-NLS-1$
			autoHeightChange = true;
		} else if ("wrap_line".equals(e.getActionCommand()))  { //$NON-NLS-1$
			wrapLineChange = true;
		}else if ("valign".equals(e.getActionCommand()))  { //$NON-NLS-1$
			vAlignChange = true;
		}else if ("halign".equals(e.getActionCommand()))  { //$NON-NLS-1$
			hAlignChange = true;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == lineSpacingField) {
			lineSpacingChange = true;
		} else if (e.getSource() == carryField) {
			carryChange = true;
		}
		
	}

}
